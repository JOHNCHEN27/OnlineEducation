package com.lncanswer.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author LNC
 * @version 1.0
 * @description 全局异常处理器
 * @date 2023/9/10 19:57
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 捕捉项目异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(OnlieEducationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(OnlieEducationException e) {
        log.error("【系统异常】{}",e.getErrMessage(),e);
        return new RestErrorResponse(e.getErrMessage());

    }

    /**
     * 捕捉全局异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {

        log.error("【系统异常】{}",e.getMessage(),e);
        //返回一个枚举类型的错误异常信息
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());

    }

}
