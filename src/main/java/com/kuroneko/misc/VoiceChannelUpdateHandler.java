package com.kuroneko.misc;

import com.kuroneko.lavaplayer.PlayerManager;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class VoiceChannelUpdateHandler extends ListenerAdapter {

    private PlayerManager playerManager;

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {

        Member selfMember = event.getGuild().getSelfMember();

        if (selfMember.getId().equals(event.getMember().getId())
                && (event.getNewValue() == null || event.getChannelJoined() == null)){
            disconnect(event);
            return;
        }

        if (shouldExitVoiceChannel(event)){
            new DisconnectionScheduler(event).start();
        }
    }

    private boolean shouldExitVoiceChannel(@NotNull GuildVoiceUpdateEvent event) {
        Member selfMember = event.getGuild().getSelfMember();
        return selfMember.getVoiceState() != null
                && event.getChannelLeft() != null
                && event.getMember() != selfMember
                && event.getChannelLeft().equals(selfMember.getVoiceState().getChannel())
                && event.getChannelLeft().getMembers().size() == 1;
    }

    private void disconnect(@NotNull GuildVoiceUpdateEvent event) {
        if (event.getGuild().getAudioManager().isConnected()) {
            event.getGuild().getAudioManager().closeAudioConnection();
        }
        playerManager.getMusicManager(event.getGuild()).scheduler.skipAll();
    }

    @AllArgsConstructor
    private class DisconnectionScheduler extends Thread {

        private GuildVoiceUpdateEvent event;

        @Override
        public void run() {
            try {
                sleep(15000);
                if (shouldExitVoiceChannel(event)){
                    disconnect(event);
                }
            } catch (Exception ignore) {}
        }
    }
}
