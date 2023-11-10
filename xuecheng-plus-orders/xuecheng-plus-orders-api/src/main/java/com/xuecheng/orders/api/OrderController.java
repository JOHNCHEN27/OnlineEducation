package com.xuecheng.orders.api;

import com.xuecheng.orders.model.dto.AddOrderDto;
import com.xuecheng.orders.model.dto.PayRecordDto;
import com.xuecheng.orders.service.OrderService;
import com.xuecheng.orders.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/11/10 19:11
 */
@Api(value = "订单支付接口",tags = "订单支付接口")
@RestController
@Slf4j
public class OrderController {

    @Autowired
    OrderService orderService;

    /**
     * 生成二维码
     * @param addOrderDto
     * @return
     */
    @ApiOperation("生成支付二维码")
    @PostMapping("/generatepaycode")
    public PayRecordDto generatePayCode(@RequestBody  AddOrderDto addOrderDto){
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String userId = user.getId();

        //调用service 完成插入订单信息，插入支付记录、生成支付二维码
        PayRecordDto order = orderService.createOrder(userId, addOrderDto);

        return order;
    }

    @ApiOperation("扫码下单接口")
    @GetMapping("/requestpay")
    public void requestPay(String payNo, HttpServletResponse response) throws IOException {


    }


}
