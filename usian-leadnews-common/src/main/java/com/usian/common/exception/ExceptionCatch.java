package com.usian.common.exception;

import com.google.common.collect.ImmutableMap;
import com.usian.model.common.dtos.ResponseResult;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice   //   controller 异常声明类   控制器增强注解
@Log4j2    //  日志打印
public class ExceptionCatch {
//    抓取异常可预知（可自定义）   区分可预知异常/不可预知异常
    @ExceptionHandler(CustomException.class)    //异常处理器  拦截指定的异常信息
    @ResponseBody           // view视图异常解析转换json
    public ResponseResult exception(CustomException exception){  //  自定义异常类
        return exception.getResponseResult();
    }
    //使用EXCEPTIONS存放异常类型和错误代码的映射，ImmutableMap的特点的一旦创建不可改变，并且线程安全
    private static ImmutableMap<Class<? extends Throwable>, ResponseResult> EXCEPTIONS;
    //使用builder来构建一个异常类型和错误代码的异常
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResponseResult> builder = ImmutableMap.builder();
    //捕获Exception此类异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception) {
        //记录日志
        exception.printStackTrace(); //  打印异常信息
        log.error("捕捉到的异常==>", exception.getMessage());
        if(EXCEPTIONS == null){
            EXCEPTIONS = builder.build(); //构建初始化
        }
//         直接取出异常
        ResponseResult result = EXCEPTIONS.get(exception.getClass());
        if (result != null) {
            //如果匹配了异常  直接返回对应异常提示
            return result;
        } else {
            // 代表异常没有，返回统一异常码
            return ResponseResult.errorResult(99999,"出现未知异常");
        }
    }
    static{
        //在这里加入一些基础的异常类型判断
        builder.put(HttpMessageNotReadableException.class,ResponseResult.errorResult( 1,"非法参数"));
        builder.put(NullPointerException.class,ResponseResult.errorResult(1,"出现空对象异常"));
        builder.put(IndexOutOfBoundsException.class,ResponseResult.errorResult(1,"下角标越界异常"));
        //自己填充的
     //   builder.put(ArithmeticException.class,ResponseResult.errorResult(1,"除数不能为0"));
    }
}
