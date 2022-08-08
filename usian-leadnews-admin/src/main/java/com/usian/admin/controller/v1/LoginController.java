package com.usian.admin.controller.v1;

import com.usian.admin.service.UserLoginService;
import com.usian.api.admin.LoginControllerApi;
import com.usian.model.admin.dtos.AdUserDto;
import com.usian.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@SuppressWarnings("ALL")
public class LoginController implements LoginControllerApi {
    @Autowired
    private UserLoginService userLoginService ;

    @Override
    @PostMapping("/in")
    public ResponseResult login(@RequestBody AdUserDto dto){
        return userLoginService.login(dto);
    }
}
