package com.usian.api.wemedia;

import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.media.dtos.WmUserDto;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestBody;

@Api(value = "自媒体登录认证管理", tags = "WmUserDto", description = "自媒体登录认证API")
public interface LoginControllerApi {
    /**
     * admin登录功能
     * @param dto
     * @return
     */
    public ResponseResult login(@RequestBody WmUserDto dto);
}
