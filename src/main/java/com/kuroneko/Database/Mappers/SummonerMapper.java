package com.kuroneko.Database.Mappers;

import com.kuroneko.Database.Entity.RankEntity;
import com.kuroneko.Database.Entity.SummonerEntity;
import com.merakianalytics.orianna.types.core.summoner.Summoner;

public class SummonerMapper {
    public static SummonerEntity map(Summoner apiSummoner) {
        SummonerEntity summoner = new SummonerEntity();
        summoner.setPuuid(apiSummoner.getPuuid());
        summoner.setName(apiSummoner.getName());
        summoner.setAccountId(apiSummoner.getAccountId());
        summoner.setId(apiSummoner.getId());
        summoner.setLevel(apiSummoner.getLevel());

        return summoner;
    }
}
