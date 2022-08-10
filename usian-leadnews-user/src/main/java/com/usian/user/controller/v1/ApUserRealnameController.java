package com.usian.user.controller.v1;

import com.usian.api.user.ApUserRealnameControllerApi;
import com.usian.common.contants.user.AdminConstans;
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
     * 审和通过
     * @param dto
     * @return
     */
    @PostMapping("/authPass")
    @Override
    public ResponseResult authPass(@RequestBody AuthDto dto) {
        return userRealnameService.updateStatusById(dto, AdminConstans.PASS_AUTH);
    }
    /**
     * 审和不通过
     * @param dto
     * @return
     */
    @PostMapping("/authFail")
    @Override
    public ResponseResult authFail(@RequestBody AuthDto dto) {
        return userRealnameService.updateStatusById(dto, AdminConstans.FAIL_AUTH);
    }
}
