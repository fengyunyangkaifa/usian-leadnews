package com.usian.user;

import com.usian.model.user.dtos.AuthDto;
import com.usian.user.service.impl.ApUserRealnameServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserZDtest {

    @Autowired
    private ApUserRealnameServiceImpl apUserRealnameService;

    @Test
    public void autoUpdates(){
        AuthDto authDto = new AuthDto();
        authDto.setId(4);
        apUserRealnameService.AutoUpdateStatus(authDto);
    }
}
