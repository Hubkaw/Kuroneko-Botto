package com.kuroneko.database.mapper;

import com.kuroneko.database.entity.ChampionEntity;
import no.stelar7.api.r4j.pojo.lol.staticdata.champion.StaticChampion;

public class ChampionMapper {
    public static ChampionEntity map(StaticChampion champion){
        ChampionEntity championEntity = new ChampionEntity();
        championEntity.setId(champion.getId());
        championEntity.setName(champion.getName());
        championEntity.setTitle(champion.getTitle());
        return championEntity;
    }
}
