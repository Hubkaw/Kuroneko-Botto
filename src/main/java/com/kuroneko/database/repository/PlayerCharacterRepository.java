package com.kuroneko.database.repository;

import com.kuroneko.database.entity.PlayerCharacterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerCharacterRepository extends JpaRepository<PlayerCharacterEntity, PlayerCharacterEntity.Pk> {
}
