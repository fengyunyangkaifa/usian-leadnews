package com.usian.api.user;


import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.user.dtos.AuthDto;

public interface ApUserRealnameControllerApi {
    /**
     *按照状态查询用户认证列表
     * @param dto
     * @return
     */
    public ResponseResult loadListByStatus(AuthDto dto);

    /**
     *人工审核修改状态
     * @param dto
     * @return
     */
    public ResponseResult loadUpdateStatus(AuthDto dto);
}
