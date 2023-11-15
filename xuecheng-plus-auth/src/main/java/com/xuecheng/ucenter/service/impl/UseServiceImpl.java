package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @description   自定义UserDetailsService用来对接Spring Security
 * @date 2023/10/25 15:11
 */
@Slf4j
@Service
public class UseServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper ;

    //注入容器对象来获取Bean
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    XcMenuMapper xcMenuMapper;


    /**
     * 根据账号查询用户信息
     * @param s the username identifying the user whose data is required.
     *
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto = null;
        try{
            //将认证参数转为自定义认证参数类型
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);

        }catch (Exception e){
            log.info("认证请求不符合项目要求：{}",s);
            throw  new RuntimeException("认证请求数据格式不对");
        }
        //获取认证类型
        String authType = authParamsDto.getAuthType();
        //用指定类型从容器中获取指定的bean对象
        String beanName = authType+ "_authservice";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        //开始认证
        XcUserExt user = authService.execute(authParamsDto);

        return getUserDetails(user);


//        //从自定义的认证对象中拿到账号username
//        String username = authParamsDto.getUsername();
//
//        //从数据库中查询用户信息
//        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
//        if (user ==null){
//            //查询的用户为空 返回null  SpringSecurity框架会认为用户验证失败
//            return null;
//        }
    }

    /**
     * 查询用户信息封装为UserDetails对象返回
     * @param user
     * @return
     */
    private  UserDetails getUserDetails(XcUserExt user) {
        //查询成功，取出数据库存储的正确密码
        String password = user.getPassword();
        //用户权限，如果不加报错：Cannot pass a null GrantedAuthority collection
        String [] authorities = {"p1"};

        List<String> permissions = new ArrayList<>();
        //查询用户权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(user.getId());
        if (xcMenus.size() > 0){
            xcMenus.stream().forEach(menu ->{
                permissions.add(menu.getCode());
            });
        }
        //将查询到的权限赋值给权限数组
        authorities = permissions.toArray(new String[0]);
        //将权限放在xcUser对象中
        user.setPermissions(permissions);


        //在UserDetails中username中扩展用户的信息，使其能够转入更多用户信息
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转为JSON
        String userJSON = JSON.toJSONString(user);
        //将转换后的userJSON格式作为 UserDetails的username属性值
        //创建UserDetails对象，权限信息之后加入 UserDetails是一个接口 创建实现类对象
        UserDetails userDetails = User.withUsername(userJSON).password(password).authorities(authorities).build();

        return userDetails;
    }
}
