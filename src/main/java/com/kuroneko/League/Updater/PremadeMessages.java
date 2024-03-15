package com.kuroneko.League.Updater;

import com.kuroneko.Database.Entity.SummonerEntity;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.core.championmastery.ChampionMastery;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class PremadeMessages {
    public MessageEmbed tierUpMessage(LeagueEntry leagueEntry) {
        EmbedBuilder embedBuilder = createBuilder(leagueEntry.getSummoner());
        String queueName = leagueEntry.getQueue() == Queue.RANKED_SOLO ? "Solo Queue " : "Flex Queue ";

        String desc = leagueEntry.getSummoner().getName() + " has reached " + leagueEntry.getTier().name() + " in "+ queueName +"\n" +
                "Current Rank: " + leagueEntry.getTier() + " " + leagueEntry.getDivision().name() + " " + leagueEntry.getLeaguePoints() + "LP";
        embedBuilder.setFooter(leagueEntry.getWins() + " wins - " + leagueEntry.getLosses() + " losses - (" + calcWinRate(leagueEntry.getWins(), leagueEntry.getLosses()) +"% win rate)");
        embedBuilder.setDescription(desc);
        return embedBuilder.build();
    }

    public MessageEmbed tierDownMessage(LeagueEntry leagueEntry) {
        EmbedBuilder embedBuilder = createBuilder(leagueEntry.getSummoner());
        String queueName = leagueEntry.getQueue() == Queue.RANKED_SOLO ? "Solo Queue " : "Flex Queue ";

        String desc = leagueEntry.getSummoner().getName() + " has deranked to " + leagueEntry.getTier().name() + " in " + queueName +"\n" +
                "Current Rank: " + leagueEntry.getTier() + " " + leagueEntry.getDivision().name() + " " + leagueEntry.getLeaguePoints() + "LP";
        embedBuilder.setFooter(leagueEntry.getWins() + " wins - " + leagueEntry.getLosses() + " losses - (" + calcWinRate(leagueEntry.getWins(), leagueEntry.getLosses()) +"% win rate)");
        embedBuilder.setDescription(desc);
        return embedBuilder.build();
    }

    public MessageEmbed divUpMessage(LeagueEntry leagueEntry) {
        EmbedBuilder embedBuilder = createBuilder(leagueEntry.getSummoner());
        String queueName = leagueEntry.getQueue() == Queue.RANKED_SOLO ? "Solo Queue " : "Flex Queue ";

        String desc = leagueEntry.getSummoner().getName() + " has ranked up to " + leagueEntry.getTier().name()+ " " + leagueEntry.getDivision().name() + " in " + queueName +"\n" +
                "Current Rank: " + leagueEntry.getTier() + " " + leagueEntry.getDivision().name() + " " + leagueEntry.getLeaguePoints() + "LP";
        embedBuilder.setFooter(leagueEntry.getWins() + " wins - " + leagueEntry.getLosses() + " losses - (" + calcWinRate(leagueEntry.getWins(), leagueEntry.getLosses()) +"% win rate)");
        embedBuilder.setDescription(desc);
        return embedBuilder.build();
    }

    public MessageEmbed divDownMessage(LeagueEntry leagueEntry) {
        EmbedBuilder embedBuilder = createBuilder(leagueEntry.getSummoner());
        String queueName = leagueEntry.getQueue() == Queue.RANKED_SOLO ? "Solo Queue " : "Flex Queue ";

        String desc = leagueEntry.getSummoner().getName() + " has deranked to " + leagueEntry.getTier().name()+ " " + leagueEntry.getDivision().name() + " in " + queueName +"\n" +
                "Current Rank: " + leagueEntry.getTier() + " " + leagueEntry.getDivision().name() + " " + leagueEntry.getLeaguePoints() + "LP";
        embedBuilder.setFooter(leagueEntry.getWins() + " wins - " + leagueEntry.getLosses() + " losses - (" + calcWinRate(leagueEntry.getWins(), leagueEntry.getLosses()) +"% win rate)");
        embedBuilder.setDescription(desc);
        return embedBuilder.build();
    }

    public MessageEmbed championMastery100k(ChampionMastery championMastery){
        EmbedBuilder embedBuilder = createBuilder(championMastery.getSummoner());
        embedBuilder.setDescription(championMastery.getSummoner().getName() + " has reached " + roundDownMasteryPoints(championMastery) + " on " + championMastery.getChampion().getName() + ". Senpai.. You shouldn't play this much...");
        embedBuilder.setThumbnail("https://static.wikia.nocookie.net/legendsofthemultiuniverse/images/7/70/601688_244053312399560_1367539327_n.jpg/revision/latest?cb=20130526154157");
        embedBuilder.setFooter(championMastery.getPoints() + " mastery points");
        return embedBuilder.build();
    }

    public MessageEmbed championMastery1m(ChampionMastery championMastery){
        EmbedBuilder embedBuilder = createBuilder(championMastery.getSummoner());
        embedBuilder.setDescription(championMastery.getSummoner().getName() + " has reached " + roundDownMasteryPoints(championMastery) + " on " + championMastery.getChampion().getName() + ". You are hopeless Senpai. Don't talk to me anymore..");
        embedBuilder.setThumbnail("https://blog.jlist.com/wp-content/uploads/2019/06/ten-things-the-Japanese-really-hate___.jpg");
        embedBuilder.setFooter(championMastery.getPoints() + " mastery points");
        return embedBuilder.build();
    }

    public MessageEmbed championLevel7(ChampionMastery championMastery){
        EmbedBuilder embedBuilder = createBuilder(championMastery.getSummoner());
        embedBuilder.setDescription(championMastery.getSummoner().getName() + " has crafted Mastery Level 7 on "+ championMastery.getChampion().getName()+". Don't think that makes you a good player Senpai.. ");
        embedBuilder.setThumbnail("https://i.imgur.com/GBDhNFs.png");
        embedBuilder.setFooter(championMastery.getPoints() + " mastery points");
        return embedBuilder.build();
    }

    public MessageEmbed nameChangeMessage(Summoner apiSummoner, SummonerEntity summonerEntity) {
        EmbedBuilder embedBuilder = createBuilder(apiSummoner);
        embedBuilder.setDescription(summonerEntity.getName() + " thinks he can run from me by changing his nickname? Of course not!\n" +
                "He is now known as " + apiSummoner.getName());
        embedBuilder.setFooter(" on " + apiSummoner.getRegion().name());
        embedBuilder.setThumbnail("https://i.imgur.com/GBDhNFs.png");
        return embedBuilder.build();
    }

    public MessageEmbed levelUp100Message(Summoner apiSummoner){
        EmbedBuilder embedBuilder = createBuilder(apiSummoner);
        embedBuilder.setDescription(apiSummoner.getName() + " has reached " + apiSummoner.getLevel() + " level. Do you even go outside Senpai?");
        embedBuilder.setThumbnail("https://i.imgur.com/GBDhNFs.png");
        return embedBuilder.build();
    }
    public  MessageEmbed levelUp1kMessage(Summoner apiSummoner) {
        EmbedBuilder embedBuilder = createBuilder(apiSummoner);
        embedBuilder.setDescription(apiSummoner.getName() + " has reached " + apiSummoner.getLevel() + " level. 1000? Really? You must be an addicted NEET Senpai, disgusting.");
        embedBuilder.setThumbnail("https://blog.jlist.com/wp-content/uploads/2019/06/ten-things-the-Japanese-really-hate___.jpg");
        return embedBuilder.build();
    }


    private EmbedBuilder createBuilder(Summoner summoner){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(summoner.getName());
        embedBuilder.setThumbnail(summoner.getProfileIcon().getImage().getURL());
        embedBuilder.setColor(new Color(131, 246, 248));
        return embedBuilder;
    }


    private int calcWinRate(int wins, int losses){
        return (int) Math.round((Double.valueOf(wins) / Double.valueOf(wins + losses)) * 100);
    }
    private int roundDownMasteryPoints(ChampionMastery championMastery){
        return  (championMastery.getPoints()/100000) * 100000;
    }

}
