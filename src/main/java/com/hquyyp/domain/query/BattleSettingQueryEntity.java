package com.hquyyp.domain.query;

public class BattleSettingQueryEntity extends BaseQueryEntity {
    private String name;
    private String mapName;

    public void setName(String name) {
        this.name = name;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public BattleSettingQueryEntity() {
    }

    public BattleSettingQueryEntity(String name, String mapName) {
        this.name = name;
        this.mapName = mapName;
    }

    public String getName() {
        return this.name;
    }

    public String getMapName() {
        return this.mapName;
    }
}
