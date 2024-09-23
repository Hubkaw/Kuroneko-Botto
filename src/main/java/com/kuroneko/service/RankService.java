package com.kuroneko.service;

import com.kuroneko.database.entity.RankEntity;
import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.database.mapper.RankMapper;
import com.kuroneko.database.repository.RankRepository;
import com.kuroneko.database.repository.SummonerRepository;
import com.kuroneko.misc.LeaguePremakeMessages;
import com.kuroneko.misc.LeagueTierHelper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import no.stelar7.api.r4j.basic.constants.types.lol.TierDivisionType;
import no.stelar7.api.r4j.pojo.lol.league.LeagueEntry;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class RankService {

    private static final List<GameQueueType> RELEVANT_QUEUES = List.of(GameQueueType.RANKED_FLEX_SR, GameQueueType.RANKED_SOLO_5X5);

    private SummonerRepository summonerRepository;
    private RankRepository rankRepository;
    private LeaguePremakeMessages premadeMessages;

    public SummonerEntity createRanksForKnownSummoner(Summoner summoner) {
        List<LeagueEntry> leagueEntry = summoner.getLeagueEntry().stream().filter(a -> RELEVANT_QUEUES.contains(a.getQueueType()) && !a.getTierDivisionType().equals(TierDivisionType.UNRANKED)).toList();
        SummonerEntity summonerEntity = summonerRepository.findById(summoner.getPUUID()).orElse(null);
        if (summonerEntity == null) {
            return null;
        }
        List<RankEntity> ranks = leagueEntry.stream().map(le -> RankMapper.map(le, summonerEntity)).toList();
        summonerEntity.getRanks().addAll(ranks);
        rankRepository.saveAllAndFlush(ranks);
        return summonerRepository.saveAndFlush(summonerEntity);
    }

    public RankEntity save(RankEntity rank) {
        return rankRepository.saveAndFlush(rank);
    }

    public List<MessageEmbed> checkRanks(Summoner summoner, SummonerEntity summonerEntity) {

        List<MessageEmbed> result = new ArrayList<>();
        summoner.getLeagueEntry().forEach(leagueEntry -> {
            Optional<RankEntity> find = rankRepository.findByQueueAndSummoner(leagueEntry.getQueueType(), summonerEntity);
            if (find.isEmpty()) {
                RankEntity rankEntity = RankMapper.map(leagueEntry, summonerEntity);
                save(rankEntity);
                summonerEntity.getRanks().add(rankEntity);
                summonerRepository.save(summonerEntity);
                return;
            }
            RankEntity rankEntity = find.get();
            boolean hasChanged = false;
            if (rankEntity.getWins() != leagueEntry.getWins()) {
                rankEntity.setWins(leagueEntry.getWins());
                hasChanged = true;
            }
            if (rankEntity.getLosses() != leagueEntry.getLosses()) {
                rankEntity.setLosses(leagueEntry.getLosses());
                hasChanged = true;
            }
            if(rankEntity.getLeaguePoints() != leagueEntry.getLeaguePoints()){
                rankEntity.setLeaguePoints(leagueEntry.getLeaguePoints());
                hasChanged = true;
            }

            if (!rankEntity.getTier().equals(leagueEntry.getTierDivisionType())){
                if (LeagueTierHelper.compareTiers(rankEntity.getTier(), leagueEntry.getTierDivisionType()) > 0) {
                    result.add(premadeMessages.tierDownMessage(leagueEntry, summonerEntity));
                } else {
                    result.add(premadeMessages.tierUpMessage(leagueEntry, summonerEntity));
                }
                rankEntity.setTier(leagueEntry.getTierDivisionType());
                hasChanged = true;
            }

            if (hasChanged) {
                rankRepository.save(rankEntity);
            }
        });
        return result;
    }
}


