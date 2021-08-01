package com.hquyyp.dao;


import com.hquyyp.domain.entity.RecordEntity;
import com.hquyyp.domain.query.RecordQueryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RecordEntityMapper {

    int insertSelective(RecordEntity recordEntity);

    RecordEntity selectByPrimaryKey(String paramString);

    List<RecordEntity> listRecordByQueryPage(RecordQueryEntity paramRecordQueryEntity);
}
