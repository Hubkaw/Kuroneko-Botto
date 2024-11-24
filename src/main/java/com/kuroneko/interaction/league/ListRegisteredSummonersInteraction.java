package com.kuroneko.interaction.league;

import com.kuroneko.database.entity.ChannelEntity;
import com.kuroneko.database.entity.RankEntity;
import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.database.repository.ChannelRepository;
import com.kuroneko.database.repository.SummonerRepository;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.KuronekoEmbed;
import com.kuroneko.misc.LeagueTierHelper;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ListRegisteredSummonersInteraction implements SlashInteraction {

    private ChannelRepository channelRepository;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        InteractionHook deferred = event.deferReply().setEphemeral(false).complete();
        if (event.getChannelId() == null){
            MessageEmbed build = new KuronekoEmbed().setTitle("Something went wrong").build();
            deferred.sendMessageEmbeds(build).setEphemeral(true).queue();
            return;
        }
        ChannelEntity channelEntity = channelRepository.findById(event.getChannelId()).orElse(null);

        if (channelEntity == null || channelEntity.getSummoners().isEmpty()){
            MessageEmbed build = new KuronekoEmbed().setTitle("Empty!").setDescription("There are no summoners registered to this channel. You can add them by using /register-lol interaction.").build();
            deferred.sendMessageEmbeds(build).queue();
            return;
        }

        StringBuilder sb = new StringBuilder();
        channelEntity.getSummoners().stream().limit(14).forEach( summoner -> {
            sb.append("**");
            sb.append(summoner.getRiotId());
            sb.append("** - ");
            sb.append(getHighestRank(summoner));
            sb.append("\n");
        });
        if (channelEntity.getSummoners().size() > 14){
            sb.append("and ");
            sb.append(channelEntity.getSummoners().size() - 14);
            sb.append(" more...");
        }

        MessageEmbed build = new KuronekoEmbed().setTitle("Registered Summoners").setDescription(sb.toString()).setFooter("Registered summoners: " + channelEntity.getSummoners().size()).build();
        deferred.sendMessageEmbeds(build).queue();
    }

    @Override
    public String getName() {
        return "list-lol";
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "List summoners registered to this channel");
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent ignore) {

    }

    private String getHighestRank(SummonerEntity summoner){
        RankEntity highestRank = summoner.getRanks().stream()
                .max((r1, r2) -> LeagueTierHelper.compareTiers(r1.getTier(), r2.getTier()))
                .orElse(null);
        if (highestRank == null){
            return "unranked";
        }
        String queue = highestRank.getQueue() == GameQueueType.RANKED_SOLO_5X5 ? "Solo Queue" : "Flex Queue";

        return highestRank.getTier().prettyName() + " - " + queue;

    }
}
