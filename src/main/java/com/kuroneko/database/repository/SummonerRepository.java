package com.kuroneko.database.repository;

import com.kuroneko.database.entity.SummonerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SummonerRepository extends JpaRepository<SummonerEntity, String> {
    Optional<SummonerEntity> findByRiotIdIgnoreCase(String riotId);
}
