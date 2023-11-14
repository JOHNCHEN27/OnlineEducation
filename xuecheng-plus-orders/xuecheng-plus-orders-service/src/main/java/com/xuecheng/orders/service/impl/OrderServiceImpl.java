package com.xuecheng.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.OnlieEducationException;
import com.xuecheng.base.utils.IdWorkerUtils;
import com.xuecheng.base.utils.QRCodeUtil;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.orders.config.AlipayConfig;
import com.xuecheng.orders.config.PayNotifyConfig;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/11/10 19:17
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    XcOrdersMapper ordersMapper;

    @Autowired
    XcOrdersGoodsMapper ordersGoodsMapper;

    @Autowired
    XcPayRecordMapper payRecordMapper;

    @Value("${pay.qrcodeurl}")
    String qrcodeurl;

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * 生成商品订单
     * @param userId 用户id
     * @param addOrderDto 接受商品参数Dto
     * @return
     */
    @Override
    @Transactional  //开启事务控制 因为涉及多个表的操作，保证数据库事务的一致性
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {
        //添加商品订单
        XcOrders xcOrders = saveXcOrders(userId, addOrderDto);
        if (xcOrders == null){
            OnlieEducationException.cast("订单创建失败");
        }

        if (xcOrders.getStatus().equals("600002")){
            OnlieEducationException.cast("订单已支付");
        }

        //添加支付记录
        XcPayRecord payRecord = createPayRecord(xcOrders);
        Long payNo = payRecord.getPayNo();

        //生成二维码
        String url = String.format(qrcodeurl, payNo);
        QRCodeUtil qrCodeUtil = new QRCodeUtil();

        String qrCode = null;
        try {
             qrCode = qrCodeUtil.createQRCode(url, 200, 200);
        }catch (IOException e) {
            OnlieEducationException.cast("生成二维码出错");
        }

        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord,payRecordDto);
        payRecordDto.setQrcode(qrCode);
        return payRecordDto;
    }

    /**
     * 查询支付记录
     * @param payNo 交易记录号
     * @return
     */
    @Override
    public XcPayRecord getPayRecordByPayno(String payNo) {
        XcPayRecord xcPayRecord = payRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>()
                .eq(XcPayRecord::getPayNo, payNo));
        return xcPayRecord;
    }

    /**
     * 查询支付结果
     * @param payNo
     * @return
     */
    @Override
    public PayRecordDto queryPayResult(String payNo) {
        //根据支付记录号查询支付记录表数据
        XcPayRecord payRecord = getPayRecordByPayno(payNo);
        if (payRecord == null){
            OnlieEducationException.cast("请重新支付获取二维码");
        }
        //支付状态
        String status  =payRecord.getStatus();
        //支付成功直接返回
        if ("601002".equals(status)){
            PayRecordDto payRecordDto = new PayRecordDto();
            BeanUtils.copyProperties(payRecord,payRecordDto);
            return payRecordDto;
        }

        //从支付宝查询支付结果
        PayStatusDto payStatusDto = queryPayResultFromAlipay(payNo);
        //保存支付结果
        saveAliPayStatus(payStatusDto);
        //重新查询记录
        payRecord = getPayRecordByPayno(payNo);
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord,payRecordDto);
        return payRecordDto;
    }

    /**
     * 请求支付宝查询结果
     * @param payNo
     * @return
     */
    public PayStatusDto queryPayResultFromAlipay(String payNo){
        //获得初始化的client
        DefaultAlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, "json", AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no",payNo);
        request.setBizContent(jsonObject.toJSONString());
        AlipayTradeQueryResponse response =null;
        try {
            response = client.execute(request);
            if (!response.isSuccess()) {
                OnlieEducationException.cast("请求支付查询查询失败");
            }
        } catch (AlipayApiException e) {
            log.error("请求支付宝查询支付结果异常:{}", e.toString(), e);
            OnlieEducationException.cast("请求支付查询查询失败");
        }

        //获取支付结果
        String resultJson = response.getBody();
        //转map
        Map resultMap = JSON.parseObject(resultJson, Map.class);
        Map alipay_trade_query_response = (Map) resultMap.get("alipay_trade_query_response");
        //支付结果
        String trade_status = (String) alipay_trade_query_response.get("trade_status");
        String total_amount = (String) alipay_trade_query_response.get("total_amount");
        String trade_no = (String) alipay_trade_query_response.get("trade_no");
        //保存支付结果
        PayStatusDto payStatusDto = new PayStatusDto();
        payStatusDto.setOut_trade_no(payNo);
        payStatusDto.setTrade_status(trade_status);
        payStatusDto.setApp_id(APP_ID);
        payStatusDto.setTrade_no(trade_no);
        payStatusDto.setTotal_amount(total_amount);
        return payStatusDto;

    }

    /**
     * 保存支付宝支付结果
     * @param payStatusDto
     */
    @Transactional
    public void saveAliPayStatus(PayStatusDto payStatusDto){
        //支付流水号
        String payNo = payStatusDto.getOut_trade_no();
        XcPayRecord payRecord = getPayRecordByPayno(payNo);
        if (payRecord == null){
            OnlieEducationException.cast("支付记录找不到");
        }
        //支付结果
        String tradeStatus = payStatusDto.getTrade_status();
        log.info("收到支付结果：{},支付记录:{}",payStatusDto.toString(),payRecord.toString());
        if (tradeStatus.equals("TRADE_SUCCESS")){
            //支付金额变为分
            Float totakPrice = payRecord.getTotalPrice() * 100;
            Float total_amount = Float.parseFloat(payStatusDto.getTotal_amount())*100;
            //校验是否一致
            if (!payStatusDto.getApp_id().equals(APP_ID) || totakPrice.intValue() != total_amount.intValue()){
                //校验失败
                log.info("校验支付结果失败，支付记录:{},AppID:{},totalPrice:{}",payRecord.toString(),payStatusDto.getApp_id(),total_amount.intValue());
                OnlieEducationException.cast("校验支付结果失败");
            }
            log.info("更新支付结果，支付交易流水号:{},支付结果:{}",payNo,tradeStatus);
            XcPayRecord xcPayRecord = new XcPayRecord();
            xcPayRecord.setStatus("601002"); //支付成功
            xcPayRecord.setOutPayChannel("Alipay");
            xcPayRecord.setOutPayNo(payStatusDto.getTrade_no()); //支付宝交易号
            xcPayRecord.setPaySuccessTime(LocalDateTime.now());  //通知时间

            int update = payRecordMapper.update(xcPayRecord, new LambdaQueryWrapper<XcPayRecord>()
                    .eq(XcPayRecord::getPayNo, payNo));
            if (update > 0){
                log.info("更新支付记录状态成功:{}",xcPayRecord.toString());
            }else {
                log.info("更新支付记录状态失败:{}",xcPayRecord.toString());
                OnlieEducationException.cast("更新支付j记录状态失败");
            }
            //关联的订单号
            Long orderId = payRecord.getOrderId();
            XcOrders xcOrders = ordersMapper.selectById(orderId);
            if (xcOrders == null){
                log.info("找不到订单:{}",payRecord.toString());
                OnlieEducationException.cast("根据支付记录找不到订单");
            }
            XcOrders xcOrders1 = new XcOrders();
            xcOrders1.setStatus("600002"); //支付成功
            int update1 = ordersMapper.update(xcOrders1, new LambdaQueryWrapper<XcOrders>()
                    .eq(XcOrders::getId, orderId));
            if (update1 > 0){
                log.info("更新关联订单号状态成功,订单号：{}",orderId);
            }else {
                log.info("更新关联订单号状态失败,订单号:{}",orderId);
                OnlieEducationException.cast("更新订单表状态失败");
            }

            //保存消息记录,参数1：支付结果通知类型，2: 业务id，3:业务类型
            MqMessage mqMessage = mqMessageService.addMessage("payresult_notify", xcOrders1.getOutBusinessId(), xcOrders1.getOrderType(), null);
            //通知消息
            notifyPayResult(mqMessage);


        }

    }

    /**
     * 发送结果通知
     * @param message
     */
    @Override
    public void notifyPayResult(MqMessage message) {
        //消息体转JSON
        String msg = JSON.toJSONString(message);
        //设置消息持久化
        Message msgObj = MessageBuilder.withBody(msg.getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        //全局唯一消息ID 需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(message.getId().toString());
        //添加callback
        correlationData.getFuture().addCallback(
                result -> {
                    if(result.isAck()){
                        // 3.1.ack，消息成功
                        log.debug("通知支付结果消息发送成功, ID:{}", correlationData.getId());
                        //删除消息表中的记录
                        mqMessageService.completed(message.getId());
                    }else{
                        // 3.2.nack，消息失败
                        log.error("通知支付结果消息发送失败, ID:{}, 原因{}",correlationData.getId(), result.getReason());
                    }
                },
                ex -> log.error("消息发送异常, ID:{}, 原因{}",correlationData.getId(),ex.getMessage())
        );
        //发送消息
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_EXCHANGE_FANOUT, "", msgObj,correlationData);

    }

    //创建支付记录，添加支付记录
    public XcPayRecord createPayRecord(XcOrders orders){
        if (orders == null){
            OnlieEducationException.cast("订单不存在");
        }
        if (orders.getStatus().equals("600002")){
            OnlieEducationException.cast("订单已支付");
        }
        XcPayRecord payRecord = new XcPayRecord();
        //生成支付流水号用户和第三方平台交互
        long payNo = IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo);
        payRecord.setOrderId(orders.getId()); //关联商品订单id
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCreateDate(LocalDateTime.now());
        payRecord.setCurrency("CNY");
        payRecord.setStatus("601001"); //未支付
        payRecord.setUserId(orders.getUserId());
        payRecordMapper.insert(payRecord);
        return payRecord;

    }

    @Transactional
    public XcOrders saveXcOrders(String userId, AddOrderDto addOrderDto){
        //判断商品订单是否存在
        XcOrders orders = getOrdersByBusinessId(addOrderDto.getOutBusinessId());
        if (orders !=null){
            return orders;
        }

        //订单不存在，创建商品订单
        orders = new XcOrders();
        //利用工具类雪花算法生成订单号
        long orderId = IdWorkerUtils.getInstance().nextId();
        orders.setId(orderId);
        orders.setTotalPrice(addOrderDto.getTotalPrice());
        orders.setCreateDate(LocalDateTime.now());
        orders.setStatus("600001"); //未支付
        orders.setUserId(userId);
        orders.setOrderType(addOrderDto.getOrderType());
        orders.setOrderName(addOrderDto.getOrderName());
        orders.setOrderDetail(addOrderDto.getOrderDetail());
        orders.setOrderDescrip(addOrderDto.getOrderDescrip());
        orders.setOutBusinessId(addOrderDto.getOutBusinessId());//选课记录id
        ordersMapper.insert(orders);

        //将订单明细信息存入订单明细表
        String orderDetailJson = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoodsList = JSON.parseArray(orderDetailJson, XcOrdersGoods.class);
        xcOrdersGoodsList.stream().forEach(good ->{
            XcOrdersGoods xcOrdersGoods = new XcOrdersGoods();
            BeanUtils.copyProperties(good,xcOrdersGoods);
            xcOrdersGoods.setOrderId(orderId);
            ordersGoodsMapper.insert(xcOrdersGoods);
        });
        return orders;
    }

    //根据业务id查询订单
    public XcOrders getOrdersByBusinessId(String businessId){
        XcOrders xcOrders = ordersMapper.selectOne(new LambdaQueryWrapper<XcOrders>().eq(
                XcOrders::getOutBusinessId, businessId));
        return xcOrders;
    }
}
