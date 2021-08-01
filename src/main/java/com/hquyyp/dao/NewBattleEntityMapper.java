package com.hquyyp.dao;

import com.hquyyp.domain.entity.NewBattleEntity;
import org.springframework.stereotype.Component;

@Component
public interface NewBattleEntityMapper {
    int deleteByPrimaryKey(String paramString);

    int insert(NewBattleEntity paramBattleEntity);

    int insertSelective(NewBattleEntity paramBattleEntity);

    NewBattleEntity selectByPrimaryKey(String paramString);

    void endAll();
}
