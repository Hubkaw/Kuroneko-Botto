package com.kuroneko.League;

import com.kuroneko.Database.DAO.*;
import com.kuroneko.Database.Entity.ChampionMasteryEntity;
import com.kuroneko.Database.Entity.ChannelEntity;
import com.kuroneko.Database.Entity.RankEntity;
import com.kuroneko.Database.Entity.SummonerEntity;
import com.kuroneko.Config.KuronekoEmbed;
import com.kuroneko.Config.TemporaryMessage;
import com.kuroneko.Database.Mappers.ChampionMasteryMapper;
import com.kuroneko.Database.Mappers.RankMapper;
import com.kuroneko.Database.Mappers.SummonerMapper;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.core.league.LeaguePositions;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerRegister extends LeagueCommand{
    @Override
    public void execute(MessageReceivedEvent messageReceivedEvent, String command) {
        MessageChannel messageChannel = messageReceivedEvent.getChannel();
        try {

            if (command == null || command.isBlank()) {
                MessageEmbed embed = new KuronekoEmbed().setTitle("BAaaaaaka").setDescription("You are supposed to put a summoner name here, senpai").build();
                new TemporaryMessage(messageChannel, embed).start();
                return;
            }

            SummonerDAO summonerDAO = new SummonerDAOImpl();
            SummonerEntity dbSummonerEntity = summonerDAO.getSummonerByName(command);
            ChannelDAO channelDAO = new ChannelDAOImpl();
            ChannelEntity dbChannelEntity = channelDAO.getById(messageChannel.getId());

            if (dbSummonerEntity != null && dbChannelEntity != null
                    && dbChannelEntity.getSummoners().stream().anyMatch(s -> s.getName().replace(" ", "")
                    .equalsIgnoreCase(dbSummonerEntity.getName().replace(" ", "")))){
                MessageEmbed embed = new KuronekoEmbed().setTitle("Already Registered").setDescription(dbSummonerEntity.getName() + " is already registered on this channel").build();
                new TemporaryMessage(messageChannel, embed).start();
                return;
            }

            Summoner apiSummoner = Orianna.summonerNamed(command).get();
            if (!apiSummoner.exists()){
                MessageEmbed embed = new KuronekoEmbed().setTitle("No such Summoner").setDescription(dbSummonerEntity.getName() + " does not exist").build();
                new TemporaryMessage(messageChannel, embed).start();
                return;
            }

            SummonerEntity summonerEntity = SummonerMapper.map(apiSummoner);
            summonerDAO.saveOrUpdate(summonerEntity);

            if (!apiSummoner.getLeaguePositions().isEmpty()){
                addRanks(apiSummoner.getLeaguePositions(), summonerEntity);
            }
            if (!apiSummoner.getChampionMasteries().isEmpty()){
                addChampionMastery(apiSummoner, summonerEntity);
            }

            if (dbChannelEntity == null){
                dbChannelEntity = createChannel(messageReceivedEvent);
                channelDAO.saveChannel(dbChannelEntity);
            }
            dbChannelEntity.getSummoners().add(summonerEntity);
            channelDAO.updateChannel(dbChannelEntity);

            MessageEmbed embed = new KuronekoEmbed().setTitle("Summoner Registered").setDescription("I will now track " + summonerEntity.getName() + "'s progress").setThumbnail(apiSummoner.getProfileIcon().getImage().getURL()).build();
            new TemporaryMessage(messageChannel, embed).start();
        } catch (Exception e) {
            e.printStackTrace();
            MessageEmbed embed = new KuronekoEmbed().setTitle("Sorry Senpai").setDescription("I can't register summoner " + command).build();
            new TemporaryMessage(messageChannel, embed).start();
        }

    }

    private void addRanks(LeaguePositions leaguePositions, SummonerEntity summoner){
        RankDAO rankDAO = new RankDAOImpl();
        leaguePositions.forEach(pos -> {
            RankEntity rank = RankMapper.map(pos);
            rank.setSummoner(summoner);
            rankDAO.saveOrUpdate(rank);
        });
    }

    private void addChampionMastery(Summoner apiSummoner, SummonerEntity summonerEntity){
        ChampionMasteryDAO championMasteryDAO = new ChampionMasteryDAOImpl();
        List<ChampionMasteryEntity> championMasteryEntities = new ArrayList<>();
        apiSummoner.getChampionMasteries().forEach(cm -> {
            ChampionMasteryEntity mapped = ChampionMasteryMapper.map(cm, summonerEntity);
            championMasteryEntities.add(mapped);
        });
        championMasteryDAO.saveOrUpdate(championMasteryEntities);
    }

    private ChannelEntity createChannel(MessageReceivedEvent mre){
        ChannelEntity newChannelEntity = new ChannelEntity();
        newChannelEntity.setChannelId(mre.getChannel().getId());
        newChannelEntity.setGuildId(mre.getGuild().getIdLong());
        newChannelEntity.setChannelName(mre.getChannel().getName());
        return newChannelEntity;
    }
}
