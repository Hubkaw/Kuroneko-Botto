package com.kuroneko.interaction.rp.character;

import com.kuroneko.database.entity.PlayerCharacterEntity;
import com.kuroneko.database.repository.PlayerCharacterRepository;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.RPGInteractionUtils;
import com.kuroneko.misc.KuronekoEmbed;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ShowCharacterInfo implements SlashInteraction {

    private PlayerCharacterRepository characterRepository;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String user = event.getUser().getId();
        Optional<PlayerCharacterEntity> character = characterRepository.findById(new PlayerCharacterEntity.Pk(user,event.getChannel().getId()));
        if (character.isEmpty()){
            MessageEmbed build = new KuronekoEmbed().setTitle("Błąd").setDescription("Nie posiadasz postaci przypisanej do tego kanału. Użyj /register aby stworzyć nową.").build();
            event.replyEmbeds(build).setEphemeral(true).queue();
            return;
        }

        String description = RPGInteractionUtils.writeDescription(character.get().getStats());

        MessageEmbed build = new EmbedBuilder()
                .setColor(new Color(110, 0, 127))
                .setTitle("Postać " + character.get().getName() + " stworzona przez " + event.getUser().getEffectiveName())
                .setDescription(description)
                .build();
        event.replyEmbeds(build).queue();


    }

    @Override
    public String getName() {
        return "character-info";
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Show character stats");
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {

    }
}
