package com.hquyyp.domain.po;
import lombok.Builder;

@Builder
public class BattleNewSetting {

    //演习id
    private String id;

    //演习名称
    private String name;

    //演习使用地图id
    private String mapId;

    //红队
    private String redTeamList;

    //蓝队
    private String blueTeamList;

    public BattleNewSetting() {
    }

    public BattleNewSetting(String id, String name, String mapId, String redTeamList, String blueTeamList) {
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

    public String getRedTeamList() {
        return redTeamList;
    }

    public void setRedTeamList(String redTeamList) {
        this.redTeamList = redTeamList;
    }

    public String getBlueTeamList() {
        return blueTeamList;
    }

    public void setBlueTeamList(String blueTeamList) {
        this.blueTeamList = blueTeamList;
    }
}
