package com.xuecheng;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.po.XcUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author LNC
 * @version 1.0
 * @description
 * @date 2023/11/7 16:06
 */
@SpringBootTest
public class SpringSecurityTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    XcUserMapper xcUserMapper;

    //测试PassWordEncoder加密解密
    @Test
    public void testPasswordEncoder(){
        String password = "123456";

        String encode = passwordEncoder.encode(password);

        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, "t1"));
        if (user !=null){
            user.setPassword(encode);
            int i = xcUserMapper.updateById(user);
            if (i >0){
                System.out.println("更新成功");
            }
        }

        boolean matches = passwordEncoder.matches(password, encode);
        System.out.println(matches);
    }

    @Test
    public void testUpdateGrant(){
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(
                XcUser::getName, "JohnChen"));
        if (user != null){
            String encode = passwordEncoder.encode("lnc007");
            user.setUsername("John");
            user.setPassword(encode);
            int i = xcUserMapper.updateById(user);
            if (i>0){
                System.out.println("更新成功");
            }
        }
    }
}
