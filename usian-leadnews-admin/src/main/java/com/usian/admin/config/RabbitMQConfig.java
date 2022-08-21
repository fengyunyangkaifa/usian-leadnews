package com.usian.admin.config;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_MESSAGEWEN="exchange_messageWen";
    public static final String QUEUE_MESSAGEWEN="queue_messageWen";

    //    directExchange  routingKey模式  fanout 发布订阅者模式 topic 主题模式   header  头信息模式
//    创建交换机
    @Bean(EXCHANGE_MESSAGEWEN)
    public Exchange EXCHANGE_MESSAGE(){
        //  durable  交换机是否持久化
        return ExchangeBuilder.directExchange(EXCHANGE_MESSAGEWEN).durable(true).build();
    }
//    创建队列
    @Bean(QUEUE_MESSAGEWEN)
    public Queue QUEUE_MESSAGE(){
        return new Queue(QUEUE_MESSAGEWEN);
    }

//    关联交换机，队列
    @Bean
    public Binding EXCHANGE_MESSAGEandQUEUE_MESSAGE(@Qualifier(EXCHANGE_MESSAGEWEN) Exchange exchange, @Qualifier(QUEUE_MESSAGEWEN) Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with("auth").noargs();
    }


}
