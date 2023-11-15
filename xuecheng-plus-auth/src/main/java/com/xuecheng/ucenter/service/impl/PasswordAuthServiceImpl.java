package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author LNC
 * @version 1.0
 * @description 账号密码认证
 * @date 2023/10/25 17:48
 */
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {
    @Autowired
    XcUserMapper xcUserMapper;

    //密码编码方式注入
    @Autowired
    PasswordEncoder passwordEn;

    @Autowired
    CheckCodeClient checkCodeClient;

    //执行认证方法
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //验证校验码
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();

        if (StringUtils.isBlank(checkcodekey) || StringUtils.isBlank(checkcode)){
            throw new RuntimeException("验证码为空");
        }

        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (!verify){
            throw new RuntimeException("验证码输入错误");
        }

        //获取用户名
        String username = authParamsDto.getUsername();
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (user == null){
            //为空说明用户不存在返回null抛出异常 ，springsecurity会认为是验证失败
            throw new RuntimeException("账号不存在");
        }
        //查询到用户则进行封装
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user,xcUserExt);
        //检验用户的数据库密码
        String password = user.getPassword();
        String authParamsDtoPassword = authParamsDto.getPassword();
       // System.out.println("authpassword = " +authParamsDtoPassword );

        boolean matches = passwordEn.matches(authParamsDtoPassword,password);

       // System.out.println("password = " +  password+ "authpassword = " +authParamsDtoPassword );
        System.out.println(matches);
        if (!matches){
            throw new RuntimeException("账号或密码错误");
        }
        return xcUserExt;
    }
}
