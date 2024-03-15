package com.kuroneko.League;

import com.kuroneko.Config.KuronekoEmbed;
import com.kuroneko.Config.TemporaryMessage;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.core.summoner.Summoner;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PlayerInfo extends LeagueCommand{



    @Override
    public void execute(MessageReceivedEvent messageReceivedEvent, String command) {
        MessageChannel channel = messageReceivedEvent.getChannel();
        try {
            Summoner summoner = Orianna.summonerNamed(command).get();
            StringBuilder desc = new StringBuilder();
            summoner.getLeaguePositions().forEach(
                    lp -> {
                        desc.append(lp.getQueue().getTag()).append(" ").append(lp.getTier()).append(" ")
                                .append(lp.getDivision()).append(" ").append(lp.getLeague().getName()).append(" ")
                                .append(" ").append(lp.getLeaguePoints()).append("LP ")
                                .append(lp.getWins()).append(" wins, ").append(lp.getLosses()).append("loses")
                                .append("\n");
                    }
            );

            MessageEmbed build = new KuronekoEmbed().setTitle(summoner.getName() + " info")
                    .setDescription(desc.toString()).build();
            channel.sendMessageEmbeds(build).queue();
        } catch (NullPointerException ignore){
            MessageEmbed notFound = new KuronekoEmbed().setTitle("Summoner not found").setDescription(command + " does not exist").build();
            new TemporaryMessage(channel, notFound).start();
        }
    }
}
