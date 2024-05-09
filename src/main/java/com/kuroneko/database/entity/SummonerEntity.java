package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.*;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class SummonerEntity {
    @Id
    private String puuid;

    private String accountId;

    private String riotId;

    private String iconId;

    @Column
    @Enumerated(EnumType.STRING)
    private LeagueShard region;

    private int level;

    @ManyToMany(mappedBy = "summoners", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ChannelEntity> channels = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "summoner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ChampionMasteryEntity> masteryPoints = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "summoner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RankEntity> ranks = new HashSet<>();
}
