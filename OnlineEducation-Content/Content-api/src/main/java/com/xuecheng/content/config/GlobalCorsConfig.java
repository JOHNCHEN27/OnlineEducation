package com.xuecheng.content.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author LNC
 * @version 1.0
 * @description  解决前端工程访问的跨域请求
 * @date 2023/9/7 9:53
 */
@Configuration
public class GlobalCorsConfig {

    /**
     * 允许跨域调用的过滤器 实现跨域过滤器
     * 跨域发送在端口、ip、协议不同去访问服务端，这三个有一个不同就是跨域
     * 服务端之间不存在跨域  可以通过nginx来解决跨域问题
     */
    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration config = new CorsConfiguration();
        //允许白名单域名进行跨域调用
        config.addAllowedOrigin("*");
        //允许跨域发送cookie
        config.setAllowCredentials(true);
        //放行全部原始头信息
        config.addAllowedHeader("*");
        //允许所有请求方法跨域调用
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",config);

        return new CorsFilter(source);
    }


}
