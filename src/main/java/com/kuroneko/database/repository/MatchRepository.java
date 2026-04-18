package com.kuroneko.database.repository;

import com.kuroneko.database.entity.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long>{
    Optional<MatchEntity> findByMatchId(Long matchId);
}
