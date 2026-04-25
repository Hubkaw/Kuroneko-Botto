package com.kuroneko.service;

import java.util.*;
import java.util.stream.Collectors;

import com.kuroneko.database.entity.MatchEntity;
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
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.stereotype.Service;

import static com.kuroneko.config.CONSTANTS.*;

@Service
@AllArgsConstructor
public class MatchHistoryService {
    private LeaguePremakeMessages premadeMessages;
    private MatchRepository matchRepository;
    private MatchSummonerRepository matchSummonerRepository;


    public List<MessageEmbed> checkMatchHistory(Summoner summoner, SummonerEntity summonerEntity) {
        List<MessageEmbed> result = new ArrayList<>();
        List<MatchEntity> newMatches = new ArrayList<>();
        List<MatchSummonerEntity> newMatchSummoners = new ArrayList<>();

        boolean newMatchPlayed = false;
        boolean isNewSummoner = !matchSummonerRepository.existsBySummonerPuuid(summonerEntity.getPuuid());

        int fetchCount = isNewSummoner ? API_FETCH_MATCHES_NEW_SUMMONER : API_FETCH_MATCHES_CRON;

        List<String> matchIds = new ArrayList<>();
        RELEVANT_QUEUES.forEach(queue -> matchIds.addAll(summoner.getLeagueGames()
                .withCount(fetchCount)
                .withQueue(queue)
                .withPuuid(summonerEntity.getPuuid())
                .get()));

        Map<Long, MatchParticipant> participantByMatchId = new HashMap<>();
        List<LOLMatch> lolMatches = new ArrayList<>();

        for (String matchId : matchIds) {
            LOLMatch match = new MatchBuilder(summoner.getPlatform())
                    .withId(matchId)
                    .getMatch();

            MatchParticipant matchParticipant = match.getParticipants()
                    .stream()
                    .filter(p -> p.getPuuid().equals(summonerEntity.getPuuid()))
                    .findFirst().orElseThrow(() -> new NoSuchElementException(
                            "Summoner with PUUID " + summonerEntity.getPuuid() + " not found in match " + matchId
                    ));

            lolMatches.add(match);
            participantByMatchId.put(match.getGameId(), matchParticipant);
        }

        List<Long> gameIds = lolMatches.stream()
                .map(LOLMatch::getGameId)
                .toList();

        Set<Long> existingMatchIds = new HashSet<>(matchRepository.findAllById(gameIds)
                .stream()
                .map(MatchEntity::getMatchId)
                .toList());

        for (LOLMatch match : lolMatches) {
            if (!existingMatchIds.contains(match.getGameId())) {
                newMatchPlayed = true;
                MatchEntity newMatchEntity = MatchMapper.map(match);

                newMatches.add(newMatchEntity);
                newMatchSummoners.add(MatchSummonerMapper.map(
                        newMatchEntity, summonerEntity, participantByMatchId.get(match.getGameId())
                ));
            }
        }

        if (newMatchPlayed) {
            matchRepository.saveAll(newMatches);
            matchSummonerRepository.saveAll(newMatchSummoners);
        }

        if (newMatchPlayed && !isNewSummoner) {
            List<MatchSummonerEntity> lastMatches = matchSummonerRepository.findLastMatchesBySummonerId(summonerEntity.getPuuid(), FIND_MATCHES_COUNT);

            if (lastMatches.isEmpty()) {
                return result;
            }

            Map<GameQueueType, List<MatchSummonerEntity>> groupedByQueue = lastMatches.stream()
                    .collect(Collectors.groupingBy(matchSummonerEntity -> matchSummonerEntity.getMatch().getGameQueueType()));

            groupedByQueue.replaceAll((_, list) ->
                    list.stream()
                            .sorted(Comparator.comparingLong(
                                    (MatchSummonerEntity matchSummonerEntity) -> matchSummonerEntity.getMatch().getGameStart()
                            ).reversed())
                            .collect(Collectors.toList())
            );

            groupedByQueue.forEach((queue, matchSummonerList) -> {
                if (!RELEVANT_QUEUES.contains(queue) || matchSummonerList.isEmpty()) {
                    return;
                }

                String gameQueueTypeCommon = queue.commonName().equals("This enum does not have a pretty name") ? queue.name() : queue.commonName();
                StreakResult streakResult = calculateStreak(matchSummonerList);
                switch (streakResult.streakType()) {
                    case WIN_STREAK ->
                            result.add(premadeMessages.gameWon(summonerEntity.getRiotId(), streakResult.count, gameQueueTypeCommon));
                    case LOSE_STREAK -> {
                        int timeThisLoserWasted = matchSummonerList.stream().findFirst().get().getMatch().getGameDuration();
                        result.add(premadeMessages.gameLost(summonerEntity.getRiotId(), timeThisLoserWasted, streakResult.count, gameQueueTypeCommon));
                    }
                    case WIN_AFTER_LOSE_STREAK ->
                            result.add(premadeMessages.gameWonAfterLoseStreak(summonerEntity.getRiotId(), gameQueueTypeCommon));
                    case LOSE_AFTER_WIN_STREAK ->
                            result.add(premadeMessages.gameLostAfterWinStreak(summonerEntity.getRiotId(), gameQueueTypeCommon));
                    case NO_STREAK -> {
                    }
                }
            });
        }

        return result;
    }

    private StreakResult calculateStreak(List<MatchSummonerEntity> matches) {
        if (matches.isEmpty()) {
            return new StreakResult(0, StreakType.NO_STREAK);
        }

        MatchSummonerEntity latest = matches.get(0);
        boolean latestWin = latest.isDidWin();

        int currentStreak = 0;
        for (MatchSummonerEntity match : matches) {
            if (match.isDidWin() == latestWin) {
                currentStreak++;
            } else {
                break;
            }
        }

        int previousStreak = 0;
        for (int i = currentStreak; i < matches.size(); i++) {
            if (matches.get(i).isDidWin() != latestWin) {
                previousStreak++;
            } else {
                break;
            }
        }

        boolean previousWasStreak = previousStreak >= 5;

        if (currentStreak == 1 && previousWasStreak) {
            return latestWin
                    ? new StreakResult(previousStreak, StreakType.WIN_AFTER_LOSE_STREAK)
                    : new StreakResult(previousStreak, StreakType.LOSE_AFTER_WIN_STREAK);
        }

        boolean isStreakMilestone = currentStreak >= 5 && (currentStreak == 5 || (currentStreak - 5) % 3 == 0);

        if (isStreakMilestone) {
            return latestWin
                    ? new StreakResult(currentStreak, StreakType.WIN_STREAK)
                    : new StreakResult(currentStreak, StreakType.LOSE_STREAK);
        }

        return new StreakResult(currentStreak, StreakType.NO_STREAK);
    }

    private record StreakResult(int count, StreakType streakType) {
    }

    private enum StreakType {
        WIN_STREAK,
        LOSE_STREAK,
        WIN_AFTER_LOSE_STREAK,
        LOSE_AFTER_WIN_STREAK,
        NO_STREAK
    }
}
