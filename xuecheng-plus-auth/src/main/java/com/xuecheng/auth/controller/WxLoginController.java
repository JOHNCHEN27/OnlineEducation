package com.xuecheng.auth.controller;

import com.xuecheng.ucenter.model.po.XcUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * @author LNC
 * @version 1.0
 * @description    请求获取wx授权码接口
 * @date 2023/11/6 8:45
 */
@Controller
@Slf4j
public class WxLoginController {

    @RequestMapping("/wxLogin")
    public String wxLogin(String code, String state) throws IOException{
        log.info("微信扫码回调,code: {},state:{}", code ,state);
        //请求微信申请令牌，拿到令牌查询用户信息，将用户信息写入本项目数据库

        //暂时硬编码
        XcUser xcUser = new XcUser();
        xcUser.setUsername("t1");

        if (xcUser == null){
            return "redirect:http://www.51xuecheng.cn/error.html";
        }
        String username = xcUser.getUsername();
        return "redirect:http://www.51xuecheng.cn/sign.html?username="+username+"&authType=wx";
    }
}
