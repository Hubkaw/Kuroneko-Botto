package com.kuroneko.interaction.music;

import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.lavaplayer.GuildMusicManager;
import com.kuroneko.lavaplayer.PlayerManager;
import com.kuroneko.misc.KuronekoEmbed;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Shuffle extends MusicInteraction implements SlashInteraction {

    @Getter
    private final String name = "shuffle";

    public Shuffle(PlayerManager playerManager) {
        super(playerManager);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();
        if (member.getVoiceState().getChannel()==selfMember.getVoiceState().getChannel()){
            GuildMusicManager musicManager = playerManager.getMusicManager(event.getGuild());
            if(!musicManager.scheduler.getQueue().isEmpty()) {

                musicManager.scheduler.shuffle();

                MessageEmbed embed = new KuronekoEmbed().setTitle("Shuffle").setDescription("Songs shuffled").build();
                InteractionHook complete = event.replyEmbeds(embed).complete();
                complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);

            } else {
                MessageEmbed embed = new KuronekoEmbed().setTitle("Shuffle").setDescription("There is nothing to shuffle Senpai").build();
                InteractionHook complete = event.replyEmbeds(embed).complete();
                complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
            }
        } else {
            MessageEmbed embed = new KuronekoEmbed().setTitle("You can't request that Senpai").setDescription("We are not in the same voice channel").build();
            InteractionHook complete = event.replyEmbeds(embed).complete();
            complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
        }
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Shuffle current song queue");
    }
}
