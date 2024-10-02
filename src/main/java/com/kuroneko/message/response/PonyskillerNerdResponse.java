package com.kuroneko.message.response;

import com.kuroneko.message.CustomMessageResponse;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class PonyskillerNerdResponse implements CustomMessageResponse {

    @Override
    public boolean isRelevant(MessageReceivedEvent event) {
        return event.getMember() != null
                && event.getMember().getIdLong() == 580456996013080576L
                && event.getMessage().getContentRaw().toLowerCase().contains("ale");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getMessage().reply(":nerd:").queue();
    }
}
