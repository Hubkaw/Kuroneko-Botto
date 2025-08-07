package com.kuroneko.interaction.rp.roll;

import com.kuroneko.database.entity.MemberChannelEntity;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.RNG;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

abstract class RollInteraction implements SlashInteraction {

    private final String[] words = new String[]{"100", "20", "10", "d6", "12d4", "100d100"};

    @Override
    public abstract String getName();

    abstract void sendMessage(SlashCommandInteractionEvent event, MessageEmbed embed);

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Roll dice")
                .addOption(OptionType.STRING, "rolls", "e.g. d100, 4d6, 2k10", true, true);
    }

    abstract RNG getRNG();

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        String value = event.getFocusedOption().getValue();
        if (value.contains(",") || value.contains(" ") || value.contains("+") || value.contains("?")) {
            event.replyChoices(createChoices(value, "")).queue();
            return;
        }
        if (value.isBlank()) {
            event.replyChoices(createChoices("", words)).queue();
            return;
        }
        if (value.matches("([0-9]+)?([dkeDKE])?1")) {
            event.replyChoices(
                    createChoices(value, "00", "2", "0")
            ).queue();
            return;
        }
        if (value.matches("([0-9]+)?[dkeDKE]")) {
            event.replyChoices(
                    createChoices(value, "100", "20", "10", "8", "6", "4")
            ).queue();
            return;
        }
        if (value.matches("([0-9]+)?([dkeDKE])?[0-9]+")) {
            event.replyChoices(new Command.Choice(value, value), new Command.Choice(value + "0", value + "0"), new Command.Choice(value + "00", value + "00")).queue();
            return;
        }
        if (value.matches("(([0-9]*)?[dkeDKE])?([0-9]+)([*])?([+-])?([0-9]+)?")) {
            event.replyChoices(new Command.Choice(value, value)).queue();
            return;
        }
        event.replyChoices(Arrays.stream(words).map(word -> new Command.Choice(word, word)).toList()).queue();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getName().equals(getName())) {
            try {
                OptionMapping dices = event.getInteraction().getOption("rolls");
                if (dices == null) {
                    sendMessage(event, rollError());
                    return;
                }
                String dicesAsString = dices.getAsString();
                String[] split = dicesAsString.split("[?,\s]\s*");
                StringBuilder sb = new StringBuilder();

                List<Dices> dicesList = Stream.of(split).map(s -> new Dices(s, getRNG())).toList();
                if (dicesList.isEmpty()) {
                    sendMessage(event, rollError());
                    return;
                }

                dicesList.forEach(d -> sb.append(d.getAsString()));


                if (dicesList.size() > 1) {
                    AtomicInteger total = new AtomicInteger(0);
                    dicesList.forEach(d -> total.getAndAdd(d.getSum()));
                    sb.append("Total: ").append(total.get());
                }

                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle("Roll for " + event.getMember().getEffectiveName())
                        .setDescription(sb.toString())
                        .setColor(new Color(110, 0, 127));

                MemberChannelEntity memberChannel = getMemberChannel(event);
                if (memberChannel != null && !memberChannel.getRollImageLink().isBlank()) {
                    builder.setThumbnail(memberChannel.getRollImageLink());
                } else {
                    builder.setThumbnail(event.getUser().getAvatarUrl());
                }

                sendMessage(event, builder.build());
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(event, rollError());
            }
        }
    }


    private MessageEmbed rollError() {
        return new EmbedBuilder()
                .setTitle("Error")
                .setDescription("Wrong format baaaaaka~~")
                .setColor(new Color(110, 0, 127))
                .build();
    }

    private List<Command.Choice> createChoices(String value, String... mods) {
        ArrayList<Command.Choice> output = new ArrayList<>();
        for (int i = 0; i < mods.length; i++) {
            output.add(modifyChoice(value, mods[i]));
        }
        return output;
    }

    private Command.Choice modifyChoice(String value, String mod) {
        return new Command.Choice(value + mod, value + mod);
    }

    protected abstract MemberChannelEntity getMemberChannel(SlashCommandInteractionEvent event);
}
