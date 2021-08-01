package com.hquyyp.domain.vo;

import com.hquyyp.domain.entity.NewBattleSettingEntity;

public class NewBattleSettingView extends NewBattleSettingEntity {
    private String mapName;


    public NewBattleSettingView(String mapName) {
        this.mapName = mapName;
    }

    public NewBattleSettingView() {
    }

    public String getMapName() {
        return this.mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
