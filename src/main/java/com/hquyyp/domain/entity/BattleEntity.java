package com.hquyyp.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hquyyp.domain.dto.BaseRestfulEntity;
import lombok.Builder;
import java.util.Date;

@Builder
public class BattleEntity extends BaseRestfulEntity {
      private String id;
      private String name;
      private String mapId;

     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private Date beginTime;

     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private Date endTime;

     public BattleEntity(String id, String name, String mapId, Date beginTime, Date endTime, Integer status, String remark, int isLoadAmmo) {
        this.id = id;
        this.name = name;
        this.mapId = mapId;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.status = status;
        this.remark = remark;
        this.isLoadAmmo = isLoadAmmo;
    }
    private Integer status;
    private String remark;
    private int isLoadAmmo;

    public BattleEntity() {
    }

    public static BattleEntityBuilder builder() {
        return new BattleEntityBuilder();
    }


    public int getIsLoadAmmo() {
        return this.isLoadAmmo;
    }


    public void setIsLoadAmmo(int isLoadAmmo) {
        this.isLoadAmmo = isLoadAmmo;
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

    public Date getBeginTime() {
        return this.beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = (remark == null) ? null : remark.trim();
    }
}