package com.kuroneko.interaction;

import com.kuroneko.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


@Component
public class SlashInteractionManager extends ListenerAdapter {
    private final PlayerManager playerManager;
    private final Map<String, SlashInteraction> interactionMap = new TreeMap<>();

    public SlashInteractionManager(Set<SlashInteraction> slashInteractions,
                                   PlayerManager playerManager) {
        this.playerManager = playerManager;
        slashInteractions.forEach(mi -> interactionMap.put(mi.getName(), mi));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashInteraction slashInteraction = interactionMap.get(event.getName());

        if (slashInteraction != null) {
            slashInteraction.execute(event);
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        SlashInteraction slashInteraction = interactionMap.get(event.getName());

        if (slashInteraction != null){
            slashInteraction.autoComplete(event);
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        interactionMap.forEach((k, i) -> event.getGuild().upsertCommand(i.getCommand()).queue());
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getJDA().getSelfUser().getId().equals(event.getMember().getId())
        && (event.getNewValue() == null || event.getChannelJoined() == null)){
            playerManager.getMusicManager(event.getGuild()).scheduler.skipAll();
        }
    }
}
