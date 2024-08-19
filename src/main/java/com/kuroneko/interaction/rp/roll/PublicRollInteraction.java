package com.kuroneko.interaction.rp.roll;


import com.kuroneko.misc.RNG;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PublicRollInteraction extends RollInteraction {

    private RNG rng;

    @Override
    void sendMessage(SlashCommandInteractionEvent event, MessageEmbed embed) {
        event.replyEmbeds(embed).setEphemeral(false).queue();
    }

    @Override
    RNG getRNG() {
        return rng;
    }

    @Override
    public String getName() {
        return "roll";
    }
}
