package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.*;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import no.stelar7.api.r4j.basic.constants.types.lol.TierDivisionType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"summoner","queue"})})
public class RankEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "summoner")
    private SummonerEntity summoner;

    @Enumerated(EnumType.STRING)
    private GameQueueType queue;

    @Enumerated(EnumType.STRING)
    private TierDivisionType tier;

    private int leaguePoints;

    private int wins;

    private int losses;

    private String name;
}
