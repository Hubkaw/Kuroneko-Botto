package League.Updater;

import Config.Bot;
import Database.DAO.*;
import Database.Entity.ChampionMasteryEntity;
import Database.Entity.RankEntity;
import Database.Entity.SummonerEntity;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Updater extends Thread {

    private PremadeMessages premadeMessages = new PremadeMessages();
    @Override
    public void run() {
        try {
            Thread.sleep(5000);
            while (true) {
                SummonerDAO summonerDAO = new SummonerDAOImpl();
                List<SummonerEntity> dbSummoners = summonerDAO.getSummoners();
                List<String> puuids = dbSummoners.stream().flatMap(s -> Stream.of(s.getPuuid())).toList();
                List<Summoner> apiSummoners = Orianna.summonersWithPuuids(puuids).get();
                if (apiSummoners.isEmpty()){
                    System.out.println("CANT LOAD SUMMONERS FROM RIOT API");
                    return;
                }
                apiSummoners.forEach(apiSummoner -> {
                    SummonerEntity summonerEntity = dbSummoners.stream().filter(s -> s.getPuuid().equals(apiSummoner.getPuuid())).findFirst().orElse(null);
                    if (summonerEntity != null){

                        summonerChangeChecks(apiSummoner, summonerEntity);

                        championMasteryCheck(apiSummoner, summonerEntity);

                        rankChangeChecks(apiSummoner, summonerEntity);
                    }

                });


                Thread.sleep(3000);
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void rankChangeChecks(Summoner apiSummoner, SummonerEntity summonerEntity) {
        apiSummoner.getLeaguePositions().forEach(leagueEntry -> {
            boolean hasChanged = false;
            RankEntity rankEntity = summonerEntity.getRanks().stream().filter(rank -> rank.getId().getQueue() == leagueEntry.getQueue()).findFirst().orElse(null);
            if (rankEntity == null){
                rankEntity = new RankEntity();
                rankEntity.setId(new RankEntity.Pk(summonerEntity.getPuuid(), leagueEntry.getQueue()));
                rankEntity.setLosses(leagueEntry.getLosses());
                rankEntity.setWins(leagueEntry.getWins());
                rankEntity.setSummoner(summonerEntity);
                rankEntity.setDivision(leagueEntry.getDivision());
                rankEntity.setTier(leagueEntry.getTier());
                rankEntity.setName(leagueEntry.getLeague().getName());
                RankDAO rankDAO = new RankDAOImpl();
                rankDAO.saveOrUpdate(rankEntity);
                return;
            }
            if (leagueEntry.getTier() != rankEntity.getTier()){
                if (leagueEntry.getTier().compare(rankEntity.getTier()) > 0){
                    MessageEmbed messageEmbed = premadeMessages.tierUpMessage(leagueEntry);
                    sendMessageToChannels(messageEmbed, summonerEntity);
                } else if (leagueEntry.getTier().compare(rankEntity.getTier()) < 0){
                    MessageEmbed messageEmbed = premadeMessages.tierDownMessage(leagueEntry);
                    sendMessageToChannels(messageEmbed, summonerEntity);
                }
                rankEntity.setDivision(leagueEntry.getDivision());
                rankEntity.setTier(leagueEntry.getTier());
                hasChanged = true;
            } else if (leagueEntry.getDivision() != rankEntity.getDivision()){
                // Division change notifications
//                if (leagueEntry.getDivision().compare(rankEntity.getDivision()) > 0){
//                    MessageEmbed messageEmbed = premadeMessages.divUpMessage(leagueEntry);
//                    sendMessageToChannels(messageEmbed, summonerEntity);
//                } else {
//                    MessageEmbed messageEmbed = premadeMessages.divDownMessage(leagueEntry);
//                    sendMessageToChannels(messageEmbed, summonerEntity);
//                }
                rankEntity.setDivision(leagueEntry.getDivision());
                hasChanged = true;
            }
            if (!leagueEntry.getLeague().getName().equals(rankEntity.getName())){
                rankEntity.setName(leagueEntry.getLeague().getName());
                hasChanged = true;
            }

            if (hasChanged) {
                RankDAO rankDAO = new RankDAOImpl();
                rankDAO.saveOrUpdate(rankEntity);
            }
        });
    }

    private void championMasteryCheck(Summoner apiSummoner, SummonerEntity summonerEntity) {
        apiSummoner.getChampionMasteries().parallelStream().forEach(apiCM -> {
            boolean hasChanged = false;
            ChampionMasteryEntity championMasteryEntity = summonerEntity.getChampionMasteries().stream().filter(dbCM -> dbCM.getId().getChampion().equals(apiCM.getChampion().getName())).findFirst().orElse(null);
            if (championMasteryEntity == null){
                championMasteryEntity = new ChampionMasteryEntity();
                championMasteryEntity.setId(new ChampionMasteryEntity.Pk(summonerEntity.getPuuid(), apiCM.getChampion().getName()));
                championMasteryEntity.setPoints(apiCM.getPoints());
                championMasteryEntity.setLevel(apiCM.getPoints());
                championMasteryEntity.setTokens(apiCM.getTokens());
                championMasteryEntity.setSummoner(summonerEntity);
                new ChampionMasteryDAOImpl().saveOrUpdate(championMasteryEntity);
                return;
            }
            if (apiCM.getPoints() != championMasteryEntity.getPoints()){
                if (apiCM.getPoints()/100000 > championMasteryEntity.getPoints()/100000){
                    if (apiCM.getPoints()/1000000 > championMasteryEntity.getPoints()/1000000){
                        send1mMasteryPointsMessages(premadeMessages.championMastery1m(apiCM), summonerEntity);
                    } else {
                        MessageEmbed messageEmbed = premadeMessages.championMastery100k(apiCM);
                        sendMessageToChannels(messageEmbed, summonerEntity);
                    }
                }
                championMasteryEntity.setPoints(apiCM.getPoints());
                hasChanged = true;
            }
            if (apiCM.getLevel() != championMasteryEntity.getLevel()) {
                if (apiCM.getLevel() == 7){
                    MessageEmbed messageEmbed = premadeMessages.championLevel7(apiCM);
                    sendMessageToChannels(messageEmbed, summonerEntity);
                }
                championMasteryEntity.setLevel(apiCM.getLevel());
                hasChanged = true;
            }
            if (apiCM.getTokens() != championMasteryEntity.getTokens()){
                championMasteryEntity.setTokens(apiCM.getTokens());
                hasChanged = true;
            }

            if (hasChanged){
                new ChampionMasteryDAOImpl().saveOrUpdate(championMasteryEntity);
            }
        });


    }


    private void summonerChangeChecks(Summoner apiSummoner, SummonerEntity summonerEntity){
        boolean hasChanged = false;
        if (!apiSummoner.getId().equals(summonerEntity.getId())){
            summonerEntity.setId(apiSummoner.getId());
            hasChanged = true;
        }
        if (!apiSummoner.getName().replace(" ", "").equalsIgnoreCase(summonerEntity.getName().replace(" ", ""))){
            MessageEmbed messageEmbed = premadeMessages.nameChangeMessage(apiSummoner, summonerEntity);
            sendMessageToChannels(messageEmbed, summonerEntity);
            summonerEntity.setName(apiSummoner.getName());
            hasChanged = true;
        }
        if (!apiSummoner.getAccountId().equals(summonerEntity.getAccountId())){
            summonerEntity.setAccountId(apiSummoner.getAccountId());
            hasChanged = true;
        }
        if (apiSummoner.getLevel() != summonerEntity.getLevel()){
            if (apiSummoner.getLevel()/100 > summonerEntity.getLevel()/100){
                if (apiSummoner.getLevel() > 999 && apiSummoner.getLevel()/1000 > summonerEntity.getLevel()/1000){
                    MessageEmbed messageEmbed = premadeMessages.levelUp1kMessage(apiSummoner);
                    sendMessageToChannels(messageEmbed, summonerEntity);
                } else {
                    MessageEmbed messageEmbed = premadeMessages.levelUp100Message(apiSummoner);
                    sendMessageToChannels(messageEmbed, summonerEntity);
                }
            }
            summonerEntity.setLevel(apiSummoner.getLevel());
            hasChanged = true;
        }

        if (hasChanged){
            new SummonerDAOImpl().saveOrUpdate(summonerEntity);
        }
    }

    private void sendMessageToChannels(MessageEmbed messageEmbed, SummonerEntity summonerEntity) {
        summonerEntity.getChannels().forEach(channelEntity -> {
            MessageChannel channelById = Bot.getJda().getChannelById(MessageChannel.class, channelEntity.getChannelId());
            if (channelById != null)
                channelById.sendMessageEmbeds(messageEmbed).queue();
            else
                new ChannelDAOImpl().delete(channelEntity);
        });
    }

    private void send1mMasteryPointsMessages(MessageEmbed messageEmbed, SummonerEntity summonerEntity) {
        summonerEntity.getChannels().parallelStream().forEach(channelEntity -> {
            MessageChannel channelById = Bot.getJda().getChannelById(MessageChannel.class, channelEntity.getChannelId());
            if (channelById != null) {
                new Thread(() -> {
                    try {
                        CompletableFuture<Message> infoMessage = channelById.sendMessageEmbeds(messageEmbed).submit();

                        Thread.sleep(10000);
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle(summonerEntity.getName());
                        embedBuilder.setDescription("\nYou really should kill yourself Senpai~..\n");
                        embedBuilder.setThumbnail("https://i.imgur.com/qgzJOBd.jpg");
                        embedBuilder.setFooter("in game");
                        embedBuilder.setColor(new Color(131, 246, 248));
                        MessageEmbed disgustMessage = embedBuilder.build();
                        channelById.sendMessageEmbeds(disgustMessage).setMessageReference(infoMessage.get()).queue();
                    }catch (Exception ignore){}
                }).start();

            }
        });
    }


}
