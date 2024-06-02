package com.kuroneko.service;

import com.kuroneko.database.entity.ChampionEntity;
import com.kuroneko.database.entity.ChampionMasteryEntity;
import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.database.mapper.ChampionMasteryMapper;
import com.kuroneko.database.repository.ChampionMasteryRepository;
import com.kuroneko.database.repository.ChampionRepository;
import com.kuroneko.database.repository.SummonerRepository;
import com.kuroneko.misc.LeaguePremakeMessages;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import no.stelar7.api.r4j.pojo.lol.championmastery.ChampionMastery;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional
public class ChampionMasteryService {

    private ChampionMasteryRepository cmRepository;
    private SummonerRepository summonerRepository;
    private ChampionRepository championRepository;
    private LeaguePremakeMessages premadeMessages;

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
            ChampionMasteryEntity championMasteryEntity = masteriesEntities.stream().filter(cm -> cm.getChampion().getId() == apiCM.getChampionId()).findFirst().orElse(null);
            if (championMasteryEntity == null) {
                ChampionEntity championEntity = championRepository.findById(apiCM.getChampionId()).orElse(null);
                if (championEntity == null) {
                    return;
                }
                ChampionMasteryEntity newCME = ChampionMasteryMapper.map(apiCM, championEntity, summonerEntity);
                cmRepository.save(newCME);
                return;
            }
            boolean hasChanged = false;
            if (apiCM.getChampionPoints() != championMasteryEntity.getPoints()) {
                if (apiCM.getChampionPoints() / 1000000 > championMasteryEntity.getPoints() / 1000000) {
                    ChampionEntity championEntity = championRepository.findById(apiCM.getChampionId()).orElse(null);
                    if (championEntity != null)
                        result.add(premadeMessages.championMastery1m(apiCM, summonerEntity.getRiotId(), championEntity.getName()));
                }
                championMasteryEntity.setPoints(apiCM.getChampionPoints());
                hasChanged = true;
            }
            if (apiCM.getChampionLevel() != championMasteryEntity.getLevel()) {
                if (apiCM.getChampionLevel() == 7) {
                    ChampionEntity championEntity = championRepository.findById(apiCM.getChampionId()).orElse(null);
                    if (championEntity != null)
                        result.add(premadeMessages.championLevel7(apiCM, summonerEntity.getRiotId(), championEntity.getName()));
                }
                championMasteryEntity.setLevel(apiCM.getChampionLevel());
                hasChanged = true;
            }
            if (hasChanged) {
                cmRepository.saveAndFlush(championMasteryEntity);
            }
        });
        return result;
    }
}
