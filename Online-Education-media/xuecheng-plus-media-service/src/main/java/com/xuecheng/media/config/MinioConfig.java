package com.xuecheng.media.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author LNC
 * @version 1.0
 * @description Minio读取配置
 * @date 2023/10/1 11:24
 */
@Configuration
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    //使用ConfigurationProperties指定配置文件，进行属性注入，需要保证属性名一直
    private String endpoint;

    private String accesskey;

    private String secretkey;

    //获取用户认证委托注册为Bean
    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accesskey, secretkey)
                .build();
        return minioClient;
    }

}
