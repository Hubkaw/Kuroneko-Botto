package com.kuroneko.message.response;

import com.kuroneko.config.ConfigLoader;
import com.kuroneko.message.CustomMessageResponse;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class SayCustomResponse implements CustomMessageResponse {
    @Override
    public boolean isRelevant(MessageReceivedEvent event) {
        return event.getAuthor().getId().equals(ConfigLoader.getConfig().getOwnerID())
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
