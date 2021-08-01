package com.hquyyp.domain.entity;

import lombok.Builder;

@Builder
public class SendDataMissionEntity {
    private byte[] data;
    private boolean isWait;
    private int vestNum;

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setWait(boolean isWait) {
        this.isWait = isWait;
    }

    public void setVestNum(int vestNum) {
        this.vestNum = vestNum;
    }

    public SendDataMissionEntity() {
    }

    public SendDataMissionEntity(byte[] data, boolean isWait, int vestNum) {
        this.data = data;
        this.isWait = isWait;
        this.vestNum = vestNum;
    }

    public static SendDataMissionEntityBuilder builder() {
        return new SendDataMissionEntityBuilder();
    }

    public byte[] getData() {
        return this.data;
    }

    public boolean isWait() {
        return this.isWait;
    }

    public int getVestNum() {
        return this.vestNum;
    }
}