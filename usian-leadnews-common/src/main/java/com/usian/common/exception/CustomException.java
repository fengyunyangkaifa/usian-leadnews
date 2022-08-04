package com.usian.common.exception;

import com.usian.model.common.dtos.ResponseResult;
//  自定义异常
public class CustomException extends RuntimeException{

    private ResponseResult responseResult;

    public CustomException(int code,String msg){
        this.responseResult=new ResponseResult(code,msg);
    }

    public ResponseResult getResponseResult() {
        return responseResult;
    }
}
