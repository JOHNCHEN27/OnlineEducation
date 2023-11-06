package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.po.XcUser;

/**
 * @author LNC
 * @version 1.0
 * @description  微信认证接口
 * @date 2023/11/6 10:52
 */
public interface WxAuthService {

    //申请令牌查询扫码用户信息
    public XcUser wxAuth(String code);
}
