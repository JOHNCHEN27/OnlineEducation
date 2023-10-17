package com.xuecheng.content.service.jobhandler;

import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author LNC
 * @version 1.0
 * @description 课程发布任务处理
 * @date 2023/10/17 12:20
 */
@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {


    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler (){
        //分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.info("shardIndex:{},shardTotal:{}",shardIndex,shardTotal);
        process(shardIndex,shardTotal,"course_publish",30,60);
    }


    /**
     * 重写excute执行方法
     * @param mqMessage 执行任务内容
     * @return
     */
    @Override
    public boolean execute(MqMessage mqMessage) {
        //获取消息的业务信息
        String businessKey1 = mqMessage.getBusinessKey1();
        long courseId = Integer.parseInt(businessKey1);
        //课程静态化 上传至minio
        generateCourseHtml(mqMessage,courseId);

        //将课程信息保存到redis
        saveCourseCache(mqMessage,courseId);

        //将课程信息保存到索引库
        saveCourseIndex(mqMessage,courseId);
        return false;
    }

    //将课程信息保存到索引库
    private void saveCourseIndex(MqMessage mqMessage, long courseId) {
        log.info("保存课程索引信息,课程id:{}",courseId);
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageThree = mqMessageService.getStageThree(id);
        if (stageThree>0){
            log.info("课程信息已经保存到索引库,课程id:{}",courseId);
            return;
        }
        //将课程信息保存到索引库 TODO

        //设置消息表课程信息状态已保存
        mqMessageService.completedStageThree(id);
    }

    //将课程信息保存到redis
    private void saveCourseCache(MqMessage mqMessage, long courseId) {
        log.info("将课程信息保存到redis，课程id:{}",courseId);
        //判断幂等性
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageTwo = mqMessageService.getStageTwo(id);
        if (stageTwo >0){
            log.info("课程信息已经缓冲到redis,课程id:{}",courseId);
            return;
        }
        //将课程信息存储到redis TODO

        //设置完成redis缓冲信息
        mqMessageService.completedStageTwo(id);
    }

    /**
     * 生成课程静态页面并上传到文件系统
     * @param mqMessage
     * @param courseId
     */
    private void generateCourseHtml(MqMessage mqMessage, long courseId) {
        //拿到消息id
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息幂等性处理
        int stageOne = mqMessageService.getStageOne(id);
        if (stageOne > 0){
            log.info("课程静态化页面已经完成，课程id：{}",courseId);
            return;
        }
        //将课程信息进行静态化存储到minio TODO

        mqMessageService.completedStageOne(id);

    }
}
