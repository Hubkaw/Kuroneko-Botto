package com.kuroneko.config;

import com.kuroneko.interaction.SlashInteractionManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Bot {

    private final JDA JDA;

    public Bot(SlashInteractionManager sim) {
        JDA = JDABuilder.createDefault(ConfigLoader.getConfig().getDiscordToken())
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .enableCache(CacheFlag.EMOJI, CacheFlag.VOICE_STATE)
                .addEventListeners(sim)
                .setActivity(Activity.listening("Ride on Time"))
                .build();
    }


}
