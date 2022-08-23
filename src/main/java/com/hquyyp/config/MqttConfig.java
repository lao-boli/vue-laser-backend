package com.hquyyp.config;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hquyyp.service.NewVestService;
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
            JSONObject MqttData = JSON.parseObject(message.getPayload().toString());
            String data = MqttData.getString("data");
            log.info("主题："+topic+"，数据："+ message.getPayload()+"，QOS："+qos);
            log.info("data："+ data);
            byte[] decode = Base64.decode(data);
            String DATA = HexToString(decode);
            //log.info("解析后数据："+ DATA);
            vestService.handleVestMqData(DATA);
           // shootWebSocket.sendMessage(data);
        };
    }

    public String HexToString(byte[] bytes){
        String DATA="";
        int[] data=new int[bytes.length];
        for (int i=0;i<bytes.length;i++){
           data[i]= bytes[i] & 0xff;
        }
        //位置报送
        if (data.length==15){
            int type=data[14];
            int equipment=data[12]*256+data[13];
            double lat=Double.parseDouble(""+(data[8]*256+data[9]))/100
                    +Double.parseDouble(""+(data[10]*256+data[11]))/1000000;
            double lng=Double.parseDouble(""+(data[4]*256+data[5]))/100
                    +Double.parseDouble(""+(data[6]*256+data[7]))/1000000;
            int backhit=data[3];
            int headhit=data[2];
            int leftammo=data[1];
            DATA=type+" "+equipment+" "+
                    lng+" "+lat+" "+backhit+" "+
                    headhit+" "+leftammo;
        }
        //hit数据
        if (data.length==6){
            int type=data[5];
            int shootee=data[3]*256+data[4];
            int shooter=data[1]*256+data[2];
            int position=data[0];
            DATA=type+" "+shooter+" "+shootee+" "+position;
        }
        // 带state的hit数据
        if (data.length==7){
            int type=data[6];
            int shootee=data[4]*256+data[5];
            int shooter=data[2]*256+data[3];
            int isUpdate=data[1];
            int position=data[0];
            DATA=type+" "+shooter+" "+shootee+" "+position+" "+isUpdate;
            log.info("received byte array: " + Arrays.toString(data));
            log.info("convert to int array: " + Arrays.toString(bytes));
            log.info("encode data: " + DATA);
        }
        return DATA;
    }
}
