package Music;

import Config.KuronekoEmbed;
import Config.TemporaryMessage;
import LavaPlayer.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NowPlaying implements MusicCommand{
    @Override
    public void execute(MessageReceivedEvent event, String pureCommand) {
        Member selfMember = event.getGuild().getSelfMember();
        if (selfMember.getVoiceState().inAudioChannel()){
            AudioTrack playingTrack = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).player.getPlayingTrack();
            if(playingTrack!=null){
                String loop = "";
                if (PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.isLooped()){
                    loop = "\nThis song is looped. To toggle type ;loop";
                }
                MessageEmbed embed = new KuronekoEmbed().setTitle(playingTrack.getInfo().title)
                        .setDescription("By: " + playingTrack.getInfo().author + "\nHere is the link:\n" + playingTrack.getInfo().uri+loop)
                        .setThumbnail("http://img.youtube.com/vi/"+playingTrack.getInfo().identifier+"/0.jpg").build();
                event.getChannel().sendMessageEmbeds(embed).queue();
            } else {
                MessageEmbed build = new KuronekoEmbed().setTitle("I'm not playing anything")
                        .setDescription("You can request a song by typing ';play <name/link>'").build();
                new TemporaryMessage(event.getChannel(), build).start();
            }
        } else {
            MessageEmbed build = new KuronekoEmbed().setTitle("I'm not playing anything")
                    .setDescription("I'm not even connected to a coive channel nerd").build();
            new TemporaryMessage(event.getChannel(), build).start();
        }
    }
}
