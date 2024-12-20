package com.kuroneko.interaction.rp.inititative;

import com.kuroneko.interaction.ButtonInteraction;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.KuronekoEmbed;
import com.kuroneko.misc.RNG;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class InitiativeCheckInteraction implements SlashInteraction {

    private static final String CHARACTERS_OPTION = "characters";
    private InitiativeCheckManager icm;
    private ButtonInteraction finishButton;
    private RNG rng;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping option = event.getOption(CHARACTERS_OPTION);
        if (option == null || option.getAsString().isBlank()) {
            icm.addParticipant(event.getChannelId(), event.getMember().getEffectiveName(), 0);
            handleMessage(event);
            return;
        }

        List<Participant> participants = new ArrayList<>();
        String[] split = option.getAsString().split(",");
        for (String s : split) {
            String trimmed = s.trim();
            if (trimmed.isEmpty()) {
                sendParsingError(event);
                return;
            }
            try {
                List<Participant> parse = Participant.parse(trimmed, rng);
                participants.addAll(parse);
            } catch (Exception e) {
                sendParsingError(event);
                return;
            }
        }
        icm.addParticipants(event.getChannelId(), participants);
        handleMessage(event);
    }

    private void handleMessage(SlashCommandInteractionEvent event){

        InitiativeCheck initiativeCheck = icm.getInitiativeCheck(event.getChannelId());
        MessageEmbed embed = initiativeCheck.buildEmbed();

        if (initiativeCheck.getOriginalMessage() == null) {

            InteractionHook complete = event.replyEmbeds(embed)
                    .setActionRow(Collections.singleton(finishButton.getButton()))
                    .setEphemeral(false)
                    .complete();
            initiativeCheck.setOriginalMessage(complete);

        } else {
            initiativeCheck.getOriginalMessage().editOriginalEmbeds(embed).queue();

            MessageEmbed build = new KuronekoEmbed().setDescription("New Participants added")
                    .setThumbnail(null).build();

            event.replyEmbeds(build).setEphemeral(true).complete().deleteOriginal().queueAfter(3, TimeUnit.SECONDS);
        }

    }


    private void sendParsingError(SlashCommandInteractionEvent event){
        MessageEmbed build = new KuronekoEmbed().setTitle("Error")
                .setDescription("Please learn how to write senpai~")
                .build();
        event.replyEmbeds(build).setEphemeral(true).queue();
    }


    @Override
    public String getName() {
        return "initiative";
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Start a initiative check")
                .addOption(OptionType.STRING, CHARACTERS_OPTION, "Add characters to initiative check", true, false);
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {

    }
}
