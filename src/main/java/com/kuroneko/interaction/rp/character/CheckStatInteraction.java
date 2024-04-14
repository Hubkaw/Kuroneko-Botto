package com.kuroneko.interaction.rp.character;

import com.kuroneko.database.entity.CharacterStatsEntity;
import com.kuroneko.database.entity.PlayerCharacterEntity;
import com.kuroneko.database.repository.PlayerCharacterRepository;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.Ability;
import com.kuroneko.misc.KuronekoEmbed;
import com.kuroneko.misc.RNG;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import java.util.List;
import java.util.Optional;

abstract class CheckStatInteraction implements SlashInteraction {

    private final PlayerCharacterRepository characterRepository;
    private final List<String> abilityNames;
    private final RNG rng;

    public CheckStatInteraction(PlayerCharacterRepository playerCharacterRepository,
                                RNG rng) {
        this.characterRepository = playerCharacterRepository;
        this.rng = rng;
        abilityNames = Ability.getAbilities().stream().map(Enum::name).toList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String user = event.getUser().getId();

        Optional<PlayerCharacterEntity> character = characterRepository.findById(new PlayerCharacterEntity.Pk(user,event.getChannel().getId()));
        if (character.isEmpty()){
            MessageEmbed build = new KuronekoEmbed().setTitle("Błąd").setDescription("Nie posiadasz postaci przypisanej do tego kanału. Użyj /register aby stworzyć nową.").build();
            event.replyEmbeds(build).setEphemeral(true).queue();
            return;
        }
        if (abilityNames.stream().noneMatch(a -> a.equalsIgnoreCase(event.getOption("stat").getAsString()))){
            MessageEmbed build = new KuronekoEmbed().setTitle("Błąd").setDescription("Niepoprawna statystyka").build();
            event.replyEmbeds(build).setEphemeral(true).queue();
            return;
        }
        if (event.getOption("bonus") != null && event.getOption("bonus").getAsInt() < -30 && event.getOption("bonus").getAsInt() > 30) {
            MessageEmbed build = new KuronekoEmbed().setTitle("Błąd").setDescription("Niepoprawna wartość bonusu").build();
            event.replyEmbeds(build).setEphemeral(true).queue();
            return;
        }
        int characterStat = getCharacterStat(character.get(), event.getOption("stat").getAsString());
        int bonus;
        try {
            bonus = event.getOption("bonus").getAsInt();
        } catch (Exception e){
            bonus = 0;
        }
        int dice = rng.rollInt(100) + 1;

        boolean hasPassed = characterStat + bonus >= dice;

        String successTier = "";

        if (hasPassed){
            int diff = characterStat + bonus - dice;
            int successTierInt = Math.floorDiv(diff, 10);
            if (successTierInt == 0){
                successTier = "Równy sukces";
            } else {
                successTier = "Poziom sukcesu: **" + successTierInt + "**";
            }
        } else {
            int diff = (characterStat + bonus - dice) * -1;
            int successTierInt = Math.floorDiv(diff, 10);
            if (successTierInt == 0){
                successTier = "Bliska porażka";
            } else {
                successTier = "Poziom porażki: **" + successTierInt + "**";
            }
        }

        String desc = character.get().getName() + (hasPassed ? " **zdał** test" : " **nie zdał** testu") + " umiejętności: **" + Ability.valueOf(event.getOption("stat").getAsString().toUpperCase()).fullName + "**\n" +
                "Trudność: **" + dice + "** Umiejętność: **" + characterStat
                + (bonus != 0 ? "** Bonus: **" + bonus : "") + "**\n" + successTier;

        EmbedBuilder embedBuilder = new KuronekoEmbed().setTitle("Test Umiejętności " + character.get().getName() + (hasPassed ? " :white_check_mark:" : " :cross_mark:"))
                .setDescription(desc);
        if (character.get().getImageLink() != null){
            embedBuilder.setThumbnail(character.get().getImageLink());
        }
        sendMessage(event, embedBuilder.build());
    }

    @Override
    public abstract String getName();

    @Override
    public CommandData getCommand() {

        return Commands.slash(getName(), "Test umiejętności")
                .addOption(OptionType.STRING, "stat", "wybierz umiejętność", true, true)
                .addOption(OptionType.INTEGER, "bonus", "bonus sytuacyjny", false);
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        if (event.getFocusedOption().getName().equals("stat")) {
            List<Command.Choice> list = abilityNames.stream()
                    .filter(s -> s.toLowerCase().startsWith(event.getOption("stat").getAsString().toLowerCase()))
                    .map(s -> new Command.Choice(s, s))
                    .toList();
            if (list.isEmpty()) {
                List<Command.Choice> choices = abilityNames.stream()
                        .map(a -> new Command.Choice(a, a))
                        .toList();
                event.replyChoices(choices).queue();
            } else {
                event.replyChoices(list).queue();
            }
        }
    }

    private int getCharacterStat(PlayerCharacterEntity pce, String ability){
        CharacterStatsEntity stats = pce.getStats();
        switch (ability.toLowerCase()) {
            case "ww" -> {
                return stats.getWarriorSkill();
            }
            case "us" -> {
                return stats.getBallisticSkill();
            }
            case "odp" -> {
                return stats.getToughness();
            }
            case "k" -> {
                return stats.getStrength();
            }
            case "zr" -> {
                return stats.getAgility();
            }
            case "int" -> {
                return stats.getIntelligence();
            }
            case "sw" -> {
                return stats.getWillPower();
            }
            case "ogd" -> {
                return stats.getFelicity();
            }
            default -> {
                return 101;
            }
        }
    }

    abstract void sendMessage(SlashCommandInteractionEvent event, MessageEmbed embed);

}
