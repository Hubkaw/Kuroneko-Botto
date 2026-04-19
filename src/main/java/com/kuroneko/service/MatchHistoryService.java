package com.kuroneko.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kuroneko.database.entity.MatchSummonerEntity;
import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.database.mapper.MatchMapper;
import com.kuroneko.database.mapper.MatchSummonerMapper;
import com.kuroneko.database.repository.MatchRepository;
import com.kuroneko.database.repository.MatchSummonerRepository;
import com.kuroneko.misc.LeaguePremakeMessages;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import no.stelar7.api.r4j.impl.lol.builders.matchv5.match.MatchBuilder;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.stereotype.Service;

import static com.kuroneko.config.CONSTANTS.RELEVANT_QUEUES;

@Service
@AllArgsConstructor
public class MatchHistoryService {
    private static final int API_FETCH_MATCHES_CRON = 5;
    private static final int API_FETCH_MATCHES_NEW_SUMMONER = 50;
    private static final int FIND_MATCHES_COUNT = 50;

    private LeaguePremakeMessages premadeMessages;
    private MatchRepository matchRepository;
    private MatchSummonerRepository matchSummonerRepository;


    public List<MessageEmbed> checkMatchHistory(Summoner summoner, SummonerEntity summonerEntity) {
        List<MessageEmbed> result = new ArrayList<>();
        boolean newMatchPlayed = false;
        boolean newSummoner = !matchSummonerRepository.existsBySummonerPuuid(summonerEntity.getPuuid());

        var fetchCount = newSummoner ? API_FETCH_MATCHES_NEW_SUMMONER : API_FETCH_MATCHES_CRON;

        List<String> matchIds = summoner.getLeagueGames()
                .withCount(fetchCount)
                .withPuuid(summonerEntity.getPuuid())
                .get();

        for (String matchId : matchIds) {
            LOLMatch match = new MatchBuilder(summoner.getPlatform())
                    .withId(matchId)
                    .getMatch();

            var matchParticipant = match.getParticipants()
                    .stream()
                    .filter(p -> p.getPuuid().equals(summonerEntity.getPuuid()))
                    .findFirst().orElse(null);

            if (matchParticipant == null) continue;

            var matchEntity = matchRepository.findById(match.getGameId());
            if (matchEntity.isEmpty()) {
                newMatchPlayed = true;
                var newMatchEntity = MatchMapper.map(match);

                matchRepository.save(newMatchEntity);
                matchSummonerRepository.save(MatchSummonerMapper.map(newMatchEntity, summonerEntity, matchParticipant));
            }
        }

        if (newMatchPlayed && !newSummoner) {
            var lastMatches = matchSummonerRepository.findLastMatchesBySummonerId(summonerEntity.getPuuid(), FIND_MATCHES_COUNT);

            if (lastMatches.isEmpty()) {
                return result;
            }

            Map<GameQueueType, List<MatchSummonerEntity>> groupedByQueue = lastMatches.stream()
                    .collect(Collectors.groupingBy(matchSummonerEntity -> matchSummonerEntity.getMatch().getGameQueueType()));

            groupedByQueue.forEach((queue, matchSummonerList) -> {
                if (!RELEVANT_QUEUES.contains(queue) || matchSummonerList.isEmpty()) {
                    return;
                }

                var gameQueueTypeCommon = queue.commonName().equals("This enum does not have a pretty name") ? queue.name() : queue.commonName();
                var streakResult = calculateStreak(matchSummonerList);
                switch(streakResult.streakType()) {
                    case WIN_STREAK -> result.add(premadeMessages.gameWon(summonerEntity.getRiotId(),streakResult.count, gameQueueTypeCommon));
                    case LOSE_STREAK -> {
                        var timeThisLoserWasted = formatDuration(matchSummonerList.stream().findFirst().get().getMatch().getGameDuration());
                        result.add(premadeMessages.gameLost(summonerEntity.getRiotId(), timeThisLoserWasted, streakResult.count, gameQueueTypeCommon));
                    }
                    case WIN_AFTER_LOSE_STREAK -> result.add(premadeMessages.gameWonAfterLoseStreak(summonerEntity.getRiotId(), gameQueueTypeCommon));
                    case LOSE_AFTER_WIN_STREAK -> result.add(premadeMessages.gameLostAfterWinStreak(summonerEntity.getRiotId(), gameQueueTypeCommon));
                    default -> {}
                }
            });
        }

        return result;
    }

    private StreakResult calculateStreak(List<MatchSummonerEntity> matches) {
        if (matches.isEmpty()) {return new StreakResult(0, null);}

        if (matches.size() == 1) {
            return matches.getFirst().isDidWin() ? new StreakResult(1, StreakType.WIN_STREAK) : new StreakResult(1, StreakType.LOSE_STREAK);
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

    public static String formatDuration(int seconds) {
        if (seconds < 60) {
            return seconds + "s";
        }
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return minutes + "m " + remainingSeconds + "s";
    }

    private enum StreakType {
        WIN_STREAK,
        LOSE_STREAK,
        WIN_AFTER_LOSE_STREAK,
        LOSE_AFTER_WIN_STREAK
    }
}
