package com.kuroneko.config;

import com.kuroneko.interaction.SlashInteractionManager;
import com.kuroneko.misc.VoiceChannelUpdateHandler;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Bot {

    private final JDA JDA;

    public Bot(SlashInteractionManager sim,
               VoiceChannelUpdateHandler vcuh) {
        JDA = JDABuilder.createDefault(ConfigLoader.getConfig().getDiscordToken())
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .enableCache(CacheFlag.EMOJI, CacheFlag.VOICE_STATE)
                .addEventListeners(sim)
                .addEventListeners(vcuh)
                .setActivity(Activity.listening("Ride on Time"))
                .build();
    }

    @Bean
    public JDA getJDA() {
        return JDA;
    }
}
