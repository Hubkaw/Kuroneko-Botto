package com.kuroneko.message.response;

import com.kuroneko.config.KuronekoIDs;
import com.kuroneko.message.CustomMessageResponse;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OlekNieKumamResponse implements CustomMessageResponse {

    private final KuronekoIDs kuronekoIDs;

    @Override
    public boolean isRelevant(MessageReceivedEvent event) {
        return event.getMember() != null
                && event.getMember().getIdLong() == kuronekoIDs.olek()
                && event.getMessage().getContentRaw().toLowerCase().contains("nie kumam");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        StringBuilder reversed = new StringBuilder(event.getMessage().getContentRaw()).reverse();
        event.getChannel().sendMessage(reversed).setMessageReference(event.getMessage()).queue();
    }
}
