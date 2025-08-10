package com.kuroneko.scheduled;

import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.database.repository.ChannelRepository;
import com.kuroneko.logger.LeagueUpdateLogger;
import com.kuroneko.service.ChampionMasteryService;
import com.kuroneko.service.RankService;
import com.kuroneko.service.SummonerService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class SummonerUpdater {


    private JDA JDA;
    private R4J riotApi;
    private SummonerService summonerService;
    private ChannelRepository channelRepository;
    private RankService rankService;
    private ChampionMasteryService championMasteryService;
    private LeagueUpdateLogger leagueUpdateLogger;

    @Scheduled(cron = "0 */5 * * * *")
    public void updateEvery5Minutes() {
        update();
    }

    @Transactional
    public void update() {

        List<SummonerEntity> summonerEntities = summonerService.getAll()
                .stream().filter(se -> !se.getChannels().isEmpty()).toList();

        summonerEntities.forEach(s -> {
            Summoner summoner = riotApi.getLoLAPI().getSummonerAPI().getSummonerByPUUID(s.getRegion(), s.getPuuid());

            List<MessageEmbed> outputs = new ArrayList<>();

            outputs.addAll(summonerService.checkSummonerInfo(s, summoner));
            outputs.addAll(rankService.checkRanks(summoner, s));
            outputs.addAll(championMasteryService.checkChampionMasteries(summoner, s));

            sendMessagesToChannels(s, outputs);
        });

    }

    private void sendMessagesToChannels(SummonerEntity summonerEntity, List<MessageEmbed> messageEmbeds) {
        if (messageEmbeds.isEmpty()) {
            return;
        }
        summonerEntity.getChannels().forEach(channelEntity -> {
            MessageChannel channelById = JDA.getChannelById(MessageChannel.class, channelEntity.getChannelId());
            if (channelById == null) {
                channelRepository.delete(channelEntity);
            } else {
                messageEmbeds.forEach(messageEmbed -> {

                    leagueUpdateLogger.log(messageEmbed, (GuildMessageChannelUnion) channelById);

                    channelById.sendMessageEmbeds(messageEmbed).queue();
                });
            }
        });
    }
}
