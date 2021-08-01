package com.hquyyp.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hquyyp.domain.entity.NewRecordEntity;

import java.util.Date;
import java.util.List;

public class NewRecordView {
    //记录id
    String id;

    //记录视频存放地址
    String path;

    String BattleId;

    //记录统计数据
    List<NewRecordEntity> newRecordEntityList;

    //对局名称
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<NewRecordEntity> getNewRecordEntityList() {
        return newRecordEntityList;
    }

    public void setNewRecordEntityList(List<NewRecordEntity> newRecordEntityList) {
        this.newRecordEntityList = newRecordEntityList;
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

    public String getBattleId() {
        return BattleId;
    }

    public void setBattleId(String battleId) {
        BattleId = battleId;
    }
}
