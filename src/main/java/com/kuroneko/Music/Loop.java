package com.kuroneko.Music;

import com.kuroneko.Config.KuronekoEmbed;
import com.kuroneko.LavaPlayer.GuildMusicManager;
import com.kuroneko.LavaPlayer.PlayerManager;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.concurrent.TimeUnit;

public class Loop implements MusicInteraction {

    @Getter
    private final String name = "loop";

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();
        if (member.getVoiceState().getChannel()==selfMember.getVoiceState().getChannel()){
            GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
            boolean looped = musicManager.scheduler.isLooped();
            musicManager.scheduler.setLoop(!looped);
            String onoff;
            if(looped){
                onoff = "loop off";
            } else {
                onoff = "loop on";
            }
            MessageEmbed embed = new KuronekoEmbed().setTitle("Loop toggled").setDescription(onoff).build();
            InteractionHook complete = event.replyEmbeds(embed).complete();
            complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
        } else {
            MessageEmbed embed = new KuronekoEmbed().setTitle("You can't request that Senpai").setDescription("We are not in the same voice channel").build();
            InteractionHook complete = event.replyEmbeds(embed).complete();
            complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
        }
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Loop current song. Works until turned off or song is skipped.");
    }
}
