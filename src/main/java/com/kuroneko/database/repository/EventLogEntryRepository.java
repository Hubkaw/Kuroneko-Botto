package com.kuroneko.database.repository;

import com.kuroneko.database.entity.EventLogEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventLogEntryRepository extends JpaRepository<EventLogEntryEntity, Long> {
}
