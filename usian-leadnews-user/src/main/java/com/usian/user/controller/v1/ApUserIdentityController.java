package com.usian.user.controller.v1;

import com.usian.api.user.ApUserIdentityControllerApi;
import com.usian.common.contants.user.AdminConstans;
import com.usian.model.common.dtos.ResponseResult;
import com.usian.model.user.dtos.AuthDto;
import com.usian.user.service.ApUserIdentityService;
import com.usian.user.service.ApUserRealnameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth1")
public class ApUserIdentityController implements ApUserIdentityControllerApi {

    @Autowired
    private ApUserIdentityService apUserIdentityService;

    /**
     * 审和通过
     * @param dto
     * @return
     */

    @PostMapping("/authPass")
    @Override
    public ResponseResult authPass(@RequestBody AuthDto dto) {
        return apUserIdentityService.updateStatusById(dto, AdminConstans.PASS_AUTH);
    }
    /**
     * 审和不通过
     * @param dto
     * @return
     */
    @PostMapping("/authFail")
    @Override
    public ResponseResult authFail(@RequestBody AuthDto dto) {
        return apUserIdentityService.updateStatusById(dto, AdminConstans.FAIL_AUTH);
    }
}
