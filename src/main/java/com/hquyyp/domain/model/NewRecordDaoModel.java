package com.hquyyp.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hquyyp.domain.po.NewRecord;

import java.util.Date;

public class NewRecordDaoModel {

    //记录id
    String id;

    //记录对应对局id
    String battleId;

    //记录视频存放地址
    String path;

    //记录统计数据
    String recordData;

    //对局名称
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    public NewRecordDaoModel() {
    }

    public NewRecordDaoModel(String id, String battleId, String path, String recordData, String name, Date time, Date beginTime, Date endTime) {
        this.id = id;
        this.battleId = battleId;
        this.path = path;
        this.recordData = recordData;
        this.name = name;
        this.time = time;
        this.beginTime = beginTime;
        this.endTime = endTime;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
