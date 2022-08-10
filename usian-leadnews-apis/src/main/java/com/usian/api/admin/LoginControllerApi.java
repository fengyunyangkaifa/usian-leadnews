package com.usian.api.admin;

import com.usian.model.admin.dtos.AdUserDto;
import com.usian.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestBody;

@Api(value = "登录认证管理", tags = "AdUserDto", description = "登录认证API")
public interface LoginControllerApi {
    /**
     * admin登录功能
     * @param dto
     * @return
     */
    public ResponseResult login(@RequestBody AdUserDto dto);
}
