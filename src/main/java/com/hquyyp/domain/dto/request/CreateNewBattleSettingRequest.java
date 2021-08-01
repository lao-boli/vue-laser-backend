package com.hquyyp.domain.dto.request;

import com.hquyyp.domain.entity.NewBattleSettingEntity;
import com.hquyyp.utils.OutsiderUtil;

public class CreateNewBattleSettingRequest extends NewBattleSettingEntity {

    public CreateNewBattleSettingRequest() {
        setId(OutsiderUtil.getUUID());
    }
}
