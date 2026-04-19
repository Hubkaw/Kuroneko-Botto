package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.stelar7.api.r4j.basic.constants.types.lol.LaneType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchSummonerEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "summoner_id")
    private SummonerEntity summoner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "match_id")
    private MatchEntity match;

    private int kills;
    private int deaths;
    private int assists;
    private boolean didWin;
    private int totalMinionsKilled;
    private int neutralMinionsKilled;
    private int goldEarned;
    private int visionScore;
    private int totalDamageDealtToChampions;
    private int totalDamageTaken;
    private int wardsPlaced;

    @Enumerated(EnumType.STRING)
    private LaneType lane;

    private int championId;
}
