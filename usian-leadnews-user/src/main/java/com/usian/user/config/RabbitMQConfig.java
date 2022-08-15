package com.usian.user.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

//    public static final String EXCHANGE_MESSAGE="exchange_message";
//    public static final String QUEUE_MESSAGE="queue_message";

    //    directExchange  routingKey模式  fanout 发布订阅者模式 topic 主题模式   header  头信息模式
//    创建交换机
//    @Bean(EXCHANGE_MESSAGE)
//    public Exchange EXCHANGE_MESSAGE(){
//        //  durable  交换机是否持久化
//        return ExchangeBuilder.directExchange(EXCHANGE_MESSAGE).durable(true).build();
//    }
////    创建队列
//    @Bean(QUEUE_MESSAGE)
//    public Queue QUEUE_MESSAGE(){
//        return new Queue(QUEUE_MESSAGE);
//    }
////    关联交换机，队列
//    @Bean
//    public Binding EXCHANGE_MESSAGEandQUEUE_MESSAGE(@Qualifier(EXCHANGE_MESSAGE) Exchange exchange, @Qualifier(QUEUE_MESSAGE) Queue queue){
//        return BindingBuilder.bind(queue).to(exchange).with("auth").noargs();
//    }

  /**
  * 延迟队列
  */
//  交换机
  public static final String TTL_EXCHANGE="ttl_exchange";
//   死信队列
  public static final String TTL_QUEUE="ttl_queue";
//   接替队列
  public static final String TTL_TEST="ttl_test";


  //  创建交换机
    @Bean
    public DirectExchange TTL_EXCHANGE(){
        return new DirectExchange(TTL_EXCHANGE);
    }
//    持久化队列
      @Bean
    public Queue TTL_TEST(){    //  接替队列持久化
        //  durable  交换机是否持久化
        return new Queue(TTL_TEST,true);
    }
//   消息发送到死信队列
    @Bean
    public Queue TTL_QUEUE(){
        return QueueBuilder.durable(TTL_QUEUE)  //  死信队列
                .withArgument("x-dead-letter-exchange",TTL_EXCHANGE)    // 对应的交换机    x-dead-letter-exchange key 固定
                .withArgument("x-dead-letter-routing-key",TTL_TEST)   //  接替的队列   x-dead-letter-routing-key key固定  routing_key
                .build();
    }
    //  进行绑定
    @Bean     //  先绑定普通队列   //  再绑定交换机
    public Binding BangDing(Queue TTL_TEST, DirectExchange TTL_EXCHANGE){
          return BindingBuilder.bind(TTL_TEST)
                  .to(TTL_EXCHANGE)
                  .with("ttl_test");
    }
}
