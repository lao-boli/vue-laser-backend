package com.hquyyp.dao;

import com.hquyyp.domain.entity.MapEntity;
import com.hquyyp.domain.query.MapQueryEntity;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public interface MapEntityMapper {
    int deleteByPrimaryKey(String paramString);

    int insertSelective(MapEntity paramMapEntity);

    MapEntity selectByPrimaryKey(String paramString);

    ArrayList<MapEntity> listMapByQuery();

    List<MapEntity> listMapByQueryPage(MapQueryEntity paramMapQueryEntity);
}


