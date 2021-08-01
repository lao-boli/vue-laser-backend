package com.hquyyp.domain.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.util.Date;

@Builder
public class NewRecord {

    //记录id
    String id;

    //记录对应对局id
    String battleId;

    //记录视频存放地址
    String path;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    //记录统计数据
    String recordData;

    public NewRecord() {
    }

    public NewRecord(String id, String battleId, String path, String recordData) {
        this.id = id;
        this.battleId = battleId;
        this.path = path;
        this.recordData = recordData;
    }

    public NewRecord(String id, String battleId, String path, Date time, String recordData) {
        this.id = id;
        this.battleId = battleId;
        this.path = path;
        this.time = time;
        this.recordData = recordData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBattleId() {
        return battleId;
    }

    public void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRecordData() {
        return recordData;
    }

    public void setRecordData(String recordData) {
        this.recordData = recordData;
    }

    @Override
    public String toString() {
        return "NewRecord{" +
                "id='" + id + '\'' +
                ", battleId='" + battleId + '\'' +
                ", path='" + path + '\'' +
                ", recordData='" + recordData + '\'' +
                '}';
    }
}
