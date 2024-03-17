package com.kuroneko.Music;

import com.kuroneko.Config.KuronekoEmbed;
import com.kuroneko.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.concurrent.TimeUnit;

public class NowPlaying implements MusicInteraction {

    @Getter
    private final String name = "np";

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member selfMember = event.getGuild().getSelfMember();
        if (selfMember.getVoiceState().inAudioChannel()){
            AudioTrack playingTrack = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).player.getPlayingTrack();
            if(playingTrack!=null){
                String loop = "";
                if (PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.isLooped()){
                    loop = "\nThis song is looped. To toggle type /loop";
                }
                MessageEmbed embed = new KuronekoEmbed().setTitle(playingTrack.getInfo().title)
                        .setDescription("By: " + playingTrack.getInfo().author + "\nHere is the link:\n" + playingTrack.getInfo().uri+loop)
                        .setThumbnail("http://img.youtube.com/vi/"+playingTrack.getInfo().identifier+"/0.jpg").build();
                InteractionHook complete = event.replyEmbeds(embed).complete();
                complete.deleteOriginal().queueAfter(30, TimeUnit.SECONDS);
            } else {
                MessageEmbed build = new KuronekoEmbed().setTitle("I'm not playing anything")
                        .setDescription("You can request a song by typing ';play <name/link>'").build();
                InteractionHook complete = event.replyEmbeds(build).complete();
                complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
            }
        } else {
            MessageEmbed build = new KuronekoEmbed().setTitle("I'm not playing anything")
                    .setDescription("I'm not even connected to a coive channel nerd").build();
            InteractionHook complete = event.replyEmbeds(build).complete();
            complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
        }
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Display currently playing song's info");
    }
}
