package com.xuecheng.orders.service;

import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;

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
}
