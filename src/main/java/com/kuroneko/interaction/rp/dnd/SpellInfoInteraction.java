package com.kuroneko.interaction.rp.dnd;

import com.kuroneko.database.entity.DnDSpellEntity;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.KuronekoEmbed;
import com.kuroneko.misc.RNG;
import com.kuroneko.misc.SpellDetailsResponse;
import com.kuroneko.service.DnDRulebookService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class SpellInfoInteraction implements SlashInteraction {

    private final DnDRulebookService rulebookService;
    private final SingleSpellMessageHandler singleSpellHandler;
    private final SpellListMessageHandler spellListHandler;
    private final RNG rng;

    private static final String SPELL_NAME_OPTION = "spell-name";

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        InteractionHook deferred = event.deferReply().complete();

        String spellname = getOptionValue(event.getOption(SPELL_NAME_OPTION));
        if (spellname == null) {
            MessageEmbed messageEmbed = errorEmbed("You must learn to write senpai :3");
            deferred.sendMessageEmbeds(messageEmbed).queue();
            return;
        }

        List<DnDSpellEntity> spells = rulebookService.findSpells(spellname);

        if (spells.isEmpty()) {
            MessageEmbed messageEmbed = errorEmbed("No such spell found, maybe read the rulebook for once, you big baaka~");
            deferred.sendMessageEmbeds(messageEmbed).queue();
            return;
        }


        DnDSpellEntity spell = spells.stream().filter(s -> s.getName().equals(spellname)).findFirst().orElse(null);

        if (spells.size() == 1 || spell != null) {
            MessageEmbed embed = singleSpellHandler.handle(rulebookService.fetchFullSpell(spell == null ? spells.get(0) : spell));
            deferred.sendMessageEmbeds(embed).queue();
            return;
        }

        MessageEmbed embed = spellListHandler.handle(spells);
        deferred.sendMessageEmbeds(embed).queue();

    }


    @Override
    public String getName() {
        return "dnd-spell";
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Fetch info of matching spell")
                .addOption(OptionType.STRING, SPELL_NAME_OPTION, "Spell name", true, true);
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        OptionMapping option = event.getOption(SPELL_NAME_OPTION);
        if (option == null || option.getAsString().isBlank()) {
            List<DnDSpellEntity> allSpells = rulebookService.findAllSpells();
            List<Command.Choice> list = allSpells.stream()
                    .filter(s -> rng.rollInt(100) <= 2)
                    .limit(25)
                    .map(s -> new Command.Choice(s.getName(), s.getName())).toList();
            event.replyChoices(list).queue();
            return;
        }
        String asString = option.getAsString();
        List<DnDSpellEntity> foundSpells = rulebookService.findSpells(asString);
        List<Command.Choice> list = foundSpells.stream()
                .filter(s -> s.getName().toLowerCase().contains(asString.toLowerCase()))
                .limit(25)
                .map(s -> new Command.Choice(s.getName(), s.getName()))
                .toList();
        event.replyChoices(list).queue();
    }

    private String getOptionValue(OptionMapping option) {
        if (option == null) {
            return null;
        }
        String asString = option.getAsString();
        if (asString.isEmpty()) {
            return null;
        }
        return asString;
    }

    private MessageEmbed errorEmbed(String message) {
        return new KuronekoEmbed().setTitle("We have an error").setDescription(message).build();
    }


}
