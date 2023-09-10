package com.lncanswer.base.exception;

import java.io.Serializable;

/**
 * @author LNC
 * @version 1.0
 * @description 错误响应参数包装 与前端约定返回异常信息的模型
 * @date 2023/9/10 19:56
 */
public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

}
