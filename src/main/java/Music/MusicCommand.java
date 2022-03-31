package Music;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface MusicCommand {
    void execute(MessageReceivedEvent event, String pureCommand);
}
