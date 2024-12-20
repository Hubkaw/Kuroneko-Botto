package com.kuroneko.message.respondless;

import com.kuroneko.interaction.rp.inititative.InitiativeCheckManager;
import com.kuroneko.message.CustomMessageResponse;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InitiativeInteractionChannelBlock implements CustomMessageResponse {

    InitiativeCheckManager icm;

    @Override
    public boolean isRelevant(MessageReceivedEvent event) {
        return  !event.getAuthor().isBot() &&
                icm.getInitiativeCheck(event.getChannel().getId()) != null;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getMessage().delete().queue();
    }
}
