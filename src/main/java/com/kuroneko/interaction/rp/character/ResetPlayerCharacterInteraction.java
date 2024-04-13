package com.kuroneko.interaction.rp.character;

import com.kuroneko.database.entity.PlayerCharacterEntity;
import com.kuroneko.database.repository.PlayerCharacterRepository;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.KuronekoEmbed;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ResetPlayerCharacterInteraction implements SlashInteraction {

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
        String name = character.get().getName();
        String link = character.get().getImageLink();
        characterRepository.delete(character.get());
        EmbedBuilder embedBuilder = new KuronekoEmbed().setTitle(name + " został usunięty");
        if (link != null){
            embedBuilder.setThumbnail(link);
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public String getName() {
        return "reset-character";
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Usuń swojego bohatera z kanału");
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {

    }
}
