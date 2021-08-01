package com.hquyyp.domain.model;

import com.hquyyp.domain.po.BattleNewSetting;

public class NewBattleSettingDaoModel extends BattleNewSetting {
    private String mapName;


    public NewBattleSettingDaoModel(String mapName) {
        this.mapName = mapName;
    }

    public NewBattleSettingDaoModel() {
    }

    public String getMapName() {
        return this.mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
