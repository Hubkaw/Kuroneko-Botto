package com.kuroneko.database.repository;

import com.kuroneko.database.entity.MatchSummonerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchSummonerRepository extends JpaRepository<MatchSummonerEntity, Long> {

    @Query("""
        SELECT ms FROM MatchSummonerEntity ms
        JOIN ms.match m
        WHERE ms.summoner.puuid = :puuid
        ORDER BY m.gameStart DESC
        LIMIT 12
    """)
    List<MatchSummonerEntity> findLast12MatchesBySummonerId(@Param("puuid") String puuid);
}