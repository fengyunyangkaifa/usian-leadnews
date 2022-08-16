package com.usian.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.media.dtos.WmUserDto;
import com.usian.model.media.pojos.WmUser;

public interface WemediaLoginService extends IService<WmUser> {
    /**
     * 登录功能
     * @param dto
     * @return
     */
    ResponseResult login(WmUserDto dto);
}
