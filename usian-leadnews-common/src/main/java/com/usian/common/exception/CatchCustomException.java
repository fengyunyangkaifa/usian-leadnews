package com.usian.common.exception;


public class CatchCustomException {

//      构造一个静态方法
    public static void catchs(Integer code, String msg){
        throw new CustomException(2000,"id为空值");
    }
}
