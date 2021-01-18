package com.mrlin.config;

import com.alibaba.fastjson.JSONObject;
import com.mrlin.commons.RabbitDictionary;
import com.mrlin.commons.exception.ParameterException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * @author linjm
 * @version 1.0.0
 * @decription TODO
 * @date 2021/1/12 19:36
 */
@Configuration
@ConfigurationProperties(prefix = "mrlin.rabbit",ignoreUnknownFields = false)
@Slf4j
public class RabbitMQConfig {



    private static List<String> exchangeTypeList = new ArrayList<>(2);
    private static final String X_DELAYED_MESSAGE = "x-delayed-message";
    static {
        exchangeTypeList.add(RabbitDictionary.fanout.getCode());
        exchangeTypeList.add(RabbitDictionary.direct.getCode());
        exchangeTypeList.add(X_DELAYED_MESSAGE);
    }

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String virtualHost;

    @Value("${spring.rabbitmq.publisher-returns:false}")
    private boolean publisherReturns;

    private String queues;

    private List<Map<String,String>> valueMapList;
    public List<Map<String, String>> getValueMapList() {
        return valueMapList;
    }
    public void setValueMapList(List<Map<String, String>> valueMapList) {
        this.valueMapList = valueMapList;
    }

    @Bean
    public List<Map<String,String>> valueMapList(){
        return this.valueMapList;
    }

//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setHost(host);
//        connectionFactory.setPort(port);
//        connectionFactory.setUsername(username);
//        connectionFactory.setPassword(password);
//        connectionFactory.setVirtualHost(virtualHost);
//        if(publisherReturns){
//            connectionFactory.setPublisherReturns(true);
//        }
//        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
//
//        return connectionFactory;
//    }

//    @Bean
//    @ConditionalOnMissingBean(value = RabbitTemplate.ConfirmCallback.class)
//    public RabbitTemplate.ConfirmCallback rabbitConfirmCallback() {
//        return (correlationData, ack, cause) -> {
//            if(!ack){
//                logger.error("publishConfirm消息发送到交换器被退回，Id：{};退回原因是：{}",correlationData.getId(), cause);
//            } else {
//                logger.info("发送消息到交换器成功,MessageId:{}",correlationData.getId());
//            }
//        };
//    }

//    @Bean
//    @ConditionalOnMissingBean(value = RabbitTemplate.ReturnCallback.class)
//    public RabbitTemplate.ReturnCallback rabbitReturnCallback() {
//        return (message, replyCode, replyText, exchange, routingKey) -> {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("Message",message);
//            jsonObject.put("replyCode",replyCode);
//            jsonObject.put("replyText",replyText);
//            jsonObject.put("exchange",exchange);
//            jsonObject.put("routingKey",routingKey);
//            logger.error("消息丢失："+jsonObject.toJSONString());
//        };
//    }


    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public List<Binding> initRabbitMQ(RabbitAdmin rabbitAdmin){
        Map<String, Queue> queueMap = new HashMap<>();
        Map<String,Object> exchangeMap = new HashMap<>();
        List<Binding> bindingList = new LinkedList<>();
        if(valueMapList == null || valueMapList.size() ==0){
            return bindingList;
        }
        for (Map<String, String> map : valueMapList) {
            String queueName = map.get("queue");
            String exchangeName = map.get("exchange");
            String routingKey = map.get("routingKey");
            String exchangeType = map.get("exchangeType");
            String argument = map.get("argument");
            if(null == queueName || "".equals(queueName.trim())){
                throw new ParameterException("队列名称不能为空");
            }
            queueName = queueName.trim();
            if(null != exchangeType && !"".equals(exchangeType.trim())){
                exchangeType = exchangeType.trim();
                if(!exchangeTypeList.contains(exchangeType)){
                    throw new ParameterException("无效交换机类型："+exchangeType);
                }
            }
            Queue queue = getQueue(queueName,queueMap,argument);
            queueMap.put(queueName,queue);
            rabbitAdmin.declareQueue(queue);
            Binding binding =dealBind(exchangeName,exchangeType,exchangeMap,queue,routingKey,rabbitAdmin);
            if(null != binding){
                rabbitAdmin.declareBinding(binding);
                bindingList.add(binding);
            }
        }
        return bindingList;
    }

    private Binding dealBind(String exchangeName,String exchangeType,Map<String,Object> exchangeMap,Queue queue,String routingKey,RabbitAdmin rabbitAdmin){
        if (exchangeName ==null || "".equals(exchangeName.trim())){
            return null;
        }
        if(exchangeType == null || "".equals(exchangeType.trim())){
            throw new ParameterException("交换机【"+exchangeName+"】类型不能为空");
        }
        if(routingKey != null){
            if("".equals(routingKey.trim())){
                routingKey = null;
            }
        }

        Object exchange = exchangeMap.get(exchangeName);
        Binding binding = null;
        if(RabbitDictionary.fanout.getCode().equals(exchangeType)){
            FanoutExchange fanoutExchange = exchange ==null ? new FanoutExchange(exchangeName) : (FanoutExchange)exchange;
            rabbitAdmin.declareExchange(fanoutExchange);
            binding = BindingBuilder.bind(queue).to(fanoutExchange);
        }else {
            if(routingKey == null || "".equals(routingKey.trim())){
                throw new ParameterException("队列【"+queue.getName()+"】绑定 direct 模式的交换机【"+exchangeName+"】routingKey 不能为空");
            }
            if(RabbitDictionary.direct.getCode().equals(exchangeType)){
                DirectExchange directExchange = exchange == null ? new DirectExchange(exchangeName) : (DirectExchange)exchange;
                rabbitAdmin.declareExchange(directExchange);
                binding = BindingBuilder.bind(queue).to(directExchange).with(routingKey);
            }else if(X_DELAYED_MESSAGE.equals(exchangeType)){
                Map<String, Object> args = new HashMap<>();
                args.put("x-delayed-type", "direct");
                CustomExchange customExchange = new CustomExchange("delayed_exchange", "x-delayed-message",
                        true, false,args);
                rabbitAdmin.declareExchange(customExchange);
                binding = BindingBuilder.bind(queue).to(customExchange).with(routingKey).noargs();
            }
        }
        return binding;
    }

    private Queue getQueue(String queueName,Map<String,Queue> queueMap,String argument){
        String[] argumentArr = null;
        Map<String,Object> argumentMap = null;
        if(null != argument && !"".equals(argument.trim())){
            argument = argument.trim();
            argumentArr = argument.split(",");
            argumentMap = new HashMap<>(2);
            for(String s:argumentArr){
                if(!s.contains("@")){
                    throw new ParameterException("队列属性配置异常："+s);
                }
                String[] sArr = s.split("@");
                if(sArr.length != 2){
                    throw new ParameterException("队列属性配置异常："+s);
                }
                argumentMap.put(sArr[0],sArr[1]);
            }
        }
        Queue queue = null;
        if(queueMap.containsKey(queueName)){
            queue = queueMap.get(queueName);
        }else {
            queue = new Queue(queueName,true,false,false,argumentMap);
        }
        return queue;
    }


    @Bean
    public List<Binding> fanoutBinding(LinkedList<FanoutExchange> buildFanoutExchanges,LinkedList<Queue> buildQueues,RabbitAdmin rabbitAdmin){
        List<Binding> bindingList = new LinkedList<>();
        if(valueMapList == null || valueMapList.size() ==0){
            return bindingList;
        }
        for (Map<String, String> map : valueMapList) {
            String exchangeType = map.get("exchangeType");
            if (!RabbitDictionary.fanout.getCode().equals(exchangeType)) {
                continue;
            }
            Binding binding = BindingBuilder.bind(getQueue(buildQueues, map.get("queue"))).to(getFanoutExchange(buildFanoutExchanges, map.get("exchange")));
            rabbitAdmin.declareBinding(binding);
            bindingList.add(binding);
            log.debug("正在绑定Queue【" + map.get("queue") + "】到FanoutExchange【" + map.get("exchange") + "】:" + JSONObject.toJSONString(binding));
        }
        return bindingList;
    }


    private Queue createQueue(String queueName){
        return new Queue(queueName);
    }

    private FanoutExchange createFanoutExchange(String exchangeName) {
        return new FanoutExchange(exchangeName);
    }

    private FanoutExchange getFanoutExchange(LinkedList<FanoutExchange> buildFanoutExchanges,String exchangeName){
        FanoutExchange fanoutExchange = null;
        if(buildFanoutExchanges ==null || buildFanoutExchanges.size()==0){
            return fanoutExchange;
        }
        for (FanoutExchange exchange : buildFanoutExchanges) {
            if (exchange.getName().equals(exchangeName)) {
                fanoutExchange = exchange;
                break;
            }
        }
        return fanoutExchange;
    }

    private Queue getQueue(LinkedList<Queue> buildQueues,String queueName){
        Queue queue = null;
        if( buildQueues == null || buildQueues.size() ==0){
            return queue;
        }
        for (Queue q : buildQueues) {
            if (q.getName().equals(queueName)) {
                queue = q;
                break;
            }
        }
        return queue;
    }

    @Bean
    public LinkedList<Queue> buildQueues(RabbitAdmin rabbitAdmin){
        LinkedList<Queue> queueLinkedList = new LinkedList<>();
        List<String> queueNames = new ArrayList<>();
        if(queues==null || "".equals(queues)){
            return queueLinkedList;
        }
        String[] queueArr =  queues.split(",");
        for (String queueName : queueArr) {
            if (queueNames.contains(queueName)) {
                continue;
            }
            Queue queue = createQueue(queueName);
            rabbitAdmin.declareQueue(queue);
            queueLinkedList.add(queue);
            queueNames.add(queueName);
            log.debug("Queue【" + queueName + "】正在创建:" + JSONObject.toJSONString(queue));
        }
        return queueLinkedList;
    }

    @Bean
    public LinkedList<FanoutExchange> buildFanoutExchanges(RabbitAdmin rabbitAdmin){
        LinkedList<FanoutExchange> fanoutExchangeLinkedList = new LinkedList<>();
        List<String> fanoutExchangeNames = new ArrayList<>();
        if(valueMapList == null || valueMapList.size() ==0){
            return fanoutExchangeLinkedList;
        }
        for (Map<String, String> map : valueMapList) {
            String exchangeType = map.get("exchangeType");
            if (!RabbitDictionary.fanout.getCode().equals(exchangeType)) {
                continue;
            }
            String exchange = map.get("exchange");
            if (fanoutExchangeNames.contains(exchange)) {
                continue;
            }
            FanoutExchange fanoutExchange = createFanoutExchange(exchange);
            rabbitAdmin.declareExchange(fanoutExchange);
            fanoutExchangeLinkedList.add(fanoutExchange);
            fanoutExchangeNames.add(exchange);
            log.debug("FanoutExchange【" + exchange + "】正在创建:" + JSONObject.toJSONString(fanoutExchange));
        }
        return fanoutExchangeLinkedList;
    }



    public String getQueues() {
        return queues;
    }

    public void setQueues(String queues) {
        this.queues = queues;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) ->{
            if(ack){
                //发送成功
                log.debug("ack,消息投递到exchange成功,msgId:{}",correlationData);
            }else {
                //发送失败，重试
                log.error("ack,消息投递exchange失败，msgId:{},原因{}" ,correlationData, cause);
            }
        });
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) ->
                log.info(
                        "message:{}, replyCode:{}, replyText:{}, exchange:{}, routingKey{}",
                        message,
                        replyCode,
                        replyText,
                        exchange,
                        routingKey));
        return rabbitTemplate;
    }


}
