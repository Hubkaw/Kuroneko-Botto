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

public class Shuffle implements MusicInteraction{

    @Getter
    private final String name = "shuffle";

    @Override
    public ReplyRemover execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();
        if (member.getVoiceState().getChannel()==selfMember.getVoiceState().getChannel()){
            GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
            if(!musicManager.scheduler.getQueue().isEmpty()) {

                musicManager.scheduler.shuffle();

                MessageEmbed embed = new KuronekoEmbed().setTitle("Shuffle").setDescription("Songs shuffled").build();
                InteractionHook complete = event.replyEmbeds(embed).complete();
                return new MessageDeleter(complete);

            } else {
                MessageEmbed embed = new KuronekoEmbed().setTitle("Shuffle").setDescription("There is nothing to shuffle Senpai").build();
                InteractionHook complete = event.replyEmbeds(embed).complete();
                return new MessageDeleter(complete);
            }
        } else {
            MessageEmbed embed = new KuronekoEmbed().setTitle("You can't request that Senpai").setDescription("We are not in the same voice channel").build();
            InteractionHook complete = event.replyEmbeds(embed).complete();
            return new MessageDeleter(complete);
        }
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Shuffle current song queue");
    }
}
