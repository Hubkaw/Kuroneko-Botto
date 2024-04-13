package com.kuroneko.interaction.rp.character;

import com.kuroneko.database.entity.PlayerCharacterEntity;
import com.kuroneko.database.repository.PlayerCharacterRepository;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.KuronekoEmbed;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ChangeCharacterImageInteraction implements SlashInteraction {

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
        String link = event.getOption("link").getAsString();
        try {
            new URL(link);
        } catch (MalformedURLException e) {
            MessageEmbed build = new KuronekoEmbed().setTitle("Błąd").setDescription("Niepoprawny link").build();
            event.replyEmbeds(build).setEphemeral(true).queue();
            return;
        }

        PlayerCharacterEntity playerCharacterEntity = character.get();
        playerCharacterEntity.setImageLink(link);
        characterRepository.save(playerCharacterEntity);

        MessageEmbed build = new KuronekoEmbed().setTitle("Dodano obrazek")
                .setThumbnail(link)
                .build();
        event.replyEmbeds(build).setEphemeral(true).queue();

    }

    @Override
    public String getName() {
        return "set-image";
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Ustaw obrazek swojej postaci")
                .addOption(OptionType.STRING, "link", "Link do obrazu", true);
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {

    }
}
