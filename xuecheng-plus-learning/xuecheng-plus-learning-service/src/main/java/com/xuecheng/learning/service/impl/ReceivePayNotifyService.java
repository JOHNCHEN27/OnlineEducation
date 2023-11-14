package com.xuecheng.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.OnlieEducationException;
import com.xuecheng.learning.config.PayNotifyConfig;
import com.xuecheng.learning.service.MyCourseTablesService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.channels.Channel;

/**
 * @author LNC
 * @version 1.0
 * @description 接受支付结果通知
 * @date 2023/11/14 9:35
 */
@Service
@Slf4j
public class ReceivePayNotifyService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    MyCourseTablesService myCourseTablesService;

    //监听消息队列接受支付结果通知
    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    public void receive(Message message, Channel channel){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //获取消息
        MqMessage mqMessage = JSON.parseObject(message.getBody(), MqMessage.class);
        log.info("学习服务中心接受到支付结果：{}",mqMessage);

        //消息类型
        String messageType = mqMessage.getMessageType();
        //订单类型 60201表示购买课程
        String businessKey2 = mqMessage.getBusinessKey2();
        //处理支付结果通知
        if (PayNotifyConfig.MESSAGE_TYPE.equals(messageType) && "60201".equals(businessKey2)){
            //选课记录id
            String chooseCourseId = mqMessage.getBusinessKey1();
            //添加选课
            boolean b = myCourseTablesService.saveChooseCourseSuccess(chooseCourseId);
            if (!b){
                OnlieEducationException.cast("收到支付结果，添加选课失败");
            }
        }
    }

}
