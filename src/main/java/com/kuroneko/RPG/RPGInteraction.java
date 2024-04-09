package com.kuroneko.RPG;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface RPGInteraction {
    void execute(SlashCommandInteractionEvent event);
    String getName();
    CommandData getCommand();
    boolean isAutoCompleted();
    void  autocomplete(CommandAutoCompleteInteractionEvent event);
}
