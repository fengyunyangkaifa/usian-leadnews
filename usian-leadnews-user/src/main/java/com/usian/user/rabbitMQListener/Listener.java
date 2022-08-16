package com.usian.user.rabbitMQListener;

import com.usian.model.user.dtos.AuthDto;
import com.usian.user.config.RabbitMQConfig;
import com.usian.user.service.ApUserRealnameService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Listener {

//    @Autowired

//    private ApUserRealnameService apUserRealnameService;

//     监听新增信息
//    @RabbitListener(queues = {RabbitMQConfig.QUEUE_MESSAGE})
//    public void authMQ(String msg){
//        AuthDto authDto = new AuthDto();
//        authDto.setId(Integer.valueOf(msg));
//        apUserRealnameService.AutoUpdateStatus(authDto);
//    }


//    监听私信消息
    @RabbitListener(queues = {RabbitMQConfig.TTL_TEST})
    @RabbitHandler
    public void msg(@Payload Object msg){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("当前时间："+simpleDateFormat.format(new Date()));
        System.out.println("收到时间："+msg);
    }
}
