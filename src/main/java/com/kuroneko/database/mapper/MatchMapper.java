package com.kuroneko.database.mapper;

import com.kuroneko.database.entity.MatchEntity;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;

public class MatchMapper {
    public static MatchEntity map(LOLMatch match) {
        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setMatchId(match.getGameId());
        matchEntity.setGameName(match.getGameName());
        matchEntity.setGameType(match.getGameType());
        matchEntity.setGameModeType(match.getGameMode());
        matchEntity.setGameStart(match.getGameStartTimestamp());
        matchEntity.setGameDuration(match.getGameDuration());
        return matchEntity;
    }
}