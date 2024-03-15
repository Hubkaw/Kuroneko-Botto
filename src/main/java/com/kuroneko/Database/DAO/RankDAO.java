package com.kuroneko.Database.DAO;

import com.kuroneko.Database.Entity.RankEntity;

import java.io.Serializable;
import java.util.List;

public interface RankDAO {
    List<RankEntity> getAllBySummoner(String summonerName);
    void saveOrUpdate(RankEntity rank);
}
