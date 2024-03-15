package Config;
import League.LeagueCommandManager;
import Misc.OlekReply;
import Misc.RandomCommand;
import Music.MusicCommandManager;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class Bot {
    private static JDA jda;

    public Bot() throws LoginException {
        jda = JDABuilder.createDefault(Config.getConfig().getDiscordToken())
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .enableCache(CacheFlag.EMOJI, CacheFlag.VOICE_STATE)
                .addEventListeners(new MusicCommandManager())
                .addEventListeners(new RandomCommand())
//                .addEventListeners(new LeagueCommandManager())
                .addEventListeners(new OlekReply())
                .setActivity(Activity.listening("Ride on Time"))
                .build();
    }

    public static JDA getJda() {
        return jda;
    }
}