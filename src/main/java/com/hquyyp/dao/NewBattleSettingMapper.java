package com.hquyyp.dao;

import com.hquyyp.domain.entity.BattleSettingEntity;
import com.hquyyp.domain.model.NewBattleSettingDaoModel;
import com.hquyyp.domain.po.BattleNewSetting;
import com.hquyyp.domain.query.BattleSettingQueryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface NewBattleSettingMapper {
    int insertBattleSetting(BattleNewSetting battleSetting);

    List<NewBattleSettingDaoModel> listBattleSettingByQueryPage(BattleSettingQueryEntity battleSettingQueryEntity);

    int deleteByPrimaryKey(String id);

    int updateByPrimaryKey(BattleNewSetting battleNewSetting);

    BattleNewSetting selectByPrimaryKey(String id);
}
