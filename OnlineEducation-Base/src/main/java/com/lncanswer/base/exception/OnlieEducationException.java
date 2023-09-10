package com.lncanswer.base.exception;

/**
 * @author LNC
 * @version 1.0
 * @description 项目业务异常
 * @date 2023/9/10 19:52
 */
public class OnlieEducationException extends RuntimeException{
    /**
     * 项目业务的异常类 定义自己的异常类 继承Runtime运行异常 重写有参、无参构造方法
     * 抛出异常时如果是业务异常直接抛出自己定义的业务异常
     * 默认是交给SpringMVC框架自动处理异常
     */
    //与前端约定好的异常信息名字
    private String errMessage;

    public OnlieEducationException() {
    }

    public OnlieEducationException(String message) {
        super(message);
        this.errMessage = message;
    }

    public static void cast(CommonError commonError){
        throw new OnlieEducationException(commonError.getErrMessage());
    }
    //用来接受自定义异常类的错误信息
    public static void cast(String errMessage){
        throw new OnlieEducationException(errMessage);
    }


    public String getErrMessage() {
        return this.errMessage;
    }
}

