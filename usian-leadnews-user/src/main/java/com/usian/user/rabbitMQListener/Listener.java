package com.usian.user.rabbitMQListener;

import com.usian.model.user.dtos.AuthDto;
import com.usian.user.config.RabbitMQConfig;
import com.usian.user.service.ApUserRealnameService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Listener {
    @Autowired
    private ApUserRealnameService apUserRealnameService;

//     监听新增信息
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_MESSAGE})
    public void authMQ(String msg){
        AuthDto authDto = new AuthDto();
        authDto.setId(Integer.valueOf(msg));
        apUserRealnameService.AutoUpdateStatus(authDto);
    }
}
