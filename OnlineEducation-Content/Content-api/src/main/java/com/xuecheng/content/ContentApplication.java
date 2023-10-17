package com.xuecheng.content;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author LNC
 * @version 1.0
 * @date 2023/9/5 14:05
 */
@SpringBootApplication  //启动类
@EnableSwagger2Doc //swagger注解 根据接口生成API文档
@ComponentScan(value = {"com.xuecheng.content","com.xuecheng.messagesdk"})
public class ContentApplication {
    public static void  main(String[] args){
        SpringApplication.run(ContentApplication.class,args);
    }
}
