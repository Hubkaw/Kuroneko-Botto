package com.kuroneko.Music;

import com.kuroneko.Config.KuronekoEmbed;
import com.kuroneko.Config.TemporaryMessage;
import com.kuroneko.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Leave implements MusicCommand {
    @Override
    public void execute(MessageReceivedEvent event, String pureCommand) {
        Member selfMember = event.getGuild().getSelfMember();
        Member member = event.getMember();
        if (selfMember.getVoiceState().inAudioChannel()) {
            if (member.getVoiceState().getChannel() == selfMember.getVoiceState().getChannel()) {
                PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.skipAll();
                event.getGuild().getAudioManager().closeAudioConnection();
            } else {
                MessageEmbed embed = new KuronekoEmbed().setTitle("I can't do that Senpai~").setDescription("We are not in the same voice channel").build();
                new TemporaryMessage(event.getChannel(), embed).start();
            }
        } else {
            MessageEmbed embed = new KuronekoEmbed().setTitle("Kys Senpai").setDescription("I'm not connected to any voice channel faggot").build();
            new TemporaryMessage(event.getChannel(), embed).start();
        }
    }
}
