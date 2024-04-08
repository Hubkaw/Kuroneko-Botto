package com.kuroneko.Music;

import com.kuroneko.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.Map;
import java.util.TreeMap;

public class MusicInteractionManager extends ListenerAdapter {

    private final Map<String, com.kuroneko.Music.MusicInteraction> interactionMap = new TreeMap<>();

    public MusicInteractionManager() {
        Reflections reflections = new Reflections("com.kuroneko.Music");
        var musicInteractions = reflections.getSubTypesOf(com.kuroneko.Music.MusicInteraction.class).stream().map(c -> {
            try {
                return c.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.out.println("UNSUCCESSFUL MUSIC INTERACTIONS CREATION");
                e.printStackTrace();
                return null;
            }
        });
        musicInteractions.forEach(mi -> interactionMap.put(mi.getName(), mi));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        MusicInteraction musicInteraction = interactionMap.get(event.getName());

        if (musicInteraction == null) {
            return;
        }

        musicInteraction.execute(event);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        interactionMap.forEach((k, i) -> event.getGuild().upsertCommand(i.getCommand()).queue());
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getJDA().getSelfUser().getId().equals(event.getMember().getId())
        && (event.getNewValue() == null || event.getChannelJoined() == null)){
            PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.skipAll();
        }
        super.onGuildVoiceUpdate(event);
    }
}
