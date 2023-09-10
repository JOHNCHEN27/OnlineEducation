package com.lncanswer.base.exception;

/**
 * @author LNC
 * @version 1.0
 * @description 描述通用错误信息
 * @date 2023/9/10 19:50
 */
public enum CommonError {
    /**
     * 普通异常类 枚举类型，是一个通用的异常类
     */
    UNKOWN_ERROR("执行过程异常，请重试。"),
    PARAMS_ERROR("非法参数"),
    OBJECT_NULL("对象为空"),
    QUERY_NULL("查询结果为空"),
    REQUEST_NULL("请求参数为空");

    private String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    private CommonError( String errMessage) {
        this.errMessage = errMessage;
    }


}
