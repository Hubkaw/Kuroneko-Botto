package Music;

import Config.KuronekoEmbed;
import Config.TemporaryMessage;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help implements MusicCommand{
    @Override
    public void execute(MessageReceivedEvent event, String pureCommand) {
        String help = """
                My prefix is ;. Staring with my name will also work.

                My music commands are:
                ;**play** + -link or name-
                ;**skip** + (optional) -amount or all-
                ;**queue**
                ;**np**
                ;**leave**
                ;**loop**
                ;**help**""";
        MessageEmbed help1 = new KuronekoEmbed().setTitle("Help").setDescription(help).build();
        event.getChannel().sendMessageEmbeds(help1).queue();

    }
}
