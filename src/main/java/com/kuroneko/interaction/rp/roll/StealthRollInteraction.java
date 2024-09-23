package com.kuroneko.interaction.rp.roll;

import com.kuroneko.misc.RNG;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StealthRollInteraction extends RollInteraction{

    private RNG rng;
    private final String GM_OPTION = "game-master";

    @Override
    protected void sendMessage(SlashCommandInteractionEvent event, MessageEmbed embed) {
        event.replyEmbeds(embed).setEphemeral(true).queue();
        OptionMapping option = event.getInteraction().getOption(GM_OPTION);
        if (option != null) {
            option.getAsUser().openPrivateChannel().complete().sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    RNG getRNG() {
        return rng;
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Roll dice")
                .addOption(OptionType.STRING, "rolls", "e.g. d100, 4d6, 2k10", true, true)
                .addOption(OptionType.USER, GM_OPTION, "e.g. @Kuroneko", false, false);
    }

    @Override
    public String getName() {
        return "stealth-roll";
    }
}
