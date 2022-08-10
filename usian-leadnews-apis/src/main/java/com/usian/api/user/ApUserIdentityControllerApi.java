package com.usian.api.user;

import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.user.dtos.AuthDto;

public interface ApUserIdentityControllerApi {

    /**
     * 审核通过
     * @param dto
     * @return
     */
    public ResponseResult authPass(AuthDto dto) ;

    /**
     * 审核失败
     * @param dto
     * @return
     */
    public ResponseResult authFail(AuthDto dto);
}
