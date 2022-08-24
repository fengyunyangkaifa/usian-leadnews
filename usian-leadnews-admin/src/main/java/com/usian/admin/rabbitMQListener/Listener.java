package com.usian.admin.rabbitMQListener;

import com.usian.admin.config.RabbitMQConfig;
import com.usian.admin.service.WemediaNewsAutoScanService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

//@Component
public class Listener {

    @Autowired
    private WemediaNewsAutoScanService wemediaNewsAutoScanService;
//
//  //     监听新增信息
//    @RabbitListener(queues = {RabbitMQConfig.QUEUE_MESSAGEWEN})
//    public void authMQ(String msg, Message message, Channel channel) throws IOException {
//        channel.basicAck(message.getMessageProperties().getContentLength(),true);
////          监听到 新增ID
//        Integer ID = Integer.parseInt(msg);
//        wemediaNewsAutoScanService.autoScanByMediaNewsId(ID);
//    }
//  延迟队列
       @RabbitListener(queues = {RabbitMQConfig.TTLQUEUE})
       @RabbitHandler
       public void msg(@Payload Object msg){
           SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           System.out.println("当前时间："+simpleDateFormat.format(new Date()));
           Message message = (Message) msg;
           String str = null;
           try {
               str = new String(message.getBody(),"utf-8");
           } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
           }
           if (str!=null && !"".equals(str)){
               wemediaNewsAutoScanService.autoScanByMediaNewsId(Integer.valueOf(str));
           }
}
}
