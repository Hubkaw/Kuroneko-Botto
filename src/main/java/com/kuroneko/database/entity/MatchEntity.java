package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.*;
import no.stelar7.api.r4j.basic.constants.types.lol.GameModeType;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import no.stelar7.api.r4j.basic.constants.types.lol.GameType;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchEntity {
    @Id
    private Long matchId;

    private int gameDuration;
    private long gameStart;
    private String gameName;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @Enumerated(EnumType.STRING)
    private GameModeType gameModeType;

    @Enumerated(EnumType.STRING)
    private GameQueueType gameQueueType;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "match", fetch = FetchType.EAGER)
    private Set<MatchSummonerEntity> participants = new HashSet<>();
}
