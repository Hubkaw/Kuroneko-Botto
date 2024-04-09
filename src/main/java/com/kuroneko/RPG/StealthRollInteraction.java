package com.kuroneko.RPG;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class StealthRollInteraction extends RollInteraction{
    @Override
    protected void sendMessage(SlashCommandInteractionEvent event, MessageEmbed embed) {
        event.replyEmbeds(embed).setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "stealth-roll";
    }
}
