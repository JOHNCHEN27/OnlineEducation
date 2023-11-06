package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
 * @author LNC
 * @version 1.0
 * @description 认证Service
 * @date 2023/10/25 16:29
 */
public interface AuthService {
    /**
     * 自定义认证方法
     * @param authParamsDto 认证参数
     * @return
     */
    XcUserExt execute(AuthParamsDto authParamsDto);
}
