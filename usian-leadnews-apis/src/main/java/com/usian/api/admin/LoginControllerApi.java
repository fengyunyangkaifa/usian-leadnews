package com.usian.api.admin;

import com.usian.model.admin.dtos.AdUserDto;
import com.usian.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;

public interface LoginControllerApi {
    /**
     * admin登录功能
     * @param dto
     * @return
     */
    public ResponseResult login(@RequestBody AdUserDto dto);
}
