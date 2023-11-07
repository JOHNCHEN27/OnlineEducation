package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

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
        //捕获SpringSecurity产生的无法访问问题
        if (e.getMessage().equals("不允许访问")){
            return new RestErrorResponse("没有操作此功能的权限");
        }
        //返回一个枚举类型的错误异常信息
        return new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
    }

    /**
     * 当controller层参数校验出错spring会抛出MethodArgumentNotValidException异常
     * 需要在统一异常处理器捕获异常 解析出异常信息
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e){
        //捕捉的controller层接受参数异常 放到bindingResult中
        BindingResult bindingResult = e.getBindingResult();
        List<String> msgList = new ArrayList<>();
        //将错误信息放到msgList
        bindingResult.getFieldErrors().stream().forEach(item->{
            msgList.add(item.getDefaultMessage());
        });
        //拼接错误信息
        String msg = StringUtils.join(msgList,",");
        log.error("系统异常：{}",msg);
        return new RestErrorResponse(msg);
    }


}
