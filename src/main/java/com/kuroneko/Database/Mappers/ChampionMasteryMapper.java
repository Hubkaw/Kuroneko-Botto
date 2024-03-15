package com.kuroneko.Database.Mappers;

import com.kuroneko.Database.Entity.ChampionMasteryEntity;
import com.kuroneko.Database.Entity.SummonerEntity;
import com.merakianalytics.orianna.types.core.championmastery.ChampionMastery;

public class ChampionMasteryMapper {
    public static ChampionMasteryEntity map(ChampionMastery championMastery, SummonerEntity summoner) {
        ChampionMasteryEntity championMasteryEntity = new ChampionMasteryEntity();
        championMasteryEntity.setTokens(championMastery.getTokens());
        championMasteryEntity.setLevel(championMastery.getLevel());
        championMasteryEntity.setPoints(championMastery.getPoints());
        championMasteryEntity.setId(new ChampionMasteryEntity.Pk(summoner.getPuuid(), championMastery.getChampion().getName()));
        championMasteryEntity.setSummoner(summoner);
        return championMasteryEntity;
    }
}
