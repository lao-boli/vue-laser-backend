package com.hquyyp.domain.model;


import lombok.Builder;

@Builder
public class SendDataModel {
    //暂做信息类型标志
    Integer fPort;

    //下发信息
    String data;

    public SendDataModel() {
    }

    public SendDataModel(Integer fPort, String data) {
        this.fPort = fPort;
        this.data = data;
    }

    public Integer getfPort() {
        return fPort;
    }

    public void setfPort(Integer fPort) {
        this.fPort = fPort;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SendDataModel{" +
                "fPort=" + fPort +
                ", data='" + data + '\'' +
                '}';
    }
}
