package com.kuroneko.message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class CustomMessageManager extends ListenerAdapter {

    private final List<CustomMessageResponse> customMessageResponses;

    public CustomMessageManager(Set<CustomMessageResponse> customMessageResponses){
        this.customMessageResponses = new ArrayList<>(customMessageResponses);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        customMessageResponses.stream()
                .filter(m -> m.isRelevant(event))
                .forEach(m -> m.execute(event));
    }
}
