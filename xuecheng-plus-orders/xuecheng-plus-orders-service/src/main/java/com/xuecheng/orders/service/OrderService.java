package com.xuecheng.orders.service;

import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.model.dto.PayStatusDto;
import com.xuecheng.orders.model.po.XcPayRecord;

/**
 * @author LNC
 * @version 1.0
 * @description 订单相关接口
 * @date 2023/11/10 19:17
 */
public interface OrderService {

    /**
     * 根据用户id 添加用户商品订单
     * @param userId
     * @param addOrderDto
     * @return
     */
    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * 查询支付记录
     * @param payNo 交易记录号
     * @return
     */
    XcPayRecord getPayRecordByPayno(String payNo);


    /**
     * 查询支付结果
     * @param payNo
     * @return
     */
    public PayRecordDto queryPayResult(String payNo);

    /**
     * 保存支付宝支付结果
     * @param payStatusDto 支付结果信息
     */
    public void saveAliPayStatus(PayStatusDto payStatusDto);
}
