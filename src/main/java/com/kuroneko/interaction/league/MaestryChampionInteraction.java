package com.kuroneko.interaction.league;

import com.kuroneko.interaction.SlashInteraction;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class MaestryChampionInteraction implements SlashInteraction {
    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }

    @Override
    public String getName() {
        return "maestry-lol";
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Get Maesty Champion");
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {

    }
}
