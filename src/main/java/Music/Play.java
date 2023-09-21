package Music;

import Config.KuronekoEmbed;
import Config.TemporaryMessage;
import LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.net.MalformedURLException;
import java.net.URL;


public class Play implements MusicCommand{
    @Override
    public void execute(MessageReceivedEvent event, String pureCommand) {
        String[] split = pureCommand.split(" ",2);
        Member member = event.getMember();
        Guild guild = event.getGuild();
        MessageChannel channel = event.getChannel();

        if(split.length<2){
            MessageEmbed embed = new KuronekoEmbed().setTitle("You can't do that Senpai~")
                    .setDescription("You must provide song's name or link").build();
            new TemporaryMessage(channel, embed).start();
            return;
        }
        if(member.getVoiceState().getChannel()==null){
            MessageEmbed embed = new KuronekoEmbed().setTitle("You can't do that Senpai~")
                    .setDescription("You are not connected to a voice channel").build();
            new TemporaryMessage(channel, embed).start();
            return;
        }
        if (guild.getSelfMember().getVoiceState().inAudioChannel()
                && !PlayerManager.getINSTANCE().getMusicManager(guild).scheduler.getQueue().isEmpty()
                && guild.getSelfMember().getVoiceState().getChannel() != member.getVoiceState().getChannel()){
            MessageEmbed embed = new KuronekoEmbed().setTitle("Im busy Senpai~")
                    .setDescription("Why don't you join me?").build();
            new TemporaryMessage(channel,embed);
            return;
        }
        if(!guild.getSelfMember().getVoiceState().inAudioChannel()){
            guild.getAudioManager().openAudioConnection(member.getVoiceState().getChannel());
        }

        PlayerManager.getINSTANCE().loadAndPlay(guild, channel, "ytsearch: " + split[1]);

    }
}
