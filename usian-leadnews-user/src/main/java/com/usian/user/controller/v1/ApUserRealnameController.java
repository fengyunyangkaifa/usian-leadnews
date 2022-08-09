package com.usian.user.controller.v1;

import com.usian.api.user.ApUserRealnameControllerApi;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.user.dtos.AuthDto;
import com.usian.user.service.ApUserRealnameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class ApUserRealnameController implements ApUserRealnameControllerApi {

    @Autowired
    private ApUserRealnameService userRealnameService;

    /**
     * 根据状态查全部
     * @param dto
     * @return
     */
    @PostMapping("/list")
    @Override
    public ResponseResult loadListByStatus(@RequestBody AuthDto dto){
        return userRealnameService.loadListByStatus(dto);
    }
    /**
     *人工审核修改状态
     * @param dto
     * @return
     */
    @Override
    public ResponseResult loadUpdateStatus(@RequestBody AuthDto dto) {
        return null;
    }
}
