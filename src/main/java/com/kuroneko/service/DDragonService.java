package com.kuroneko.service;

import com.kuroneko.scheduled.DDragonVersionUpdater;
import lombok.AllArgsConstructor;
import no.stelar7.api.r4j.impl.R4J;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DDragonService {
    private R4J riotApi;
    private DDragonVersionUpdater dDragonVersion;

    public String getIconLink(String iconId) {
        return riotApi.getImageAPI().getProfileIcon(iconId, dDragonVersion.getCurrentVersion());
    }


}
