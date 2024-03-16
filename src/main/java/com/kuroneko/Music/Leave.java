package com.kuroneko.Music;

import com.kuroneko.Config.KuronekoEmbed;
import com.kuroneko.LavaPlayer.PlayerManager;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Leave implements MusicInteraction {

    @Getter
    private final String name = "leave";

    @Override
    public ReplyRemover execute(SlashCommandInteractionEvent event) {
        Member selfMember = event.getGuild().getSelfMember();
        Member member = event.getMember();
        if (selfMember.getVoiceState().inAudioChannel()) {
            if (member.getVoiceState().getChannel() == selfMember.getVoiceState().getChannel()) {
                PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.skipAll();
                String vcName = selfMember.getVoiceState().getChannel().getAsMention();
                event.getGuild().getAudioManager().closeAudioConnection();
                InteractionHook leave = event.replyEmbeds(new KuronekoEmbed().setTitle("Leave").setDescription("I have left " + vcName + " voice channel").build()).complete();
                return new MessageDeleter(leave);

            } else {
                MessageEmbed embed = new KuronekoEmbed().setTitle("I can't do that Senpai~").setDescription("We are not in the same voice channel").build();
                InteractionHook complete = event.replyEmbeds(embed).complete();
                return new MessageDeleter(complete);
            }
        } else {
            MessageEmbed embed = new KuronekoEmbed().setTitle("Kys Senpai").setDescription("I'm not connected to any voice channel faggot").build();
            InteractionHook complete = event.replyEmbeds(embed).complete();
            return new MessageDeleter(complete);
        }
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Leave current voice channel and clear music queue");
    }
}
