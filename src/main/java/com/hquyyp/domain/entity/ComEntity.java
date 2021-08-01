package com.hquyyp.domain.entity;

import lombok.Builder;

@Builder
public class ComEntity {
    public static final int NORMAL_STATUS = 0;
    public static final int UNNORMAL_STATUS = 1;
    private String id;
    private String portName;
    private Integer baudRate;
    private Integer status;
    private String msg;

    public ComEntity(String id, String portName, Integer baudRate, Integer status, String msg) {
        this.id = id;
        this.portName = portName;
        this.baudRate = baudRate;
        this.status = status;
        this.msg = msg;
    }

    public ComEntity() {
    }

    public static ComEntityBuilder builder() {
        return new ComEntityBuilder();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = (id == null) ? null : id.trim();
    }

    public String getPortName() {
        return this.portName;
    }

    public void setPortName(String portName) {
        this.portName = (portName == null) ? null : portName.trim();
    }

    public Integer getBaudRate() {
        return this.baudRate;
    }

    public void setBaudRate(Integer baudRate) {
        this.baudRate = baudRate;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = (msg == null) ? null : msg.trim();
    }
}