package com.kuroneko.service;

import java.util.ArrayList;
import java.util.List;

import com.kuroneko.database.entity.MatchSummonerEntity;
import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.database.mapper.MatchMapper;
import com.kuroneko.database.mapper.MatchSummonerMapper;
import com.kuroneko.database.repository.MatchRepository;
import com.kuroneko.database.repository.MatchSummonerRepository;
import com.kuroneko.database.repository.SummonerRepository;
import com.kuroneko.misc.LeaguePremakeMessages;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MatchHistoryService {
    private LeaguePremakeMessages premadeMessages;
    private SummonerRepository summonerRepository;
    private MatchRepository matchRepository;
    private MatchSummonerRepository matchSummonerRepository;
    private DDragonService dDragonService;

    public List<MessageEmbed> checkMatchHistory(Summoner summoner, SummonerEntity summonerEntity) {
        boolean newMatchPlayed = false;
        List<MessageEmbed> result = new ArrayList<>();

        for (LOLMatch match : summoner.getLeagueGames().withCount(12).withPuuid(summoner.getPUUID()).getMatchIterator()) {
            var matchParticipant = match.getParticipants()
                    .stream()
                    .filter(loser -> loser.getPuuid().equals(summoner.getPUUID()))
                    .findFirst().orElse(null);

            if (matchParticipant == null) {
                continue;
            }

            var matchEntity = matchRepository.findByMatchId(match.getGameId());
            if (matchEntity.isEmpty()) {
                newMatchPlayed = true;
                var newMatchEntity = MatchMapper.map(match);
                var staticChampion = dDragonService.fetchChampionById(matchParticipant.getChampionId());
                matchRepository.saveAndFlush(newMatchEntity);
                summonerEntity.getMatches().add(MatchSummonerMapper.map(newMatchEntity, summonerEntity, matchParticipant, staticChampion));
                summonerRepository.saveAndFlush(summonerEntity);
            }
        }

        if (newMatchPlayed) {
            var last12matches = matchSummonerRepository.findLast12MatchesBySummonerId(summoner.getPUUID());

            if (last12matches.isEmpty()) {
                return result;
            }

            var timeThisLoserWasted = last12matches.stream().findFirst().get().getMatch().getGameDuration();
            var streakResult = calculateStreak(last12matches);
            switch(streakResult.streakType()) {
                case WIN_STREAK -> result.add(premadeMessages.gameWon(summoner.getName(),streakResult.count));
                case LOSE_STREAK -> result.add(premadeMessages.gameLost(summoner.getName(), timeThisLoserWasted, streakResult.count));
                case WIN_AFTER_LOSE_STREAK -> result.add(premadeMessages.gameWonAfterLoseStreak(summoner.getName()));
                case LOSE_AFTER_WIN_STREAK -> result.add(premadeMessages.gameLostAfterWinStreak(summoner.getName()));
                default -> {}
            }
        }

        return result;
    }

    private StreakResult calculateStreak(List<MatchSummonerEntity> matches) {
        if (matches.isEmpty()) {return new StreakResult(0, null);}

        if (matches.size() == 1) {
            return matches.get(0).isDidWin() ? new StreakResult(1, StreakType.WIN_STREAK) : new StreakResult(1, StreakType.LOSE_STREAK);
        }

        var latestMatch = matches.get(0);
        var previousMatch = matches.get(1);

        if (latestMatch.isDidWin() == previousMatch.isDidWin()) {
            int count = 1;
            boolean didWin = latestMatch.isDidWin();
            for (int i = 1; i < matches.size(); i++) {
                if (matches.get(i).isDidWin() == didWin) {
                    count++;
                } else {
                    break;
                }
            }
            return didWin ? new StreakResult(count, StreakType.WIN_STREAK) : new StreakResult(count, StreakType.LOSE_STREAK);
        } else {
            return latestMatch.isDidWin() ? new StreakResult(1, StreakType.WIN_AFTER_LOSE_STREAK) : new StreakResult(1, StreakType.LOSE_AFTER_WIN_STREAK);
        }
    }

    private record StreakResult(int count, StreakType streakType) {}

    private enum StreakType {
        WIN_STREAK,
        LOSE_STREAK,
        WIN_AFTER_LOSE_STREAK,
        LOSE_AFTER_WIN_STREAK
    }
}
