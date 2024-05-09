package com.kuroneko.database.mapper;

import com.kuroneko.database.entity.ChampionEntity;
import com.kuroneko.database.entity.ChampionMasteryEntity;
import com.kuroneko.database.entity.SummonerEntity;
import no.stelar7.api.r4j.pojo.lol.championmastery.ChampionMastery;

public class ChampionMasteryMapper {

    public static ChampionMasteryEntity map(ChampionMastery championMastery, ChampionEntity champion, SummonerEntity summonerEntity){
        ChampionMasteryEntity championMasteryEntity = new ChampionMasteryEntity();
        championMasteryEntity.setChampion(champion);
        championMasteryEntity.setPoints(championMastery.getChampionPoints());
        championMasteryEntity.setLevel(championMastery.getChampionLevel());
        championMasteryEntity.setSummoner(summonerEntity);
        return championMasteryEntity;
    }
}
