package com.xuecheng.base.result;

import lombok.Data;

/**
 * @author LNC
 * @version 1.0
 * @description 返回结果类
 * @date 2023/9/14 20:12
 */
@Data
public class ResultClass {
    private Integer code = 200;
    private String errMessage;
    private Object data;

    //默认返回成功结果
    public static final ResultClass success(){
        return new ResultClass();
    }
    //返回成功结果，并且返回数据
    public static final ResultClass success(String msg ,Object data){
       ResultClass resultClass= new ResultClass();
       resultClass.setErrMessage(msg);
       resultClass.setData(data);
       return resultClass;
    }
    public static final ResultClass success(Object data){
        ResultClass resultClass= new ResultClass();
        resultClass.setData(data);
        return resultClass;
    }

    //失败返回错误信息
    public static final ResultClass error(String msg){
        ResultClass resultClass = new ResultClass();
        resultClass.setCode(120409);
        resultClass.setErrMessage(msg);
        return resultClass;
    }
}
