package com.kuroneko.Music;

import com.kuroneko.Config.KuronekoEmbed;
import com.kuroneko.LavaPlayer.PlayerManager;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.concurrent.TimeUnit;


public class Play implements MusicInteraction {

    @Getter
    private final String name = "play";

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        MessageChannel channel = event.getChannel();
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
                && !PlayerManager.getINSTANCE().getMusicManager(guild).scheduler.getQueue().isEmpty()
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
        PlayerManager.getINSTANCE().loadAndPlay(guild, complete, prepareLink(link.getAsString()));
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
