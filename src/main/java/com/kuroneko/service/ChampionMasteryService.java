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
}
