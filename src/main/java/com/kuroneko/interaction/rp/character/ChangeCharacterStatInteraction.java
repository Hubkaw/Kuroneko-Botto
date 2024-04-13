package com.kuroneko.interaction.rp.character;

import com.kuroneko.database.entity.PlayerCharacterEntity;
import com.kuroneko.database.repository.PlayerCharacterRepository;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.Ability;
import com.kuroneko.misc.KuronekoEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ChangeCharacterStatInteraction implements SlashInteraction {

    private final PlayerCharacterRepository characterRepository;
    private final List<String> abilityNames;

    public ChangeCharacterStatInteraction(PlayerCharacterRepository playerCharacterRepository) {
        this.characterRepository = playerCharacterRepository;
        abilityNames = Ability.getAbilities().stream().map(Enum::name).toList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String user = event.getUser().getId();

        Optional<PlayerCharacterEntity> character = characterRepository.findById(new PlayerCharacterEntity.Pk(user, event.getChannel().getId()));
        if (character.isEmpty()) {
            MessageEmbed build = new KuronekoEmbed().setTitle("Błąd").setDescription("Nie posiadasz postaci przypisanej do tego kanału. Użyj /register aby stworzyć nową.").build();
            event.replyEmbeds(build).setEphemeral(true).queue();
            return;
        }
        int value = event.getOption("value").getAsInt();
        if (value < 0 || value > 100) {
            MessageEmbed embed = new KuronekoEmbed()
                    .setTitle("Błąd")
                    .setDescription("Nie mogę zapisać tej statystyki. Upewnij się że jest w przedziale 0-100.")
                    .build();
            event.replyEmbeds(embed).setEphemeral(true).queue();
            return;
        }


        try {
            setCharacterStat(character.get(), event.getOption("stat").getAsString().toLowerCase(), value);
            EmbedBuilder embedBuilder = new KuronekoEmbed()
                    .setTitle("Zmieniono statystykę")
                    .setDescription("Ustawiono " + Ability.valueOf(event.getOption("stat").getAsString().toUpperCase()).fullName + " na " + value);
            if (character.get().getImageLink()!= null){
                embedBuilder.setThumbnail(character.get().getImageLink());
            }
            event.replyEmbeds(embedBuilder.build()).setEphemeral(false).queue();
        } catch (NoSuchFieldException e) {
            MessageEmbed embed = new KuronekoEmbed()
                    .setTitle("Błąd")
                    .setDescription("Nie mogę zapisać tej postaci. Sprawdź poprawność skrótu statystyki: "+ event.getOption("stat").getAsString())
                    .build();
            event.replyEmbeds(embed).setEphemeral(true).queue();
        }

    }

    @Override
    public String getName() {
        return "set-stat";
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Zmień wartość statystyki")
                .addOption(OptionType.STRING, "stat", "Skrót statystyki", true, true)
                .addOption(OptionType.INTEGER, "value", "Nowa wartość", true);
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("set-stat") && event.getFocusedOption().getName().equals("stat")) {
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

    private void setCharacterStat(PlayerCharacterEntity pce, String ability, int value) throws NoSuchFieldException {
        switch (ability.toLowerCase()) {
            case "ww" -> pce.getStats().setWarriorSkill(value);

            case "us" -> pce.getStats().setBallisticSkill(value);

            case "odp" -> pce.getStats().setToughness(value);

            case "k" -> pce.getStats().setStrength(value);

            case "zr" -> pce.getStats().setAgility(value);

            case "int" -> pce.getStats().setIntelligence(value);

            case "sw" -> pce.getStats().setWillPower(value);

            case "ogd" -> pce.getStats().setFelicity(value);

            default -> throw new NoSuchFieldException("");
        }
        characterRepository.save(pce);
    }
}
