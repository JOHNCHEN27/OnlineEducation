package com.xuecheng.media.service.jobhandler;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author LNC
 * @version 1.0
 * @description
 * XxlJob开发示例（Bean模式）
 *  开发步骤：
 *       1、任务开发：在Spring Bean实例中，开发Job方法；
 *       2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 *       3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 *       4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @date 2023/10/11 18:42
 */
@Component
public class SampleXxlJob {
        private static Logger logger = LoggerFactory.getLogger(SampleXxlJob.class);

    /**
     * 测试Xxl-job任务调度
     * @XxlJob 绑定任务名称
     */
    @XxlJob("testJobHandler")
    public void testJobHandler() throws Exception  {
        System.out.println("处理媒资视频");
       // XxlJobHelper.log("处理媒资视频");
    }


    }

