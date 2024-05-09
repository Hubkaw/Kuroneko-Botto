package com.kuroneko.database.repository;

import com.kuroneko.database.entity.RankEntity;
import com.kuroneko.database.entity.SummonerEntity;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<RankEntity, Long>{

    Optional<RankEntity> findByQueueAndSummoner(GameQueueType queue, SummonerEntity summoner);
}
