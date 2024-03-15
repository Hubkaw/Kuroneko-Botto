package com.kuroneko.League;

import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.core.championmastery.ChampionMasteries;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlayerMastery extends LeagueCommand {
    @Override
    public void execute(MessageReceivedEvent messageReceivedEvent, String command) {
        ChampionMasteries championMasteries = Orianna.summonerNamed(command).get().getChampionMasteries();
        StringBuilder stringBuilder = new StringBuilder();
        championMasteries.forEach(cm -> {
            stringBuilder.append(cm.getChampion().getName()).append(" ").append(cm.getLevel()).append(" ")
                    .append(cm.getPoints()).append(" ").append(cm.getTokens()).append("\n");
        });
        messageReceivedEvent.getChannel().sendMessage(stringBuilder).queue();
    }
}
