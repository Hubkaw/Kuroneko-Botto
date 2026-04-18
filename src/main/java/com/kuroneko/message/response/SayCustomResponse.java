package com.kuroneko.message.response;

import com.kuroneko.config.KuronekoIDs;
import com.kuroneko.message.CustomMessageResponse;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SayCustomResponse implements CustomMessageResponse {

    private final KuronekoIDs  kuronekoIDs;

    @Override
    public boolean isRelevant(MessageReceivedEvent event) {
        return event.getAuthor().getIdLong() == kuronekoIDs.owner()
                && event.getMessage().getContentRaw().startsWith("`");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        Message referencedMessage = event.getMessage().getReferencedMessage();
        event.getMessage().delete().queue();
        if (referencedMessage != null){
            event.getChannel().sendMessage(message.replaceFirst("`", ""))
                    .setMessageReference(referencedMessage).queue();
            return;
        }
        event.getChannel().sendMessage(message.replaceFirst("`", "")).queue();
    }
}
