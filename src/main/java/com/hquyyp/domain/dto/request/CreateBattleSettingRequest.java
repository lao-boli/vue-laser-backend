package com.hquyyp.domain.dto.request;

import com.hquyyp.domain.entity.BattleSettingEntity;
import com.hquyyp.utils.OutsiderUtil;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "对局设置参数")
public class CreateBattleSettingRequest extends BattleSettingEntity {
    public CreateBattleSettingRequest() {
        setId(OutsiderUtil.getUUID());
    }
}
