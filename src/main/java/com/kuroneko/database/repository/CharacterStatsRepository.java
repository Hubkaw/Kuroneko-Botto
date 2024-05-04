package com.kuroneko.database.repository;

import com.kuroneko.database.entity.CharacterStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterStatsRepository extends JpaRepository<CharacterStatsEntity, Integer> {
}
