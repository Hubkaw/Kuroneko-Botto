package com.kuroneko.message.response;

import com.kuroneko.config.ConfigLoader;
import com.kuroneko.message.CustomMessageResponse;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class OlekNieKumamResponse implements CustomMessageResponse {

    @Override
    public boolean isRelevant(MessageReceivedEvent event) {
        return event.getMember().getIdLong() == ConfigLoader.getConfig().getOlekID()
                && event.getMessage().getContentRaw().toLowerCase().contains("nie kumam");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        StringBuilder reversed = new StringBuilder(event.getMessage().getContentRaw()).reverse();
        event.getChannel().sendMessage(reversed).setMessageReference(event.getMessage()).queue();
    }
}
