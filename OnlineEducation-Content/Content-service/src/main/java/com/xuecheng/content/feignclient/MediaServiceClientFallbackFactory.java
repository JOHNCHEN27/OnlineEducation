package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author LNC
 * @version 1.0
 * @description 熔断降级反馈
 * @date 2023/10/20 14:00
 */
@Component
@Slf4j
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {

    @Override
    public MediaServiceClient create(Throwable throwable) {
        return new MediaServiceClient() {
            @Override
            public String uploadFile(MultipartFile upload, String folder, String objectName) {
                //降级方法：自行采取策略
                log.debug("调用媒资管理服务上传文件时发生熔断，异常信息:{}",throwable.toString(),throwable);
                //返回类型由自己定义，当返回这个值时表示微服务之间发生了熔断
                return null;
            }
        };
    }
}
