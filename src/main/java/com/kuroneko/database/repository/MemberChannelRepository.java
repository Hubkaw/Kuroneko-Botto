package com.kuroneko.database.repository;

import com.kuroneko.database.entity.MemberChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberChannelRepository extends JpaRepository<MemberChannelEntity, MemberChannelEntity.Pk> {

}
