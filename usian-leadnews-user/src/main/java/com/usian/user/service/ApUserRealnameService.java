package com.usian.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.user.dtos.AuthDto;
import com.usian.model.user.pojos.ApUserRealname;

public interface ApUserRealnameService extends IService<ApUserRealname> {

    /**
     * 根据状态查询需要认证相关的用户信息
     * @param dto
     * @return
     */
    ResponseResult loadListByStatus(AuthDto dto);


    /**
     *  自动审核API
     */
    public ResponseResult AutoUpdateStatus(AuthDto dto);

    /**
     * 人工实名认证 /  通过不通过
     * @param dto
     * @param status
     * @return
     */
    public ResponseResult updateStatusById(AuthDto dto, Short status);

}
