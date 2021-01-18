package com.mrlin.rabbitmq.service;

import cn.hutool.core.util.IdUtil;
import com.rabbitmq.client.Channel;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.Charset;

/**
 * @Description:
 * @Author: ljm
 * @Date: 2021/1/14 11:55
 * @Version: 1.0
 */
@Service
@Slf4j
public class RabbitService {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void testSend() {
        String msg = "this is demo";
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(IdUtil.simpleUUID());
        //使用默认交换机
        rabbitTemplate.convertAndSend(
               "" ,"mrlin.test.queues",msg
        );
        log.info("=====testSend====");
    }

    /**
     * 注解创建 交换机 队列 路由键
     * @param message
     * @param deliveryTag
     * @param channel
     */
    @RabbitListener(
            bindings = {
                    @QueueBinding(
                            value = @Queue(name = "queue.order",
                                    arguments = {
                                            //                                            @Argument(name =
                                            //                                            "x-message-ttl", value =
                                            //                                            "1000", type = "java.lang
                                            //                                            .Integer"),
                                            //                                            @Argument(name =
                                            //                                            "x-dead-letter-exchange",
                                            //                                            value = "aaaaa"),
                                            //                                            @Argument(name =
                                            //                                            "x-dead-letter-routing-key", value = "bbbb")
                                    }),
                            exchange = @Exchange(name = "exchange.order.restaurant", type = ExchangeTypes.DIRECT),
                            key = "key.order"
                    )
            }
    )
    @RabbitHandler
    public void  createListener(byte[]  message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, @NotNull Channel channel){
        System.out.println(new String(message, Charset.defaultCharset()));
    }
}
