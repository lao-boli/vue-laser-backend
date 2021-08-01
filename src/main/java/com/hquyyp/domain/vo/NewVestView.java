package com.hquyyp.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hquyyp.domain.entity.NewVestEntity;

import java.util.Date;


public class NewVestView  extends NewVestEntity {

    private int hp;

    private int ammo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastReportTime;
    // @ExcelIgnore
    // private List<HitRecordEntity> hitRecordEntityList;

    private double lat;

    private double lng;

    private String team;

    public NewVestView() {
        super();
    }

    public NewVestView(NewVestEntity newVestEntity) {
        super(  newVestEntity.getId(),
                newVestEntity.getName(),
                newVestEntity.getGender(),
                newVestEntity.getAge(),
                newVestEntity.getUnit(),
                newVestEntity.getEquipment());
    }

    public NewVestView(String id, String name, String gender, String age, String unit, String equipment, int hp, int ammo, Date lastReportTime, double lat, double lng, String team) {
        super(id, name, gender, age, unit, equipment);
        this.hp = hp;
        this.ammo = ammo;
        this.lastReportTime = lastReportTime;
        this.lat = lat;
        this.lng = lng;
        this.team = team;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public Date getLastReportTime() {
        return lastReportTime;
    }

    public void setLastReportTime(Date lastReportTime) {
        this.lastReportTime = lastReportTime;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
