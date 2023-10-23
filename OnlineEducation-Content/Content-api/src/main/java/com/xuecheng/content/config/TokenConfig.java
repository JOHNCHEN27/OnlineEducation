package com.xuecheng.content.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author LNC
 * @version 1.0
 * @description 令牌管理
 * @date 2023/10/22 20:02
 */
@Configuration
public class TokenConfig {
    //jwt签名密钥,与认证服务保持一致
    private String SIGNING_KEY = "mq123";

    @Bean
    public TokenStore tokenStore(){
        //JWT令牌存储方案
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter (){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(SIGNING_KEY);
        return jwtAccessTokenConverter;
    }

}
