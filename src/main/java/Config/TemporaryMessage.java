package Config;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class TemporaryMessage extends Thread {
    private MessageChannel messageChannel;
    private MessageEmbed messageEmbed;
    public TemporaryMessage(MessageChannel messageChannel, MessageEmbed messageEmbed){
        this.messageChannel=messageChannel;
        this.messageEmbed=messageEmbed;
    }

    @Override
    public void run() {
        Message message = messageChannel.sendMessageEmbeds(messageEmbed).complete();
        try{
            Thread.sleep(10000);
        } catch (InterruptedException e){

        }
        message.delete().queue();
    }
}
