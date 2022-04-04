package Music;

import Config.KuronekoEmbed;
import Config.TemporaryMessage;
import LavaPlayer.PlayerManager;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MusicCommandManager extends ListenerAdapter {

    private final Map<String, MusicCommand> commandMap;

    public MusicCommandManager() {
        commandMap = Map.of(
                "play", new Play(),
                "skip", new Skip(),
                "queue", new Queue(),
                "leave", new Leave(),
                "np", new NowPlaying(),
                "nowplaying", new NowPlaying(),
                "loop", new Loop(),
                "help", new Help(),
                "shuffle", new Shuffle(),
                "mix", new Shuffle()
        );
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        String pureCommand = "";

        String message = event.getMessage().getContentRaw().toLowerCase();

        if (message.startsWith(";")){
            pureCommand = message.substring(1);
        } else if(message.startsWith("<@!738883479902617670>")){
            pureCommand = message.substring(23);
        } else if(message.startsWith("kuroneko ")){
            pureCommand = message.substring(9);
        }
        if (!event.getAuthor().isBot() && !pureCommand.equals("")) {

            String[] command = pureCommand.split(" ", 2);

            try {
                commandMap.get(command[0]).execute(event, pureCommand);
            } catch (Exception e) {
                e.printStackTrace();
                MessageEmbed embed = new KuronekoEmbed().setTitle("This is not a valid command Senpai").setDescription(command[0] + " is currently not a valid command. Try again.").build();
                new TemporaryMessage(event.getChannel(), embed).start();
            }

            event.getMessage().delete().queue();
        }
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        if (event.getMember() == event.getGuild().getSelfMember()) {
            PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.skipAll();
        }
        super.onGuildVoiceLeave(event);
    }

}
