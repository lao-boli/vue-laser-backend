package com.hquyyp.domain.model;


import com.hquyyp.domain.entity.BattleSettingEntity;

public class BattleSettingViewModel extends BattleSettingEntity {
    private String mapName;

    public BattleSettingViewModel(String mapName) {
        this.mapName = mapName;
    }

    public BattleSettingViewModel() {
    }

    public String getMapName() {
        return this.mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
