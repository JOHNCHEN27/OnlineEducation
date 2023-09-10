package com.xuecheng.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author LNC
 * @version 1.0
 * @description 解决跨域问题
 * @date 2023/9/10 10:00
 */
@Configuration
public class GlovalCorsConfig {

    /**
     * 创建跨域过滤器  配置过滤规则、配置路径
     * @return
     */
    @Bean
    public CorsFilter corsFilter (){
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOrigin("*");
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        //允许跨域携带cookie
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",cors);
        return new CorsFilter(source);
    }
}
