package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "channel")
    private Set<PlayerCharacterEntity> characters = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.ALL},
            fetch = FetchType.EAGER)
    @JoinTable(
            name = "registered_in_channels",
            joinColumns = {@JoinColumn(name = "channelId")},
            inverseJoinColumns = {@JoinColumn(name = "puuid")})
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<SummonerEntity> summoners = new ArrayList<>();
}
