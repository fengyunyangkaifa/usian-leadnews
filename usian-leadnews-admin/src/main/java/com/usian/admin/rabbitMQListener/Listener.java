package com.usian.admin.rabbitMQListener;

import com.rabbitmq.client.Channel;
import com.usian.admin.config.RabbitMQConfig;
import com.usian.admin.service.WemediaNewsAutoScanService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Listener {

    @Autowired
    private WemediaNewsAutoScanService wemediaNewsAutoScanService;

  //     监听新增信息
    @RabbitListener(queues = {RabbitMQConfig.QUEUE_MESSAGEWEN})
    public void authMQ(String msg, Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getContentLength(),true);
//          监听到 新增ID
        Integer ID = Integer.parseInt(msg);
        wemediaNewsAutoScanService.autoScanByMediaNewsId(ID);
    }

}
