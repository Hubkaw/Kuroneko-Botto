package com.kuroneko.service;

import com.kuroneko.database.entity.ChampionEntity;
import com.kuroneko.database.entity.ChampionMasteryEntity;
import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.database.mapper.ChampionMapper;
import com.kuroneko.database.mapper.ChampionMasteryMapper;
import com.kuroneko.database.repository.ChampionMasteryRepository;
import com.kuroneko.database.repository.ChampionRepository;
import com.kuroneko.database.repository.SummonerRepository;
import com.kuroneko.misc.LeaguePremakeMessages;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageEmbed;
import no.stelar7.api.r4j.pojo.lol.championmastery.ChampionMastery;
import no.stelar7.api.r4j.pojo.lol.staticdata.champion.StaticChampion;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.kuroneko.config.CONSTANTS.*;

@Service
@AllArgsConstructor
@Slf4j
public class ChampionMasteryService {

    private final LeaguePremakeMessages leaguePremakeMessages;
    private ChampionMasteryRepository cmRepository;
    private SummonerRepository summonerRepository;
    private ChampionRepository championRepository;
    private DDragonService dDragonService;

    public SummonerEntity createChampionMasteryForKnownSummoner(Summoner summoner) {
        SummonerEntity summonerEntity = summonerRepository.findById(summoner.getPUUID()).orElse(null);
        if (summonerEntity == null) {
            return null;
        }

        List<ChampionMasteryEntity> cmEntities = summoner.getChampionMasteries().stream()
                .map(c -> {
                    ChampionEntity championEntity = championRepository.findById(c.getChampionId()).orElse(null);
                    if (championEntity == null) {
                        return null;
                    }
                    return ChampionMasteryMapper.map(c, championEntity, summonerEntity);

                })
                .filter(Objects::nonNull)
                .toList();
        List<ChampionMasteryEntity> masteryEntities = cmRepository.saveAllAndFlush(cmEntities);
        summonerEntity.getMasteryPoints().addAll(masteryEntities);
        return summonerRepository.saveAndFlush(summonerEntity);
    }

    public List<MessageEmbed> checkChampionMasteries(Summoner summoner, SummonerEntity summonerEntity) {
        List<MessageEmbed> result = new ArrayList<>();
        List<ChampionMasteryEntity> masteriesEntities = cmRepository.findAllBySummoner(summonerEntity);
        List<ChampionMastery> championMasteries = summoner.getChampionMasteries();
        championMasteries.forEach(apiCM -> {
            ChampionMasteryEntity dbCM = masteriesEntities.stream().filter(cm -> cm.getChampion().getId() == apiCM.getChampionId()).findFirst().orElse(null);

            if (dbCM == null) {
                ChampionEntity championEntity = championRepository.findById(apiCM.getChampionId()).orElse(null);
                if (championEntity == null) {
                    log.info("Could not find champion entity with id {}", apiCM.getChampionId());
                    championEntity = loadNewChampion(apiCM.getChampionId());
                    if (championEntity == null) {
                        return;
                    }
                    log.info("Loaded champion entity with id {} name {}", apiCM.getChampionId(), championEntity.getName());
                }
                ChampionMasteryEntity newCME = ChampionMasteryMapper.map(apiCM, championEntity, summonerEntity);
                cmRepository.save(newCME);
                return;
            }

            boolean isChanged = false;

            if (apiCM.getChampionLevel() != dbCM.getLevel()) {

                if (hasPassedInterval(apiCM.getChampionLevel(), dbCM.getLevel(), MASTERY_LEVEL_MESSAGE_INTERVAL)) {
                    result.add(leaguePremakeMessages.championLevelUpdateBy10(apiCM, summonerEntity.getRiotId(), dbCM.getChampion().getName()));
                }

                dbCM.setLevel(apiCM.getChampionLevel());
                isChanged = true;
            }

            if (apiCM.getChampionPoints() != dbCM.getPoints()) {

                if (hasPassedInterval(apiCM.getChampionPoints(), dbCM.getPoints(), MASTERY_POINTS_BIG_MESSAGE_INTERVAL)) {
                    result.add(leaguePremakeMessages.championMastery1m(apiCM, summonerEntity.getRiotId(), dbCM.getChampion().getName()));
                } else if (hasPassedInterval(apiCM.getChampionPoints(), dbCM.getPoints(), MASTERY_POINTS_MESSAGE_INTERVAL)) {
                    result.add(leaguePremakeMessages.championMastery100k(apiCM, summonerEntity.getRiotId(), dbCM.getChampion().getName()));
                }

                dbCM.setPoints(apiCM.getChampionPoints());
                isChanged = true;
            }

            if (isChanged) {
                cmRepository.save(dbCM);
            }
        });
        return result;
    }


    private boolean hasPassedInterval(int api, int db, int interval) {
        return Math.floorDiv(api, interval) > Math.floorDiv(db, interval);
    }

    private ChampionEntity loadNewChampion(int id) {
        StaticChampion staticChampion = dDragonService.fetchChampionById(id);
        if (staticChampion == null) {
            log.error("Could not fetch champion id = {}", id);
            return null;
        }

        ChampionEntity entity = ChampionMapper.map(staticChampion);
        return championRepository.save(entity);
    }
}
