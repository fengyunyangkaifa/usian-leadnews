package com.usian.user.service;

import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.user.dtos.AuthDto;

public interface ApUserIdentityService {


    /**
     * 审和通过不通过
     * @param dto
     * @param status
     * @return
     */
    public ResponseResult updateStatusById(AuthDto dto, Short status);
}
