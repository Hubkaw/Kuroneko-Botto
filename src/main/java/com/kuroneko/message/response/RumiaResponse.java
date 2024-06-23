package com.kuroneko.message.response;


import com.kuroneko.message.CustomMessageResponse;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class RumiaResponse implements CustomMessageResponse {

    @Override
    public boolean isRelevant(MessageReceivedEvent event) {
        return !event.getAuthor().isBot()
        && event.getMessage().getContentRaw().toLowerCase().contains("rumia");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getMessage().reply("https://tenor.com/view/rumia-dance-gif-22869742").queue();
    }
}
