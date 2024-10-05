package com.kuroneko.interaction.rp.roll;

import com.kuroneko.database.entity.MemberChannelEntity;
import com.kuroneko.database.repository.MemberChannelRepository;
import com.kuroneko.misc.RNG;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StealthRollInteraction extends RollInteraction{

    private RNG rng;
    private MemberChannelRepository mcRepository;
    private final String GM_OPTION = "game-master";

    @Override
    protected void sendMessage(SlashCommandInteractionEvent event, MessageEmbed embed) {
        event.replyEmbeds(embed).setEphemeral(true).queue();
        OptionMapping option = event.getInteraction().getOption(GM_OPTION);
        if (option != null) {
            option.getAsUser().openPrivateChannel().complete().sendMessageEmbeds(embed).queue();
        }
    }

    @Override
    RNG getRNG() {
        return rng;
    }

    @Override
    protected MemberChannelEntity getMemberChannel(SlashCommandInteractionEvent event) {
        return mcRepository.findById(new MemberChannelEntity.Pk(event.getMember().getIdLong(), event.getChannel().getId())).orElse(null);
    }

    @Override
    public CommandData getCommand() {
        return ((SlashCommandData) super.getCommand()).addOption(OptionType.USER, GM_OPTION, "e.g. @Kuroneko", false, false);
    }

    @Override
    public String getName() {
        return "stealth-roll";
    }
}
