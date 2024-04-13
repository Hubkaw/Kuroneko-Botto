package com.kuroneko.interaction.music;


import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.lavaplayer.PlayerManager;
import com.kuroneko.misc.KuronekoEmbed;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class Leave extends MusicInteraction implements SlashInteraction {

    public Leave(PlayerManager playerManager) {
        super(playerManager);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member selfMember = event.getGuild().getSelfMember();
        Member member = event.getMember();
        log.debug("Leave event started ");
        if (selfMember.getVoiceState().inAudioChannel()) {
            if (member.getVoiceState().getChannel() == selfMember.getVoiceState().getChannel()) {
                playerManager.getMusicManager(event.getGuild()).scheduler.skipAll();
                String vcName = selfMember.getVoiceState().getChannel().getAsMention();
                event.getGuild().getAudioManager().closeAudioConnection();
                InteractionHook leave = event.replyEmbeds(new KuronekoEmbed().setTitle("Leave").setDescription("I have left " + vcName + " voice channel").build()).complete();
                leave.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
                log.debug("Leave event completed");
            } else {
                MessageEmbed embed = new KuronekoEmbed().setTitle("I can't do that Senpai~").setDescription("We are not in the same voice channel").build();
                InteractionHook complete = event.replyEmbeds(embed).complete();
                complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
            }
        } else {
            MessageEmbed embed = new KuronekoEmbed().setTitle("Kys Senpai").setDescription("I'm not connected to any voice channel faggot").build();
            InteractionHook complete = event.replyEmbeds(embed).complete();
            complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
        }
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Leave current voice channel and clear music queue");
    }

    @Override
    public String getName() {
        return "leave";
    }
}
