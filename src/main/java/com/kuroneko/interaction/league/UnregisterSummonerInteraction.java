package com.kuroneko.interaction.league;

import com.kuroneko.database.entity.ChannelEntity;
import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.database.repository.ChannelRepository;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.KuronekoEmbed;
import com.kuroneko.service.DDragonService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.Permission;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
@AllArgsConstructor
public class UnregisterSummonerInteraction implements SlashInteraction {

    private ChannelRepository channelRepository;
    private DDragonService dDragonService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        InteractionHook deferred = event.deferReply().setEphemeral(false).complete();
        if (event.getMember() == null ||
                (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)
                        && !event.getMember().getPermissions(event.getGuildChannel()).contains(Permission.MANAGE_CHANNEL))) {
            MessageEmbed embed = new KuronekoEmbed().setTitle("No Sufficient Permission").setDescription("To use this command you need Administrator or Manage Channel permissions senpai.").build();
            deferred.sendMessageEmbeds(embed).queue();
            return;
        }
        OptionMapping option = event.getInteraction().getOption("riot-id");

        if (event.getChannelId() == null || option == null || option.getAsString().isBlank()){
            MessageEmbed build = new KuronekoEmbed().setTitle("Invalid Request").build();
            deferred.sendMessageEmbeds(build).queue();
            return;
        }

        ChannelEntity channelEntity = channelRepository.findById(event.getChannelId()).orElse(null);
        String name = option.getAsString();
        if (channelEntity == null || channelEntity.getSummoners().stream().noneMatch(s -> s.getRiotId().equalsIgnoreCase(name))){
            MessageEmbed build = new KuronekoEmbed().setTitle("No such Summoner").setDescription("Riot account " + name + " is not registered to this channel.").build();
            deferred.sendMessageEmbeds(build).queue();
            return;
        }


        SummonerEntity summonerEntity = channelEntity.getSummoners().stream().filter(s -> s.getRiotId().equalsIgnoreCase(name)).findFirst().get();
        String icon = dDragonService.getIconLink(summonerEntity.getIconId());
        channelEntity.getSummoners().remove(summonerEntity);
        channelRepository.save(channelEntity);

        MessageEmbed build = new KuronekoEmbed()
                .setTitle("Summoner Unregistered")
                .setDescription("I will no longer track progress of " +name + " on this channel")
                .setThumbnail(icon)
                .build();
        deferred.sendMessageEmbeds(build).queue();
    }

    @Override
    public String getName() {
        return "unregister-lol";
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "unregister a summoner")
                .addOption(OptionType.STRING,"riot-id", "Riot ID", true, true);
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        if (event.getMember() == null ||
                (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)
                        && !event.getMember().getPermissions(event.getGuildChannel()).contains(Permission.MANAGE_CHANNEL))) {
            event.replyChoices(Collections.emptyList()).queue();
            return;
        }

        ChannelEntity channelEntity = channelRepository.findById(event.getChannelId() == null ? "" : event.getChannelId()).orElse(null);
        if (channelEntity == null || channelEntity.getSummoners().isEmpty()){
            event.replyChoices(Collections.emptyList()).queue();
            return;
        }

        OptionMapping option = event.getOption("riot-id");
        if (option == null || option.getAsString().isBlank()){
            List<Command.Choice> choices = new ArrayList<>();
            channelEntity.getSummoners().forEach(summoner -> choices.add(new Command.Choice(summoner.getRiotId(), summoner.getRiotId())));
            event.replyChoices(choices).queue();
            return;
        }

        List<Command.Choice> choices = new ArrayList<>();
        channelEntity.getSummoners().stream()
                .filter(s -> s.getRiotId().startsWith(option.getAsString()))
                .forEach(summoner -> choices.add(new Command.Choice(summoner.getRiotId(), summoner.getRiotId())));
        event.replyChoices(choices).queue();
    }
}
