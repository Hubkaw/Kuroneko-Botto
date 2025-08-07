package com.kuroneko.service;

import com.kuroneko.scheduled.DDragonVersionUpdater;
import lombok.AllArgsConstructor;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.staticdata.champion.StaticChampion;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DDragonService {
    private R4J riotApi;
    private DDragonVersionUpdater dDragonVersion;

    public String getIconLink(String iconId) {
        return riotApi.getImageAPI().getProfileIcon(iconId, dDragonVersion.getCurrentVersion());
    }

    public StaticChampion fetchChampionById(int championId) {
        return riotApi.getDDragonAPI().getChampion(championId);
    }

}
