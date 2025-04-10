package com.kuroneko.interaction;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


@Component
public class SlashInteractionManager extends ListenerAdapter {
    private final Map<String, SlashInteraction> interactionMap = new TreeMap<>();
    private SlashInteractionLogger logger;

    public SlashInteractionManager(Set<SlashInteraction> slashInteractions,
                                   SlashInteractionLogger logger) {
        this.logger = logger;
        slashInteractions.forEach(mi -> interactionMap.put(mi.getName(), mi));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashInteraction slashInteraction = interactionMap.get(event.getName());

        if (slashInteraction != null) {
            logger.log(event);
            slashInteraction.execute(event);
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        SlashInteraction slashInteraction = interactionMap.get(event.getName());

        if (slashInteraction != null) {
            slashInteraction.autoComplete(event);
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        interactionMap.forEach((k, i) -> event.getGuild().upsertCommand(i.getCommand()).queue());
    }
}
