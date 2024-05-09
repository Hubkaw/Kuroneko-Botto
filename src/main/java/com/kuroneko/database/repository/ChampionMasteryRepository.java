package com.kuroneko.database.repository;

import com.kuroneko.database.entity.ChampionMasteryEntity;
import com.kuroneko.database.entity.SummonerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChampionMasteryRepository extends JpaRepository<ChampionMasteryEntity, Long> {

    List<ChampionMasteryEntity> findAllBySummoner(SummonerEntity summoner);
}
