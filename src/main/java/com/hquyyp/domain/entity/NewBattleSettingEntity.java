package com.hquyyp.domain.entity;

import lombok.Builder;

import java.util.List;

@Builder
public class NewBattleSettingEntity {

    //演习id
    private String id;

    //演习名称
    private String name;

    //演习使用地图id
    private String mapId;

    //红队
    private List<NewVestEntity> redTeamList;

    //蓝队
    private List<NewVestEntity> blueTeamList;

    public NewBattleSettingEntity() {
    }

    public NewBattleSettingEntity(String id, String name, String mapId, List<NewVestEntity> redTeamList, List<NewVestEntity> blueTeamList) {
        this.id = id;
        this.name = name;
        this.mapId = mapId;
        this.redTeamList = redTeamList;
        this.blueTeamList = blueTeamList;
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

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public List<NewVestEntity> getRedTeamList() {
        return redTeamList;
    }

    public void setRedTeamList(List<NewVestEntity> redTeamList) {
        this.redTeamList = redTeamList;
    }

    public List<NewVestEntity> getBlueTeamList() {
        return blueTeamList;
    }

    public void setBlueTeamList(List<NewVestEntity> blueTeamList) {
        this.blueTeamList = blueTeamList;
    }

    @Override
    public String toString() {
        return "NewBattleSettingEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mapId='" + mapId + '\'' +
                ", redTeamList=" + redTeamList +
                ", blueTeamList=" + blueTeamList +
                '}';
    }
}
