package com.kuroneko.scheduled;

import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.database.repository.ChannelRepository;
import com.kuroneko.service.ChampionMasteryService;
import com.kuroneko.service.RankService;
import com.kuroneko.service.SummonerService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Component
@Transactional
public class SummonerUpdater {


    private JDA JDA;
    private R4J riotApi;
    private SummonerService summonerService;
    private ChannelRepository channelRepository;
    private RankService rankService;
    private ChampionMasteryService championMasteryService;

    @Scheduled(cron = "*/20 * * * * *")
    public void updateEvery10Minutes() {
        System.out.println("siema");
        update();
    }

    private void update() {

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
        if (messageEmbeds.isEmpty()){
            return;
        }
        summonerEntity.getChannels().forEach(channelEntity -> {
            MessageChannel channelById = JDA.getChannelById(MessageChannel.class, channelEntity.getChannelId());
            if (channelById == null) {
                channelRepository.delete(channelEntity);
            } else {
                messageEmbeds.forEach(messageEmbed -> {
                    if (messageEmbed.getDescription().contains("You are hopeless Senpai. Don't talk to me anymore.")){
                        send1mMasteryPointsMessages(messageEmbed, summonerEntity);
                    } else {
                        channelById.sendMessageEmbeds(messageEmbed).queue();
                    }
                });
            }
        });
    }
    private void send1mMasteryPointsMessages(MessageEmbed messageEmbed, SummonerEntity summonerEntity) {
        summonerEntity.getChannels().parallelStream().forEach(channelEntity -> {
            MessageChannel channelById = JDA.getChannelById(MessageChannel.class, channelEntity.getChannelId());
            if (channelById != null) {
                new Thread(() -> {
                    try {
                        CompletableFuture<Message> infoMessage = channelById.sendMessageEmbeds(messageEmbed).submit();

                        Thread.sleep(10000);
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle(summonerEntity.getRiotId());
                        embedBuilder.setDescription("\nYou should really kill yourself Senpai~..\n");
                        embedBuilder.setThumbnail("https://i.imgur.com/qgzJOBd.jpg");
                        embedBuilder.setFooter("in-game");
                        embedBuilder.setColor(new Color(131, 246, 248));
                        MessageEmbed disgustMessage = embedBuilder.build();
                        channelById.sendMessageEmbeds(disgustMessage).setMessageReference(infoMessage.get()).queue();
                    }catch (Exception ignore){}
                }).start();

            }
        });
    }
}
