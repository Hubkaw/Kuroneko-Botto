package com.kuroneko.interaction.rp.roll;

import com.kuroneko.interaction.SlashInteraction;
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
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class RollInteraction implements SlashInteraction {

    private final String[] words = new String[]{"100", "20", "10", "d6", "12d4", "100d100"};

    private final Pattern dicePattern = Pattern.compile("([0-9]*)[d|k]([0-9]+)");


    @Override
    public abstract String getName();

    abstract void sendMessage(SlashCommandInteractionEvent event, MessageEmbed embed);

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        String value = event.getFocusedOption().getValue();
        if (value.isBlank()) {
            event.replyChoices(Arrays.stream(words).map(word -> new Command.Choice(word, word)).toList()).queue();
            return;
        }
        if (value.matches("([0-9]+)?[d|k|D|K]")){
            event.replyChoices(
                    new Command.Choice(value + "100", value + "100"),
                    new Command.Choice(value + "20", value + "20"),
                    new Command.Choice(value + "10", value + "10"),
                    new Command.Choice(value + "8", value + "8"),
                    new Command.Choice(value + "6", value + "6"),
                    new Command.Choice(value + "4", value + "4"))
                    .queue();
            return;
        }
        if (value.matches("([0-9]+)?([d|k|D|K])?[0-9]+")){
            event.replyChoices(new Command.Choice(value, value), new Command.Choice(value + "0", value + "0"), new Command.Choice(value + "00", value + "00")).queue();
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
                Matcher matcher = dicePattern.matcher(dices.getAsString());
                if (matcher.matches()) {

                    int die = matcher.group(1).isBlank() ? 1 : Integer.parseInt(matcher.group(1));
                    int size = Integer.parseInt(matcher.group(2));
                    MessageEmbed embed = rollMessage(die, size, event.getMember().getEffectiveName());
                    sendMessage(event, embed);
                    return;
                }
                int size = Integer.parseInt(dices.getAsString());
                MessageEmbed embed = rollMessage(1, size, event.getMember().getEffectiveName());
                sendMessage(event, embed);
            } catch (Exception e) {
                sendMessage(event, rollError());
            }
        }
    }

    private MessageEmbed rollMessage(int diceAmount, int diceSize, String author) {
        if (diceSize > 1000 || diceAmount > 100) {
            return rollError();
        }

        int[] rolls = new int[diceAmount];
        for (int i = 0; i < diceAmount; i++) {
            rolls[i] = roll(diceSize) + 1;
        }

        StringBuilder sb = new StringBuilder("[ ");

        int sum = 0;

        for (int i = 0; i < diceAmount; i++) {
            if (i != 0) {
                sb.append(" -");
            }
            sb.append(" ").append(rolls[i]).append(" ");
            sum += rolls[i];
        }
        sb.append("] ");
        if (diceAmount > 1) {
            String avg = new DecimalFormat("#.###").format(((double) sum) / ((double) diceAmount));
            sb.append("- [SUM: ").append(sum).append("] - [AVG: ").append(avg).append("] ");
        }
        return new EmbedBuilder()
                .setTitle("Roll " + diceAmount + "d" + diceSize + " for " + author)
                .setDescription(sb.toString())
                .setColor(new Color(110, 0, 127))
                .build();
    }

    private MessageEmbed rollError() {
        return new EmbedBuilder()
                .setTitle("Error")
                .setDescription("Wrong format baaaaaka~~")
                .setColor(new Color(110, 0, 127))
                .build();
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Roll dice")
                .addOption(OptionType.STRING, "rolls", "e.g. d100, 4d6, 2k10", true, true);
    }

    abstract int roll(int cap);

}
