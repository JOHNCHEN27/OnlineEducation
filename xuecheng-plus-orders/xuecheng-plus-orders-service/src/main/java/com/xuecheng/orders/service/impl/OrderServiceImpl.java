package com.xuecheng.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.OnlieEducationException;
import com.xuecheng.base.utils.IdWorkerUtils;
import com.xuecheng.base.utils.QRCodeUtil;
import com.xuecheng.orders.mapper.XcOrdersGoodsMapper;
import com.xuecheng.orders.mapper.XcOrdersMapper;
import com.xuecheng.orders.mapper.XcPayRecordMapper;
import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.po.XcOrders;
import com.xuecheng.orders.model.po.XcOrdersGoods;
import com.xuecheng.orders.model.po.XcPayRecord;
import com.xuecheng.orders.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/11/10 19:17
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    XcOrdersMapper ordersMapper;

    @Autowired
    XcOrdersGoodsMapper ordersGoodsMapper;

    @Autowired
    XcPayRecordMapper payRecordMapper;

    @Value("${pay.qrcodeurl}")
    String qrcodeurl;


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
