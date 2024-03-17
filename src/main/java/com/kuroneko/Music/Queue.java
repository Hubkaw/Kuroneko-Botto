package com.kuroneko.Music;

import com.kuroneko.Config.KuronekoEmbed;
import com.kuroneko.LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Queue implements MusicInteraction {

    @Getter
    private final String name = "queue";

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        AtomicInteger integer = new AtomicInteger(0);
        BlockingQueue<AudioTrack> queue = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.getQueue();
        String[] tracks = new String[queue.size()];
        queue.forEach(track -> {
            tracks[integer.getAndIncrement()] = integer.get()+". "+track.getInfo().title+"\n";
        });
        StringBuilder sb = new StringBuilder();
        if(PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.isLooped()){
            sb.append("The current song is looped. Type /np to check it\n\n");
        }
        if(queue.size()==0){
            sb.append("The queue is empty.");
            MessageEmbed current_queue = new KuronekoEmbed().setTitle("Current Queue").setDescription(sb.toString()).build();
            InteractionHook complete = event.replyEmbeds(current_queue).complete();
            complete.deleteOriginal().queueAfter(12, TimeUnit.SECONDS);

        } else {
            for (int i = 0; i < 20 && i < queue.size(); i++) {
                if (tracks[i] != null)
                    sb.append(tracks[i]);
            }
            MessageEmbed current_queue = new KuronekoEmbed().setTitle("Current Queue").setDescription(sb.toString()).build();

            InteractionHook complete = event.replyEmbeds(current_queue).complete();
            complete.deleteOriginal().queueAfter(45, TimeUnit.SECONDS);

        }
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Display currently queued songs");
    }
}
