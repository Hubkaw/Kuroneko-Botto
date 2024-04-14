package com.kuroneko.interaction.rp.character;

import com.kuroneko.database.repository.PlayerCharacterRepository;
import com.kuroneko.misc.RNG;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class PublicCheckStatInteraction extends CheckStatInteraction {

    public PublicCheckStatInteraction(PlayerCharacterRepository pcr,
                                      RNG rng){
        super(pcr, rng);
    }

    @Override
    public String getName() {
        return "check";
    }

    @Override
    void sendMessage(SlashCommandInteractionEvent event, MessageEmbed embed) {
        event.replyEmbeds(embed).queue();
    }
}
