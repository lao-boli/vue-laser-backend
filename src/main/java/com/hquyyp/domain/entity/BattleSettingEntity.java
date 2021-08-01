package com.hquyyp.domain.entity;

import lombok.Builder;

@Builder
public class BattleSettingEntity {
       private String id;
       private String name;
       private String mapId;
       private String redVestNumStr;
       private String blueVestNumStr;
       private String redVestWeapon;
       private String blueVestWeapon;
       private Integer mode;

    public BattleSettingEntity() {
    }

    public BattleSettingEntity(String id, String name, String mapId, String redVestNumStr, String blueVestNumStr, String redVestWeapon, String blueVestWeapon, Integer mode) {
        this.id = id;
        this.name = name;
        this.mapId = mapId;
        this.redVestNumStr = redVestNumStr;
        this.blueVestNumStr = blueVestNumStr;
        this.redVestWeapon = redVestWeapon;
        this.blueVestWeapon = blueVestWeapon;
        this.mode = mode;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = (id == null) ? null : id.trim();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = (name == null) ? null : name.trim();
    }

    public String getMapId() {
        return this.mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = (mapId == null) ? null : mapId.trim();
    }


    public String getRedVestNumStr() {
        return this.redVestNumStr;
    }

    public void setRedVestNumStr(String redVestNumStr) {
        this.redVestNumStr = (redVestNumStr == null) ? null : redVestNumStr.trim();
    }

    public String getBlueVestNumStr() {
        return this.blueVestNumStr;
    }

    public void setBlueVestNumStr(String blueVestNumStr) {
        this.blueVestNumStr = (blueVestNumStr == null) ? null : blueVestNumStr.trim();
    }

    public String getRedVestWeapon() {
        return this.redVestWeapon;
    }

    public void setRedVestWeapon(String redVestWeapon) {
        this.redVestWeapon = (redVestWeapon == null) ? null : redVestWeapon.trim();
    }

    public String getBlueVestWeapon() {
        return this.blueVestWeapon;
    }

    public void setBlueVestWeapon(String blueVestWeapon) {
        this.blueVestWeapon = (blueVestWeapon == null) ? null : blueVestWeapon.trim();
    }

    public Integer getMode() {
        return this.mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }
}