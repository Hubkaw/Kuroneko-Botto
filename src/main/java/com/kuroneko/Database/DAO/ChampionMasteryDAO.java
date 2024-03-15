package com.kuroneko.Database.DAO;

import com.kuroneko.Database.Entity.ChampionMasteryEntity;

import java.util.List;

public interface ChampionMasteryDAO {
    List<ChampionMasteryEntity> getAllBySummoner(String nickname);
    void saveOrUpdate(ChampionMasteryEntity championMasteryEntity);

    void saveOrUpdate(List<ChampionMasteryEntity> championMasteryEntities);

}
