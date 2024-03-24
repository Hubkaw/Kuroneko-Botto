package com.kuroneko.Misc;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomCommand extends ListenerAdapter {

    private final String[] words = new String[]{"100", "10", "d6", "4d6", "12d4", "100d100"};

    private final Pattern dicePattern = Pattern.compile("([0-9]*)[d|k]([0-9]+)");
    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().upsertCommand(Commands.slash("roll", "Roll dice")
                .addOption(OptionType.STRING, "rolls", "e.g. d100, 4d6, 2k10", true, true)).queue();
        super.onGuildReady(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("roll") && event.getFocusedOption().getName().equals("rolls")) {
            List<Command.Choice> options = Stream.of(words)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("roll")){
            try {
                OptionMapping dices = event.getInteraction().getOption("rolls");
                if (dices == null) {
                    event.replyEmbeds(rollError()).queue();
                    return;
                }
                Matcher matcher = dicePattern.matcher(dices.getAsString());
                if (matcher.matches()) {

                    int die = matcher.group(1).isBlank() ? 1 : Integer.parseInt(matcher.group(1));
                    int size = Integer.parseInt(matcher.group(2));
                    MessageEmbed embed = rollMessage(die, size, event.getMember().getEffectiveName());
                    event.replyEmbeds(embed).queue();
                    return;
                }
                int size = Integer.parseInt(dices.getAsString());
                MessageEmbed embed = rollMessage(1, size, event.getMember().getEffectiveName());
                event.replyEmbeds(embed).queue();
            } catch (Exception e) {
                event.replyEmbeds(rollError()).queue();
            }
        }
    }

    private MessageEmbed rollMessage(int diceAmount, int diceSize, String author) {
        if (diceSize > 1000 || diceAmount > 100) {
            return rollError();
        }

        int[] rolls = new int[diceAmount];
        for (int i = 0; i < diceAmount; i++) {
            rolls[i] = random.nextInt(diceSize) + 1;
        }

        StringBuilder sb = new StringBuilder("[ ");

        int sum = 0;

        for (int i = 0; i < diceAmount; i++) {
            if (i != 0){
                sb.append(" -");
            }
            sb.append(" ").append(rolls[i]).append(" ");
            sum+= rolls[i];
        }
        sb.append("] ");
        if (diceAmount > 1){
            String avg = new DecimalFormat("#.###").format(((double) sum) / ((double) diceAmount));
            sb.append("- [SUM: ").append(sum).append("] - [AVG: ").append(avg).append("] ");
        }
        return new EmbedBuilder()
                .setTitle("Roll " + diceAmount + "d" + diceSize + " for " + author)
                .setDescription(sb.toString())
                .setColor(new Color(110, 0, 127))
                .build();
    }

    private MessageEmbed rollError(){
        return new EmbedBuilder()
                .setTitle("Error")
                .setDescription("Wrong format baaaaaka~~")
                .setColor(new Color(110, 0, 127))
                .build();
    }
}
