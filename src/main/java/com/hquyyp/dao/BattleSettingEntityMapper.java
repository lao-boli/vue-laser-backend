package com.hquyyp.dao;

import com.hquyyp.domain.entity.BattleSettingEntity;
import com.hquyyp.domain.model.BattleSettingViewModel;
import com.hquyyp.domain.query.BattleSettingQueryEntity;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public interface BattleSettingEntityMapper {
    int deleteByPrimaryKey(String paramString);

    int insertSelective(BattleSettingEntity paramBattleSettingEntity);

    ArrayList<BattleSettingViewModel> listBattleSettingByQuery();

    BattleSettingEntity selectByPrimaryKey(String paramString);

    List<BattleSettingViewModel> listBattleSettingByQueryPage(BattleSettingQueryEntity paramBattleSettingQueryEntity);
}
