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

    private static final DecimalFormat masteryFormat = new DecimalFormat("###,###,###");

    private DDragonService dDragonService;

    public MessageEmbed nameChangeMessage(SummonerEntity summonerEntity, String oldName) {
        EmbedBuilder embedBuilder = createBuilder();
        embedBuilder.setDescription("%s thinks he can run from me by changing his nickname? Of course not! %nHe is now known as %s."
                .formatted(oldName, summonerEntity.getRiotId()));
        embedBuilder.setThumbnail("https://i.imgur.com/GBDhNFs.png");
        return embedBuilder.build();
    }

    private EmbedBuilder createBuilder() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Nerd Alert");
        embedBuilder.setColor(new Color(131, 246, 248));
        return embedBuilder;
    }

    public MessageEmbed levelUp100Message(Summoner apiSummoner, SummonerEntity summonerEntity) {
        EmbedBuilder embedBuilder = createBuilder();
        embedBuilder.setDescription("%s has reached %s level. Do you even go outside Senpai?".formatted(summonerEntity.getRiotId(), apiSummoner.getSummonerLevel()));
        embedBuilder.setThumbnail("https://i.imgur.com/GBDhNFs.png");
        return embedBuilder.build();
    }

    public MessageEmbed levelUp1kMessage(Summoner apiSummoner, SummonerEntity summonerEntity) {
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

        String footer = "%s wins - %s losses - (%s%% win rate) - %s"
                .formatted(
                        leagueEntry.getWins(),
                        leagueEntry.getLosses(),
                        calcWinRate(leagueEntry.getWins(), leagueEntry.getLosses()),
                        summonerEntity.getRegion().name());

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

        String footer = "%s wins - %s losses - (%s%% win rate) - %s"
                .formatted(
                        leagueEntry.getWins(),
                        leagueEntry.getLosses(),
                        calcWinRate(leagueEntry.getWins(), leagueEntry.getLosses()),
                        summonerEntity.getRegion().name());

        embedBuilder.setFooter(footer);
        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail(dDragonService.getIconLink(summonerEntity.getIconId()));
        return embedBuilder.build();
    }


    private int calcWinRate(int wins, int losses) {
        return (int) Math.round((Double.valueOf(wins) / Double.valueOf(wins + losses)) * 100);
    }

    private int roundDownMasteryPoints(ChampionMastery championMastery) {
        return (championMastery.getChampionPoints() / 100000) * 100000;
    }


    public MessageEmbed championMastery100k(ChampionMastery championMastery, String summonerName, String championName) {
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

    public MessageEmbed championMastery1m(ChampionMastery championMastery, String summonerName, String championName) {
        EmbedBuilder embedBuilder = createBuilder();

        String desc = "%s has reached %s on %s. You are hopeless Senpai, kill yourself or something..".formatted(
                summonerName,
                roundDownMasteryPoints(championMastery),
                championName);

        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail("https://blog.jlist.com/wp-content/uploads/2019/06/ten-things-the-Japanese-really-hate___.jpg");
        embedBuilder.setFooter(masteryFormat.format(championMastery.getChampionPoints()) + " mastery points...");
        return embedBuilder.build();
    }

    public MessageEmbed championLevelUpdateBy10(ChampionMastery championMastery, String summonerName, String championName) {
        EmbedBuilder embedBuilder = createBuilder();


        String desc = "%s has reached mastery level %s on %s. Don't think that makes you a good player Senpai.. ".formatted(
                summonerName,
                championMastery.getChampionLevel(),
                championName);

        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail("https://i.imgur.com/GBDhNFs.png");
        embedBuilder.setFooter(masteryFormat.format(championMastery.getChampionPoints()) + " mastery points");
        return embedBuilder.build();
    }

    public MessageEmbed gameLost(String summonerName, int matchTime, int loseStreak) {
        EmbedBuilder embedBuilder = createBuilder();
        String desc;
        if (loseStreak == 1) {
            desc = "%s threw yet another game in his life and wasted %s minutes for this. You could take at leas one shower with that time You smelly fart.".formatted(
                    summonerName,
                    matchTime);
        } else {
            desc = "%s has wasted %s minutes of his life just to lose %s times in a row! I hope You got reported Senpai..".formatted(
                    summonerName,
                    matchTime,
                    loseStreak);
        }


        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail("https://i.imgur.com/wiOGsMJ.jpeg");

        return embedBuilder.build();
    }

    public MessageEmbed gameWon(String summonerName, int winStreak) {
        EmbedBuilder embedBuilder = createBuilder();
        String desc;

        if (winStreak == 1) {
            desc = "%s won a game, but don't get used to it, Your team can't carry You every single time.".formatted(
                    summonerName);
        } else {
            desc = "%s was carried %s times in a row! You can't even throw a game properly..".formatted(
                    summonerName,
                    winStreak);
        }
        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail("https://i.imgur.com/wiOGsMJ.jpeg");

        return embedBuilder.build();
    }

    public MessageEmbed gameWonAfterLoseStreak(String summonerName) {
        EmbedBuilder embedBuilder = createBuilder();

        String desc = "Looks like %s was carried and broke his losing streak... at least for now. Remember Senpai, You're still a loser in real life".formatted(
                summonerName);

        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail("https://i.imgur.com/wiOGsMJ.jpeg");

        return embedBuilder.build();
    }

    public MessageEmbed gameLostAfterWinStreak(String summonerName) {
        EmbedBuilder embedBuilder = createBuilder();

        String desc = "You can only be carried so many times Senpai. How does it feel to throw perfectly winnable game? (Don't respond idk how You feel)".formatted(
                summonerName);

        embedBuilder.setDescription(desc);
        embedBuilder.setThumbnail("https://i.imgur.com/wiOGsMJ.jpeg");

        return embedBuilder.build();
    }
}
