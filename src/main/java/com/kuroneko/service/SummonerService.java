package com.kuroneko.service;

import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.database.mapper.SummonerMapper;
import com.kuroneko.database.repository.SummonerRepository;
import com.kuroneko.misc.LeaguePremakeMessages;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import no.stelar7.api.r4j.pojo.shared.RiotAccount;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SummonerService {

    private SummonerRepository summonerRepository;
    private R4J riotApi;
    private LeaguePremakeMessages premadeMessages;

    public SummonerEntity saveNewSummoner(Summoner summoner, String summonerName) {
        return summonerRepository.saveAndFlush(SummonerMapper.map(summoner, summonerName));
    }

    public SummonerEntity getSummonerById(String puuid) {
        return summonerRepository.findById(puuid).orElse(null);
    }

    public SummonerEntity save(SummonerEntity summoner) {
        return summonerRepository.saveAndFlush(summoner);
    }

    public List<SummonerEntity> getAll() {
        return summonerRepository.findAll();
    }

    public List<MessageEmbed> checkSummonerInfo(SummonerEntity summonerEntity, Summoner summoner) {
        boolean hasChanged = false;
        List<MessageEmbed> result = new ArrayList<>();

        RiotAccount accountByPUUID = riotApi.getAccountAPI().getAccountByPUUID(RegionShard.EUROPE, summoner.getPUUID());
        if (!(accountByPUUID.getName() + "#" + accountByPUUID.getTag()).equals(summonerEntity.getRiotId())) {
            String oldName = summonerEntity.getRiotId();
            summonerEntity.setRiotId(accountByPUUID.getName() + "#" + accountByPUUID.getTag());
            result.add(premadeMessages.nameChangeMessage(summonerEntity, oldName));
            hasChanged = true;
        }

        if (summoner.getSummonerLevel() != summonerEntity.getLevel()) {
            if (summoner.getSummonerLevel() / 100 > summonerEntity.getLevel() / 100) {
                if (summoner.getSummonerLevel() > 999 && summoner.getSummonerLevel() / 1000 > summonerEntity.getLevel() / 1000) {
                    result.add(premadeMessages.levelUp1kMessage(summoner, summonerEntity));
                } else {
                    result.add(premadeMessages.levelUp100Message(summoner, summonerEntity));
                }
            }
            summonerEntity.setLevel(summoner.getSummonerLevel());
            hasChanged = true;
        }

        if (!Integer.toString(summoner.getProfileIconId()).equals(summonerEntity.getIconId())){
            summonerEntity.setIconId(Integer.toString(summoner.getProfileIconId()));
            hasChanged = true;
        }

        if (hasChanged) {
            save(summonerEntity);
        }
        return result;
    }

}
