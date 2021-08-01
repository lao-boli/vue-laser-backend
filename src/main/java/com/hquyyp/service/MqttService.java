package com.hquyyp.service;

import com.hquyyp.MqttInterface.MqMessager;

import java.util.logging.Logger;

public class MqttService implements MqMessager {
    Logger logger=Logger.getLogger(com.hquyyp.service.MqttService.class.getName());
    @Override
    public void send(String data) {
        logger.info("【mqtt发送】发送"+data);
    }

    @Override
    public void send(String topic, String payload) {
        logger.info("【mqtt发送】发送"+payload);
    }

    @Override
    public void send(String topic, int qos, String payload) {
        logger.info("【mqtt发送】发送"+payload);
    }
}
