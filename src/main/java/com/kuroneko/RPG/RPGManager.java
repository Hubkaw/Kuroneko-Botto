package com.kuroneko.RPG;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.Map;
import java.util.TreeMap;

public class RPGManager extends ListenerAdapter {


    private final Map<String, RPGInteraction> interactionMap = new TreeMap<>();

    public RPGManager() {
        Reflections reflections = new Reflections("com.kuroneko.RPG");
        var rpgInteractions = reflections.getSubTypesOf(com.kuroneko.RPG.RPGInteraction.class).stream().map(c -> {
            try {
                return c.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.out.println("UNSUCCESSFUL RPG INTERACTIONS CREATION");
                e.printStackTrace();
                return null;
            }
        });
        rpgInteractions.forEach(mi -> interactionMap.put(mi.getName(), mi));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        RPGInteraction rpgInteractions = interactionMap.get(event.getName());

        if (rpgInteractions != null) {
            rpgInteractions.execute(event);
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        interactionMap.forEach((k, i) -> event.getGuild().upsertCommand(i.getCommand()).queue());
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        RPGInteraction rpgInteraction = interactionMap.get(event.getName());
        if (rpgInteraction != null && rpgInteraction.isAutoCompleted()) {
            rpgInteraction.autocomplete(event);
        }
    }
}
