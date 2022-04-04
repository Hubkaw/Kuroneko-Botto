package Music;

import Config.KuronekoEmbed;
import Config.TemporaryMessage;
import LavaPlayer.GuildMusicManager;
import LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Shuffle implements MusicCommand{

    @Override
    public void execute(MessageReceivedEvent event, String pureCommand) {
        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();
        if (member.getVoiceState().getChannel()==selfMember.getVoiceState().getChannel()){
            GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
            if(!musicManager.scheduler.getQueue().isEmpty()) {

                musicManager.scheduler.shuffle();

                MessageEmbed embed = new KuronekoEmbed().setTitle("Shuffle").setDescription("Songs shuffled").build();
                new TemporaryMessage(event.getChannel(), embed).start();
            } else {
                MessageEmbed embed = new KuronekoEmbed().setTitle("Shuffle").setDescription("There is nothing to shuffle Senpai").build();
                new TemporaryMessage(event.getChannel(), embed).start();
            }
        } else {
            MessageEmbed embed = new KuronekoEmbed().setTitle("You can't request that Senpai").setDescription("We are not in the same voice channel").build();
            new TemporaryMessage(event.getChannel(), embed).start();
        }
    }
}
