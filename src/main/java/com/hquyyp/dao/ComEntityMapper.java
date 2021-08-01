package com.hquyyp.dao;

import com.hquyyp.domain.entity.ComEntity;
import org.springframework.stereotype.Component;

@Component
public interface ComEntityMapper {
    void clearComTable();

    int insertSelective(ComEntity paramComEntity);

    int updateByPrimaryKey(ComEntity paramComEntity);

    ComEntity getComEntity();
}

