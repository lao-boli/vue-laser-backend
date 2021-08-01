package com.hquyyp.domain.dto.request;


import com.hquyyp.domain.entity.MapEntity;
import com.hquyyp.utils.OutsiderUtil;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "地图设置参数")
public class CreateMapRequest extends MapEntity {
    public CreateMapRequest() {
        setId(OutsiderUtil.getUUID());
    }
}
