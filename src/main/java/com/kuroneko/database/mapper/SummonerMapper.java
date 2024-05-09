package com.kuroneko.database.mapper;

import com.kuroneko.database.entity.SummonerEntity;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

public class SummonerMapper {
    public static SummonerEntity map(Summoner summoner, String riotId) {
        SummonerEntity summonerEntity = new SummonerEntity();
        summonerEntity.setPuuid(summoner.getPUUID());
        summonerEntity.setRiotId(riotId);
        summonerEntity.setLevel(summoner.getSummonerLevel());
        summonerEntity.setAccountId(summoner.getAccountId());
        summonerEntity.setRegion(summoner.getPlatform());
        summonerEntity.setIconId(String.valueOf(summoner.getProfileIconId()));
        return summonerEntity;
    }
}
