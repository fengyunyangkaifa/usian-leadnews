package com.usian.article.rabbitMQ;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.usian.article.config.RabbitMQAotuConfig;
import com.usian.article.service.ApArticleConfigService;
import com.usian.model.article.pojos.ApArticleConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

//@Component
public class AoTuListener {

    @Autowired
    private ApArticleConfigService apArticleConfigService;

    //     监听新增信息
    @RabbitListener(queues = {RabbitMQAotuConfig.QUEUE_MESSAGEWEN})
    @RabbitHandler
    public void authMQ(String msg){
        Map map = JSONObject.parseObject(msg,Map.class);
        if (map != null && map.size() > 0) {
//          监听到 新增ID
            apArticleConfigService.update(    //  1为上架   0为下架
               Wrappers.<ApArticleConfig>lambdaUpdate().eq(ApArticleConfig::getArticleId,map.get("articleId"))
                       .set(ApArticleConfig::getIsDown,map.get("enable")));
        }

    }
}