package com.kuroneko.scheduled;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import no.stelar7.api.r4j.impl.R4J;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DDragonVersionUpdater {

    private final R4J riotApi;

    @Getter
    private String currentVersion;

    public DDragonVersionUpdater(R4J riotApi) {
        this.riotApi = riotApi;
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void updateAt6EveryDay() {
        update();
    }

    @PostConstruct
    public void updateAtStartup(){
        update();
    }

    public void update() {
        currentVersion = riotApi.getDDragonAPI().getVersions().get(0);
    }
}

