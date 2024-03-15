package com.kuroneko.League;


import com.kuroneko.Config.Config;
import com.merakianalytics.orianna.Orianna;
import com.merakianalytics.orianna.types.common.Region;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Map;
import java.util.TreeMap;

public class LeagueCommandManager extends ListenerAdapter {

    Map<String, LeagueCommand> commands = new TreeMap<>();

    public LeagueCommandManager(){
        commands.put("lolinfo", new PlayerInfo());
        commands.put("register", new PlayerRegister());
        commands.put("mastery", new PlayerMastery());

        Orianna.setDefaultRegion(Region.EUROPE_NORTH_EAST);
        Orianna.setRiotAPIKey(Config.getConfig().getRiotToken());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot())
            return;

        String message = event.getMessage().getContentRaw().toLowerCase();
        String pureCommand = null;

        if (message.startsWith(";")){
            pureCommand = message.substring(1);
        } else if(message.startsWith("<@738883479902617670>")){
            pureCommand = message.substring(23);
        } else if(message.startsWith("kuroneko ")){
            pureCommand = message.substring(9);
        }

        if (pureCommand == null || pureCommand.length() == 0){
            return;
        }
        String[] split = pureCommand.split(" ", 2);


        LeagueCommand leagueCommand = commands.get(split[0]);
        if (leagueCommand != null){
            leagueCommand.execute(event, split.length > 1 ? split[1] : null);
        }
    }
}
