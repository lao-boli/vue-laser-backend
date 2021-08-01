package com.hquyyp.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.util.Date;

@Builder
public class ShootWebSocketModel {
    //用于标识
    private String mark;

    private String shooterTeam;

    private String shooteeTeam;

    private String shooterNum;

    private String shooteeNum;

    private String position;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    public ShootWebSocketModel() {
    }

    public ShootWebSocketModel(String mark, String shooterTeam, String shooteeTeam, String shooterNum, String shooteeNum, String position, Date time) {
        this.mark = mark;
        this.shooterTeam = shooterTeam;
        this.shooteeTeam = shooteeTeam;
        this.shooterNum = shooterNum;
        this.shooteeNum = shooteeNum;
        this.position = position;
        this.time = time;
    }


    public String getShooterTeam() {
        return shooterTeam;
    }

    public void setShooterTeam(String shooterTeam) {
        this.shooterTeam = shooterTeam;
    }

    public String getShooteeTeam() {
        return shooteeTeam;
    }

    public void setShooteeTeam(String shooteeTeam) {
        this.shooteeTeam = shooteeTeam;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "ShootWebSocketModel{" +
                "mark='" + mark + '\'' +
                ", shooterTeam='" + shooterTeam + '\'' +
                ", shooteeTeam='" + shooteeTeam + '\'' +
                ", shooterNum=" + shooterNum +
                ", shooteeNum=" + shooteeNum +
                ", position=" + position +
                ", time=" + time +
                '}';
    }

    public String getShooterNum() {
        return shooterNum;
    }

    public void setShooterNum(String shooterNum) {
        this.shooterNum = shooterNum;
    }

    public String getShooteeNum() {
        return shooteeNum;
    }

    public void setShooteeNum(String shooteeNum) {
        this.shooteeNum = shooteeNum;
    }
}
