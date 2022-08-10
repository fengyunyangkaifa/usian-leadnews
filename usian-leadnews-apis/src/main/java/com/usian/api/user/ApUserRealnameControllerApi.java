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
     * 实名不通过
     * @param dto
     * @return
     */
    public ResponseResult authPass(AuthDto dto) ;

    /**
     * 实名通过
     * @param dto
     * @return
     */
    public ResponseResult authFail(AuthDto dto);

}
