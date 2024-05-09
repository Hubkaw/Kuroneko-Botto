package com.kuroneko.database.mapper;

import com.kuroneko.database.entity.RankEntity;
import com.kuroneko.database.entity.SummonerEntity;
import no.stelar7.api.r4j.pojo.lol.league.LeagueEntry;

public class RankMapper {
    public static RankEntity map(LeagueEntry entry, SummonerEntity summoner) {
        return RankEntity.builder()
                .queue(entry.getQueueType())
                .wins(entry.getWins())
                .losses(entry.getLosses())
                .tier(entry.getTierDivisionType())
                .leaguePoints(entry.getLeaguePoints())
                .name(entry.getLeagueId())
                .summoner(summoner)
                .build();
    }
}
