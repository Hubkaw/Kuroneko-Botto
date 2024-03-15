package com.kuroneko.Music;

import com.kuroneko.Config.KuronekoEmbed;
import com.kuroneko.Config.TemporaryMessage;
import com.kuroneko.LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Skip implements MusicCommand{

    @Override
    public void execute(MessageReceivedEvent event, String pureCommand) {
        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();
        if(member.getVoiceState().getChannel()==selfMember.getVoiceState().getChannel()){
            String[] s = pureCommand.split(" ");
            if(s.length == 2) {
                try {
                    int i = Integer.parseInt(s[1]);
                    PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.skip(i);
                    return;
                } catch (Exception e) {
                    if (s[1].equalsIgnoreCase("all")){
                        PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.skipAll();
                        return;
                    }
                }
            }

            PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.nextTrack();
        } else {
            MessageEmbed build = new KuronekoEmbed().setTitle("You can't do that Senpai").setDescription("We are not in the same voice channel").build();
            new TemporaryMessage(event.getChannel(), build).start();
        }
    }
}
