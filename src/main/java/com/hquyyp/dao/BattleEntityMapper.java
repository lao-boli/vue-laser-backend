package com.hquyyp.dao;

import com.hquyyp.domain.entity.BattleEntity;
import org.springframework.stereotype.Component;

@Component
public interface BattleEntityMapper {
    int deleteByPrimaryKey(String paramString);

    int insert(BattleEntity paramBattleEntity);

    int insertSelective(BattleEntity paramBattleEntity);

    BattleEntity selectByPrimaryKey(String paramString);

    int updateByPrimaryKeySelective(BattleEntity paramBattleEntity);

    int updateByPrimaryKey(BattleEntity paramBattleEntity);

    void endAll();

}