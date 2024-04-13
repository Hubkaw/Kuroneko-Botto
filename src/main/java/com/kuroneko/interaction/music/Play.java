package com.kuroneko.interaction.music;

import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.lavaplayer.PlayerManager;
import com.kuroneko.misc.KuronekoEmbed;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Play extends MusicInteraction implements SlashInteraction {

    @Getter
    private final String name = "play";

    public Play(PlayerManager playerManager) {
        super(playerManager);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        OptionMapping link = event.getOption("link");

        if(link == null || link.getAsString().isBlank()){
            MessageEmbed embed = new KuronekoEmbed().setTitle("You can't do that Senpai~")
                    .setDescription("You must provide song's name or link").build();
            InteractionHook complete = event.replyEmbeds(embed).complete();
            complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
            return;
        }
        if(member.getVoiceState().getChannel()==null){
            MessageEmbed embed = new KuronekoEmbed().setTitle("You can't do that Senpai~")
                    .setDescription("You are not connected to a voice channel").build();
            InteractionHook complete = event.replyEmbeds(embed).complete();
            complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
            return;
        }
        if (guild.getSelfMember().getVoiceState().inAudioChannel()
                && !playerManager.getMusicManager(guild).scheduler.getQueue().isEmpty()
                && guild.getSelfMember().getVoiceState().getChannel() != member.getVoiceState().getChannel()){
            MessageEmbed embed = new KuronekoEmbed().setTitle("Im busy Senpai~")
                    .setDescription("I am already playing, why don't you join me at " + guild.getSelfMember().getVoiceState().getChannel().getAsMention() +"?").build();
            InteractionHook complete = event.replyEmbeds(embed).complete();
            complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
            return;
        }
        if(!guild.getSelfMember().getVoiceState().inAudioChannel()){
            guild.getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
        }

        InteractionHook complete = event.deferReply().complete();
        playerManager.loadAndPlay(guild, complete, prepareLink(link.getAsString()));
    }

    private String prepareLink(String link) {
        String toBeSearched = "";
        if (!link.contains("?list") && !link.contains("&list")){
            toBeSearched += "ytsearch: ";
        }

        if (link.contains("youtu.be")){
            toBeSearched += link.replace("youtu.be/", "www.youtube.com/watch?v=");
        } else {
            toBeSearched += link;
        }
        return toBeSearched;
    }

    @Override
    public CommandData getCommand() {
        SlashCommandData commandData = Commands.slash(getName(), "Play sound from a Youtube video");
        commandData.addOption(OptionType.STRING, "link", "Link or name to be searched",true, false);
        return commandData;
    }
}
