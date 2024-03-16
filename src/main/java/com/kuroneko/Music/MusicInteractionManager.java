package com.kuroneko.Music;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MusicInteractionManager extends ListenerAdapter {

    private final Map<String, com.kuroneko.Music.MusicInteraction> interactionMap = new TreeMap<>();
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

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

        ReplyRemover executed = musicInteraction.execute(event);

        if (executed != null) {
            executorService.schedule(executed::getAction, executed.getDelay(), TimeUnit.SECONDS);
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        interactionMap.forEach((k, i) -> event.getGuild().upsertCommand(i.getCommand()).queue());
    }
}
