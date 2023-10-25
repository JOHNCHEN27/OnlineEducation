package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author LNC
 * @version 1.0
 * @description 账号密码验证(不需要验证码方式)
 * @date 2023/10/25 19:52
 */
@Service
public class AuthService implements com.xuecheng.ucenter.service.AuthService {

    @Autowired
    XcUserMapper userMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //获取用户名
        String username = authParamsDto.getUsername();

        XcUser user = userMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (user == null){
            throw new RuntimeException("用户不存在");
        }
        //将用户封装成扩展对象Xuser
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user,xcUserExt);

        //检验密码是否正确
        String password = user.getPassword();
        String paramsPassword = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(paramsPassword, password);
        if (!matches){
            throw new RuntimeException("输入密码错误");
        }
        return xcUserExt;
    }
}
