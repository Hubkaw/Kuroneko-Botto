package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.*;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private Set<ChannelEntity> channels = new HashSet<>();


    @OneToMany(mappedBy = "summoner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChampionMasteryEntity> masteryPoints = new HashSet<>();


    @OneToMany(mappedBy = "summoner", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RankEntity> ranks = new HashSet<>();
}
