package com.kuroneko.interaction.rp.roll;


import com.kuroneko.database.entity.MemberChannelEntity;
import com.kuroneko.database.repository.MemberChannelRepository;
import com.kuroneko.misc.RNG;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PublicRollInteraction extends RollInteraction {

    private RNG rng;
    private MemberChannelRepository mcRepository;

    @Override
    void sendMessage(SlashCommandInteractionEvent event, MessageEmbed embed) {
        event.replyEmbeds(embed).setEphemeral(false).queue();
    }

    @Override
    RNG getRNG() {
        return rng;
    }

    @Override
    public String getName() {
        return "roll";
    }

    @Override
    protected MemberChannelEntity getMemberChannel(SlashCommandInteractionEvent event) {
        return mcRepository.findById(new MemberChannelEntity.Pk(event.getMember().getIdLong(), event.getChannel().getId())).orElse(null);
    }
}
