package com.xuecheng.media.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LNC
 * @version 1.0
 * @description Minio读取配置
 * @date 2023/10/1 11:24
 */
@Configuration
public class MinioConfig {
    //使用Value指定配置文件，进行属性注入，需要保证属性名一直
    //@Value("${minio.endpoint}")
    private String endpoint = "http://47.113.185.5:9000";

    //@Value("${minio.accessKey}")
    private String accessKey = "@lncminio007";

    //@Value("${minio.secretKey}")
    private String secretKey = "@lncminio007";

    //获取用户认证委托注册为Bean
    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        return minioClient;
    }

}
