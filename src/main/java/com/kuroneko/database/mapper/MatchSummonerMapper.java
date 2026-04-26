package com.kuroneko.database.mapper;

import com.kuroneko.database.entity.MatchEntity;
import com.kuroneko.database.entity.MatchSummonerEntity;
import com.kuroneko.database.entity.SummonerEntity;
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant;

public class MatchSummonerMapper {
    public static MatchSummonerEntity map(MatchEntity matchEntity, SummonerEntity summonerEntity, MatchParticipant matchParticipant) {
        MatchSummonerEntity matchSummonerEntity = new MatchSummonerEntity();
        matchSummonerEntity.setMatch(matchEntity);
        matchSummonerEntity.setSummoner(summonerEntity);
        matchSummonerEntity.setDidWin(matchParticipant.didWin());
        matchSummonerEntity.setKills(matchParticipant.getKills());
        matchSummonerEntity.setDeaths(matchParticipant.getDeaths());
        matchSummonerEntity.setAssists(matchParticipant.getAssists());
        matchSummonerEntity.setTotalMinionsKilled(matchParticipant.getTotalMinionsKilled());
        matchSummonerEntity.setNeutralMinionsKilled(matchParticipant.getNeutralMinionsKilled());
        matchSummonerEntity.setGoldEarned(matchParticipant.getGoldEarned());
        matchSummonerEntity.setVisionScore(matchParticipant.getVisionScore());
        matchSummonerEntity.setTotalDamageDealtToChampions(matchParticipant.getTotalDamageDealtToChampions());
        matchSummonerEntity.setTotalDamageTaken(matchParticipant.getTotalDamageTaken());
        matchSummonerEntity.setLane(matchParticipant.getLane());
        matchSummonerEntity.setWardsPlaced(matchParticipant.getWardsPlaced());
        matchSummonerEntity.setChampionId(matchParticipant.getChampionId());
        return matchSummonerEntity;
    }
}
