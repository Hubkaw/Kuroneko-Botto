package Config;
import League.LeagueCommandManager;
import Misc.OlekReply;
import Music.MusicCommandManager;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Bot {
    private static JDA jda;

    public Bot() throws LoginException {
        jda = JDABuilder.createDefault(Config.getToken())
                .enableIntents(Config.getIntents())
                .enableCache(Config.getFlags())
                .addEventListeners(new MusicCommandManager())
                .addEventListeners(new OlekReply())
                .addEventListeners(new LeagueCommandManager())
                .setActivity(Activity.listening("Ride on Time"))
                .build();
    }

    public static JDA getJda() {
        return jda;
    }
}