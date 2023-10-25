package com.xuecheng.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author LNC
 * @version 1.0
 * @description feign反馈工厂
 * @date 2023/10/25 19:39
 */
@Slf4j
@Component
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {
    @Override
    public CheckCodeClient create(Throwable cause) {
        return new CheckCodeClient() {
            @Override
            public Boolean verify(String key, String code) {
                log.info("调用验证码服务熔断异常:{}",cause.getMessage());
                return null;
            }
        };
    }
}
