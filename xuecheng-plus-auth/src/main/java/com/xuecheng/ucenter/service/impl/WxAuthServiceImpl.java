package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.AuthService;
import com.xuecheng.ucenter.service.WxAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author LNC
 * @version 1.0
 * @description wx认证
 * @date 2023/10/25 17:49
 */
@Slf4j
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService, WxAuthService {

    //本类的代理对象
    @Autowired
    WxAuthServiceImpl currentProxy;

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    XcUserRoleMapper xcUserRoleMapper;

    //申请的appid和密钥
    @Value("${weixin.appid}")
    String appid;

    @Value("${weixin.secret}")
    String secret;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        String username = authParamsDto.getUsername();
        //根据传进来的用户去数据库里面查询
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (user == null){
            throw new RuntimeException("账号不存在");
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user,xcUserExt);
        return xcUserExt;
    }

    /**
     * 根据 WX授权码申请令牌获取用户信息
     * @param code
     * @return
     */
    @Override
    public XcUser wxAuth(String code) {
        //调用微信接口得到返回的令牌JSON数据
        Map<String, String> accessTokenMap = getAccessToken(code);
        if (accessTokenMap == null){
            return null;
        }

        //获取返回结果map中令牌和openid（相当于每个用户的id）
        String openid = accessTokenMap.get("openid");
        String accessToken = accessTokenMap.get("access_token");

        //用令牌和用户openid去WX查找用户信息并返回
        Map<String, String> userinfo = getUserinfo(accessToken, openid);
        if (userinfo == null){
            return null;
        }
        //添加用户到数据库
        XcUser user = currentProxy.addWxUser(userinfo);
        return user;



    }


    /**
     * 申请访问令牌,响应示例
     {
     "access_token":"ACCESS_TOKEN",
     "expires_in":7200,
     "refresh_token":"REFRESH_TOKEN",
     "openid":"OPENID",
     "scope":"SCOPE",
     "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     }
     */

    private Map<String,String> getAccessToken(String code){
        String wxUrlTemplate = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        //请求微信地址
        String wxUrl = String.format(wxUrlTemplate,appid,secret,code);
        log.info("调用微信接口申请访问令牌TokenUrl: {}",wxUrl);
        ResponseEntity<String> exchange = restTemplate.exchange(wxUrl, HttpMethod.POST, null, String.class);
        String result = exchange.getBody();

        log.info("调用微信接口申请令牌返回值：{}",result);

        //将返回的结果转换的JSON格式的map键值对
        Map<String,String> resultMap = JSON.parseObject(result, Map.class);


        return resultMap;
    }


    /**
     * 根据令牌和用户标识从WX数据库中查找数据
     * @param access_token 用户令牌
     * @param openid 用户标识
     * @return
     * **获取用户信息，示例如下：
     *  {
     *  "openid":"OPENID",
     *  "nickname":"NICKNAME",
     *  "sex":1,
     *  "province":"PROVINCE",
     *  "city":"CITY",
     *  "country":"COUNTRY",
     *  "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     *  "privilege":[
     *  "PRIVILEGE1",
     *  "PRIVILEGE2"
     *  ],
     *  "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     *  }
     */
    private Map<String,String> getUserinfo(String access_token,String openid){

        String wxUrlTamplate = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        //填充数据
        String wxUrl = String.format(wxUrlTamplate,access_token,openid);

        ResponseEntity<String> exchange = restTemplate.exchange(wxUrl, HttpMethod.POST, null, String.class);

        //防止乱码进行转码
        String result = new String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

        //转换为JSON对象
        Map<String,String> resultMap = JSON.parseObject(result, Map.class);
        return resultMap;
    }


    /**
     * 将wx获取的用户信息保存到数据库
     * @param userinfoMap
     * @return
     */
    @Transactional
    public XcUser addWxUser(Map userinfoMap){

        String unionId = userinfoMap.get("unionid").toString();

        //根据unionid查询数据库
        XcUser xcUser =xcUserMapper.selectOne( new LambdaQueryWrapper<XcUser>()
                .eq(XcUser::getWxUnionid,unionId));
        if (xcUser != null){
            return xcUser;
        }

        //如果数据库不存在，将其添加到数据库中
        String userId = UUID.randomUUID().toString();
        xcUser =  new XcUser();
        xcUser.setId(userId);
        xcUser.setWxUnionid(unionId);
        //记录从wx获取的昵称
        xcUser.setNickname(userinfoMap.get("nickname").toString());
        xcUser.setUserpic(userinfoMap.get("headimgurl").toString());
        xcUser.setName(userinfoMap.get("nickname").toString());
        xcUser.setUsername(unionId);
        xcUser.setPassword(unionId);
        xcUser.setUtype("101001");  //学生类型
        xcUser.setStatus("1");   //用户状态
        xcUser.setCreateTime(LocalDateTime.now());
        //插入数据库
        int insert = xcUserMapper.insert(xcUser);
        if (insert != 0){
            log.info("添加用户到数据库成功");
        }

        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17"); //学生角色
        xcUserRoleMapper.insert(xcUserRole);

        return xcUser;

    }
}
