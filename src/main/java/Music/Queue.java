package Music;

import Config.KuronekoEmbed;
import Config.TemporaryMessage;
import LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Queue implements MusicCommand {
    @Override
    public void execute(MessageReceivedEvent event, String pureCommand) {
        AtomicInteger integer = new AtomicInteger(0);
        BlockingQueue<AudioTrack> queue = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.getQueue();
        String[] tracks = new String[queue.size()];
        queue.forEach(track -> {
            tracks[integer.getAndIncrement()] = integer.get()+". "+track.getInfo().title+"\n";
        });
        StringBuilder sb = new StringBuilder();
        if(PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.isLooped()){
            sb.append("The current song is looped. Type ';NowPlaying' to check it\n\n");
        }
        if(queue.size()==0){
            sb.append("The queue is empty.");
            MessageEmbed current_queue = new KuronekoEmbed().setTitle("Current Queue").setDescription(sb.toString()).build();
            new TemporaryMessage(event.getChannel(), current_queue).start();
        } else {
            for (int i = 0; i < 20 && i < queue.size(); i++) {
                if (tracks[i] != null)
                    sb.append(tracks[i]);
            }
            MessageEmbed current_queue = new KuronekoEmbed().setTitle("Current Queue").setDescription(sb.toString()).build();
            event.getChannel().sendMessageEmbeds(current_queue).queue();
        }
    }
}
