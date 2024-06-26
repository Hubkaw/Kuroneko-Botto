package com.kuroneko.interaction.music;


import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.lavaplayer.PlayerManager;
import com.kuroneko.lavaplayer.TrackScheduler;
import com.kuroneko.misc.KuronekoEmbed;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Queue extends MusicInteraction implements SlashInteraction {

    @Getter
    private final String name = "queue";

    public Queue(PlayerManager playerManager) {
        super(playerManager);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        AtomicInteger integer = new AtomicInteger(0);
        TrackScheduler scheduler = playerManager.getMusicManager(event.getGuild()).scheduler;
        BlockingQueue<AudioTrack> queue = scheduler.getQueue();
        String[] tracks = new String[queue.size()];
        queue.forEach(track -> {
            tracks[integer.getAndIncrement()] = integer.get()+". "+track.getInfo().title+"\n";
        });
        StringBuilder sb = new StringBuilder();
        if(scheduler.isLooped()){
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

            if (scheduler.getQueue().size() > 20){
                sb.append("[...]\n").append(scheduler.getQueue().size()).append(" songs queued.");
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
