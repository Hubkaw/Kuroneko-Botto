package com.kuroneko.message.response;

import com.kuroneko.config.KuronekoIDs;
import com.kuroneko.message.CustomMessageResponse;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PonyskillerNerdResponse implements CustomMessageResponse {

    private final KuronekoIDs  kuronekoIDs;

    @Override
    public boolean isRelevant(MessageReceivedEvent event) {
        return event.getMember() != null
                && event.getMember().getIdLong() == kuronekoIDs.ponyskiller()
                && event.getMessage().getContentRaw().toLowerCase().contains("ale");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getMessage().reply(":nerd:").queue();
    }
}
