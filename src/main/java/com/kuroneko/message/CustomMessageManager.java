package com.kuroneko.message;
import com.kuroneko.logger.CustomMessageLogger;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class CustomMessageManager extends ListenerAdapter {

    private final List<CustomMessageResponse> customMessageResponses;
    private CustomMessageLogger logger;

    public CustomMessageManager(Set<CustomMessageResponse> customMessageResponses,
                                CustomMessageLogger logger){
        this.customMessageResponses = new ArrayList<>(customMessageResponses);
        this.logger = logger;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        customMessageResponses.stream()
                .filter(m -> m.isRelevant(event))
                .forEach(m -> {
                    logger.log(event, m);
                    m.execute(event);
                });
    }
}
