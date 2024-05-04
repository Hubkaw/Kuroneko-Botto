package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode
@ToString
@Table(name = "channel")
public class ChannelEntity {

    @Id
    @Column(length = 30)
    private String channelId;

    @Column
    private String channelName;

    @Column
    private Long guildId;

    @OneToMany(mappedBy = "channel")
    private Set<PlayerCharacterEntity> characters = new HashSet<>();
}
