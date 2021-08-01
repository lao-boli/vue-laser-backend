package com.hquyyp.dao;

import com.hquyyp.domain.model.NewRecordDaoModel;
import com.hquyyp.domain.po.NewRecord;
import com.hquyyp.domain.query.RecordQueryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface NewRecordEntityMapper {
    void insertSelective(NewRecord recordEntity);

    NewRecordDaoModel selectByPrimaryKey(String id);

    List<NewRecordDaoModel> listRecordByQueryPage(RecordQueryEntity queryEntity);
}
