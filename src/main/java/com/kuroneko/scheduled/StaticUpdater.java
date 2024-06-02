package com.kuroneko.scheduled;

import com.kuroneko.database.entity.ChampionEntity;
import com.kuroneko.database.mapper.ChampionMapper;
import com.kuroneko.database.repository.ChampionRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.staticdata.champion.StaticChampion;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class StaticUpdater {

    private R4J riotApi;
    private ChampionRepository championRepository;

    @Scheduled(cron = "0 0 6 * * *")
    public void updateChampionsEveryDayAt6() {
        updateChampions();
    }

    @PostConstruct
    public void updateChampionsAtStartup() {
        updateChampions();
    }

    private void updateChampions(){
        Map<Integer, StaticChampion> champions = riotApi.getDDragonAPI().getChampions();
        List<ChampionEntity> championEntities = champions.values().stream().map(ChampionMapper::map).toList();
        championRepository.saveAll(championEntities);
    }
}
