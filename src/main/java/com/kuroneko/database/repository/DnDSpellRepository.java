package com.kuroneko.database.repository;

import com.kuroneko.database.entity.DnDSpellEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DnDSpellRepository extends JpaRepository<DnDSpellEntity, String> {
    List<DnDSpellEntity> findAllByNameContainingIgnoreCase(String name);
}
