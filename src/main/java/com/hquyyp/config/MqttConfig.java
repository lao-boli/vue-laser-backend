package com.hquyyp.config;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hquyyp.protocol.ProtoEntity;
import com.hquyyp.service.NewVestService;
import com.hquyyp.utils.ProtoParser;
import com.hquyyp.websocket.ShootWebSocket;
import com.sun.jersey.core.util.Base64;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.logging.Logger;
import java.lang.String;

@Component
public class MqttConfig {
    private static final Logger log=Logger.getLogger(com.hquyyp.config.MqttConfig.class.getName());

    @Value("${mq.host}")
    private String host;
    @Value("${mq.clientId}")
    private String clientId;
    @Value("${mq.topic}")
    private String topic;
    @Value("${mq.qos}")
    private Integer qos;
    @Value("${mq.username}")
    private String username;
    @Value("${mq.password}")
    private String password;
    @Value("${mq.timeout}")
    private Integer timeout;
    @Value("${mq.keepalive}")
    private Integer keepalive;

    @Autowired
    private NewVestService vestService;

    @Autowired
    private ShootWebSocket shootWebSocket;

    @Bean
    public MqttConnectOptions mqttConnectOptions(){
        log.info(" make options");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(timeout);
        options.setKeepAliveInterval(keepalive);
        options.setAutomaticReconnect(true);
        if (StrUtil.isNotBlank(username)&&StrUtil.isNotBlank(password)){
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        }
        options.setServerURIs(new String[]{host});
        return options;
    }

    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory(MqttConnectOptions mqttConnectOptions){
        log.info("make factory");
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions);
        return factory;
    }

    @Bean
    public MessageChannel mqttOutputChannel() {
        DirectChannel directChannel = new DirectChannel();
        return directChannel;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutputChannel")
    public MessageHandler mqttOutbound(MqttPahoClientFactory factory) {
        //订阅者发布者ID相同会导致掉线重连
        MqttPahoMessageHandler messageHandler =  new MqttPahoMessageHandler(clientId+"_pub", factory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(topic);
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        DirectChannel directChannel = new DirectChannel();
        return directChannel;
    }


    @Bean
    public MessageProducer inbound(MqttPahoClientFactory factory, @Qualifier("mqttInputChannel") MessageChannel channel) {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId, factory,
                        topic);
        adapter.setCompletionTimeout(timeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qos);
        adapter.setOutputChannel(channel);
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            String topic = (String)message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
            int qos = (int) message.getHeaders().get(MqttHeaders.RECEIVED_QOS);
            JSONObject MqttData = null;
            try {
                MqttData = JSON.parseObject(message.getPayload().toString());
            } catch (Exception e) {
                log.warning("json decode fail："+ message.getPayload());
                return;
            }
            String data = MqttData.getString("data");
            log.info("主题："+topic+"，数据："+ message.getPayload()+"，QOS："+qos);
            log.info("data："+ data);

            byte[] decode;
            try {
                decode = Base64.decode(data);
            } catch (Exception e) {
                log.warning("Base64 decode fail: data: " + data);
                return;
            }

            ProtoEntity entity = ProtoParser.decode(decode);

            log.info("mqtt data parse: " + data);
            vestService.handleVestMqData(entity);
        };
    }

}
