package Music;

import Config.KuronekoEmbed;
import Config.TemporaryMessage;
import LavaPlayer.GuildMusicManager;
import LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Loop implements MusicCommand{
    @Override
    public void execute(MessageReceivedEvent event, String pureCommand) {
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
            new TemporaryMessage(event.getChannel(), embed).start();
        } else {
            MessageEmbed embed = new KuronekoEmbed().setTitle("You can't request that Senpai").setDescription("We are not in the same voice channel").build();
            new TemporaryMessage(event.getChannel(), embed).start();
        }

    }
}
