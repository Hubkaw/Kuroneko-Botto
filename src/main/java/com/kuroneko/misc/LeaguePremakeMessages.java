package com.kuroneko.misc;

import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.service.DDragonService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import no.stelar7.api.r4j.pojo.lol.championmastery.ChampionMastery;
import no.stelar7.api.r4j.pojo.lol.league.LeagueEntry;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import org.springframework.stereotype.Component;
import java.awt.*;
import java.text.DecimalFormat;

@AllArgsConstructor
@Component
public class LeaguePremakeMessages {

    private static DecimalFormat masteryFormat = new DecimalFormat("###,###,###");

    private DDragonService dDragonService;

    public MessageEmbed nameChangeMessage(SummonerEntity summonerEntity, String oldName) {
        EmbedBuilder embedBuilder = createBuilder();
        embedBuilder.setDescription("%s thinks he can run from me by changing his nickname? Of course not! %nHe is now known as %s."
                .formatted(oldName, summonerEntity.getRiotId()));
        embedBuilder.setThumbnail("https://i.imgur.com/GBDhNFs.png");
        return embedBuilder.build();
    }

    private EmbedBuilder createBuilder(){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Nerd Alert");
        embedBuilder.setColor(new Color(131, 246, 248));
        return embedBuilder;
    }
    public MessageEmbed levelUp100Message(Summoner apiSummoner, SummonerEntity summonerEntity){
        EmbedBuilder embedBuilder = createBuilder();
        embedBuilder.setDescription("%s has reached %s level. Do you even go outside Senpai?".formatted(summonerEntity.getRiotId(), apiSummoner.getSummonerLevel()));
        embedBuilder.setThumbnail("https://i.imgur.com/GBDhNFs.png");
        return embedBuilder.build();
    }
    public  MessageEmbed levelUp1kMessage(Summoner apiSummoner, SummonerEntity summonerEntity) {
        EmbedBuilder embedBuilder = createBuilder();
        embedBuilder.setDescription("%s has reached %s level. %s? Really? You must be an addicted NEET Senpai, disgusting.".formatted(summonerEntity.getRiotId(), apiSummoner.getSummonerLevel(), apiSummoner.getSummonerLevel()));
        embedBuilder.setThumbnail("https://blog.jlist.com/wp-content/uploads/2019/06/ten-things-the-Japanese-really-hate___.jpg");
        return embedBuilder.build();
    }

    public MessageEmbed tierUpMessage(LeagueEntry leagueEntry, SummonerEntity summonerEntity) {
        EmbedBuilder embedBuilder = createBuilder();
        String queueName = leagueEntry.getQueueType().prettyName();

        String desc = "%s has ranked up in %s, how cute :3. %nCurrent rank: %s %s LP%n"
                .formatted(
                        summonerEntity.getRiotId(),
                        queueName,
                        leagueEntry.getTierDivisionType().prettyName(),
                        leagueEntry.getLeaguePoints());

        String footer = "%s wins - %s losses - (%s%% win rate)"
                .formatted(
                        leagueEntry.getWins(),
                        leagueEntry.getLosses(),
                        calcWinRate(leagueEntry.getWins(), leagueEntry.getLosses()));

        embedBuilder.setFooter(footer);
        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail(dDragonService.getIconLink(summonerEntity.getIconId()));
        return embedBuilder.build();
    }

    public MessageEmbed tierDownMessage(LeagueEntry leagueEntry, SummonerEntity summonerEntity) {
        EmbedBuilder embedBuilder = createBuilder();
        String queueName = leagueEntry.getQueueType().prettyName();

        String desc = "%s has de-ranked in %s, what a loser.. %nCurrent rank: %s %s LP%n"
                .formatted(
                        summonerEntity.getRiotId(),
                        queueName,
                        leagueEntry.getTierDivisionType().prettyName(),
                        leagueEntry.getLeaguePoints());

        String footer = "%s wins - %s losses - (%s%% win rate)"
                .formatted(
                        leagueEntry.getWins(),
                        leagueEntry.getLosses(),
                        calcWinRate(leagueEntry.getWins(), leagueEntry.getLosses()));

        embedBuilder.setFooter(footer);
        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail(dDragonService.getIconLink(summonerEntity.getIconId()));
        return embedBuilder.build();
    }


    private int calcWinRate(int wins, int losses){
        return (int) Math.round((Double.valueOf(wins) / Double.valueOf(wins + losses)) * 100);
    }
    private int roundDownMasteryPoints(ChampionMastery championMastery){
        return  (championMastery.getChampionPoints()/100000) * 100000;
    }


    public MessageEmbed championMastery100k(ChampionMastery championMastery, String summonerName, String championName){
        EmbedBuilder embedBuilder = createBuilder();

        String desc = "%s has reached %s on %s. Senpai.. You shouldn't play this much...".formatted(
                        summonerName,
                        roundDownMasteryPoints(championMastery),
                        championName);

        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail("https://static.wikia.nocookie.net/legendsofthemultiuniverse/images/7/70/601688_244053312399560_1367539327_n.jpg/revision/latest?cb=20130526154157");
        embedBuilder.setFooter(masteryFormat.format(championMastery.getChampionPoints()) + " mastery points");
        return embedBuilder.build();
    }

    public MessageEmbed championMastery1m(ChampionMastery championMastery, String summonerName, String championName){
        EmbedBuilder embedBuilder = createBuilder();

        String desc = "%s has reached %s on %s. You are hopeless Senpai. Don't talk to me anymore..".formatted(
                summonerName,
                roundDownMasteryPoints(championMastery),
                championName);

        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail("https://blog.jlist.com/wp-content/uploads/2019/06/ten-things-the-Japanese-really-hate___.jpg");
        embedBuilder.setFooter(masteryFormat.format(championMastery.getChampionPoints()) + " mastery points...");
        return embedBuilder.build();
    }

    public MessageEmbed championLevelUpdateBy10(ChampionMastery championMastery, String summonerName, String championName, int level){
        EmbedBuilder embedBuilder = createBuilder();

        String desc = "%s has reached mastery level %s on %s. Don't think that makes you a good player Senpai.. ".formatted(
                level,
                summonerName,
                championName);

        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail("https://i.imgur.com/GBDhNFs.png");
        embedBuilder.setFooter(masteryFormat.format(championMastery.getChampionPoints()) + " mastery points");
        return embedBuilder.build();
    }
}
