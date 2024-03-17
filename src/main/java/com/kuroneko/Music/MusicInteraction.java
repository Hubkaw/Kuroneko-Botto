package com.kuroneko.Music;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface MusicInteraction {
    void execute(SlashCommandInteractionEvent event);
    String getName();
    CommandData getCommand();
}
