package com.hquyyp.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.util.Date;
@Builder
public class RecordEntity {
    private String id;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String path;

    public RecordEntity() {
    }

    public RecordEntity(String id, String name, Date time, Date beginTime, Date endTime, String path) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
