package com.kuroneko.interaction.rp.character;

import com.kuroneko.database.entity.ChannelEntity;
import com.kuroneko.database.entity.PlayerCharacterEntity;
import com.kuroneko.database.mapper.ChannelMapper;
import com.kuroneko.database.mapper.StatMapper;
import com.kuroneko.database.repository.ChannelRepository;
import com.kuroneko.database.repository.PlayerCharacterRepository;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.Ability;
import com.kuroneko.misc.RPGInteractionUtils;
import com.kuroneko.misc.KuronekoEmbed;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Component
public class RegisterCharacterInteraction implements SlashInteraction {

    private final ChannelRepository channelRepository;
    private PlayerCharacterRepository playerCharacterRepository;


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Map<Ability, Integer> abilityMap;
        try {
            abilityMap = mapOptions(event);
        } catch (ArithmeticException e) {
            MessageEmbed embed = new KuronekoEmbed()
                    .setTitle("Błąd")
                    .setDescription("Nie mogę zapisać tej postaci. Upewnij się że statystyki są w przedziale 0-100.")
                    .build();
            event.replyEmbeds(embed).setEphemeral(true).queue();
            return;
        }

        ChannelEntity channelEntity = channelRepository.findById(event.getChannel().getId()).orElse(null);
        if (channelEntity == null) {
            channelEntity = channelRepository.save(ChannelMapper.map(event.getGuildChannel()));
        }

        if (playerCharacterRepository.existsById(new PlayerCharacterEntity.Pk(event.getUser().getId(), channelEntity.getChannelId()))) {
            MessageEmbed embed = new KuronekoEmbed()
                    .setTitle("Błąd")
                    .setDescription("Już masz postać przypisaną do tego kanału. Żeby ją usunąć użyj **/reset-character**, żeby zmienić statystykę użyj **/set-stat**.")
                    .build();
            event.replyEmbeds(embed).setEphemeral(true).queue();
            return;
        }
        PlayerCharacterEntity playerCharacterEntity = playerCharacterRepository.save(createCharacter(event.getUser().getId(), event.getOption("name").getAsString(), channelEntity, abilityMap));

        MessageEmbed build = new EmbedBuilder()
                .setColor(new Color(110, 0, 127))
                .setTitle("Bohater zapisany")
                .setDescription(event.getUser().getEffectiveName() + " przypisał bohatera " + playerCharacterEntity.getName() + " jako swoją postać na tym kanale." + " \n" + RPGInteractionUtils.writeCharacterDescription(playerCharacterEntity.getStats()))
                .build();
        event.replyEmbeds(build).queue();
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public CommandData getCommand() {
        SlashCommandData commandData = Commands.slash(getName(), "Zarejestruj bohatera");
        commandData.addOption(OptionType.STRING, "name", "Imie bohatera", true);
        Ability.getAbilities().forEach(a -> commandData.addOption(OptionType.INTEGER, a.name().toLowerCase(), a.fullName, true, false));
        return commandData;
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent ignore) {
    }

    private Map<Ability, Integer> mapOptions(SlashCommandInteractionEvent event) throws ArithmeticException {
        Map<Ability, Integer> abilityIntegerMap = new HashMap<>();
        Ability.getAbilities().forEach(a -> {
            int value = event.getOption(a.name().toLowerCase()).getAsInt();
            if (value < 0 || value > 100) {
                throw new ArithmeticException();
            }
            abilityIntegerMap.put(a, value);
        });
        return abilityIntegerMap;
    }

    private PlayerCharacterEntity createCharacter(String userId, String name, ChannelEntity channel, Map<Ability, Integer> stats) {
        PlayerCharacterEntity playerCharacterEntity = new PlayerCharacterEntity();
        playerCharacterEntity.setChannel(channel);
        playerCharacterEntity.setName(name);
        playerCharacterEntity.setOwner(userId);
        playerCharacterEntity.setStats(StatMapper.map(stats));
        return playerCharacterEntity;
    }
}
