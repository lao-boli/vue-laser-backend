package com.hquyyp.MqttInterface;


import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@MessagingGateway(defaultRequestChannel = "mqttOutputChannel")
public interface MqMessager {
    void send(String data);
    void send(@Header(MqttHeaders.TOPIC)String topic, String payload);
    void send(@Header(MqttHeaders.TOPIC)String topic,@Header(MqttHeaders.QOS)int qos,String payload);
}