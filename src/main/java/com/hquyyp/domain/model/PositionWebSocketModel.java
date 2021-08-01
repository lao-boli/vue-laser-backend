package com.hquyyp.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.util.Date;

@Builder
public class PositionWebSocketModel {

    private String mark;

    private String num;
    //维度
    private Double lat;
    //经度
    private Double lng;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    public PositionWebSocketModel() {
    }

    public PositionWebSocketModel(String mark, String num, Double lat, Double lng, Date time) {
        this.mark = mark;
        this.num = num;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }



    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "PositionWebSocketModel{" +
                "mark='" + mark + '\'' +
                ", num=" + num +
                ", lat=" + lat +
                ", lng=" + lng +
                ", time=" + time +
                '}';
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
