/*    */
package com.hquyyp.domain.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.util.Date;

@Builder
public class VestEntity {
    @ExcelProperty({"马甲编号"})
    private int num;
    @ExcelProperty({"姓名"})
    private String name;
    @ExcelProperty({"生命值"})
    private int hp;
    @ExcelProperty({"弹药量"})
    private int ammo;
    @ExcelIgnore

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastReportTime;
   // @ExcelIgnore
   // private List<HitRecordEntity> hitRecordEntityList;
    @ExcelIgnore
    private double lat;
    @ExcelIgnore
    private double lng;
    @ExcelIgnore
    private String team;
    @ExcelProperty({"武器"})
    private String weapon;

    public void setNum(int num) {
        this.num = num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }


    public void setLastReportTime(Date lastReportTime) {
        this.lastReportTime = lastReportTime;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

    public VestEntity(){
    }

    public VestEntity(int num, String name, int hp, int ammo, Date lastReportTime, double lat, double lng, String team, String weapon) {
        this.num = num;
        this.name = name;
        this.hp = hp;
        this.ammo = ammo;
        this.lastReportTime = lastReportTime;
        this.lat = lat;
        this.lng = lng;
        this.team = team;
        this.weapon = weapon;
    }

    public static VestEntityBuilder builder() {
        return new VestEntityBuilder();
    }

    public int getNum() {
        return this.num;
    }

    public String getName() {
        return this.name;
    }

    public int getHp() {
        return this.hp;
    }

    public int getAmmo() {
        return this.ammo;
    }

    public Date getLastReportTime() {
        return this.lastReportTime;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    public String getTeam() {
        return this.team;
    }

    public String getWeapon() {
        return this.weapon;
    }

    @Override
    public String toString() {
        return "VestEntity{" +
                "num=" + num +
                ", name='" + name + '\'' +
                ", hp=" + hp +
                ", ammo=" + ammo +
                ", lastReportTime=" + lastReportTime +
                ", lat=" + lat +
                ", lng=" + lng +
                ", team='" + team + '\'' +
                ", weapon='" + weapon + '\'' +
                '}';
    }
}

