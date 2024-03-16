package com.kuroneko.Config;
import com.kuroneko.Misc.OlekReply;
import com.kuroneko.Misc.RandomCommand;
import com.kuroneko.Music.MusicInteractionManager;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;


public class Bot {
    private static JDA jda;

    public Bot() {
        jda = JDABuilder.createDefault(Config.getConfig().getDiscordToken())
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .enableCache(CacheFlag.EMOJI, CacheFlag.VOICE_STATE)
                .addEventListeners(new MusicInteractionManager())
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