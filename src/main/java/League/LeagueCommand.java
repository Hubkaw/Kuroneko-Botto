package League;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.nio.channels.Channel;

public abstract class LeagueCommand {

    public abstract void execute(MessageReceivedEvent messageReceivedEvent, String command);
}
