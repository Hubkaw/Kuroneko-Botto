package com.kuroneko.logger;

import com.kuroneko.database.entity.EventLogEntryEntity;
import com.kuroneko.message.CustomMessageResponse;
import com.kuroneko.service.LoggingService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class CustomMessageLogger {

    private LoggingService loggingService;

    @Async
    public void log(MessageReceivedEvent event, CustomMessageResponse handler) {

        loggingService.log(
                event.getAuthor(),
                event.getMessage().getTimeCreated().toLocalDateTime(),
                handler.getClass().getSimpleName(),
                EventLogEntryEntity.EventType.CUSTOM_MESSAGE_RESPONSE,
                event.getGuildChannel(),
                prepareArgs(event)
        );

    }

    private Map<String, String> prepareArgs(MessageReceivedEvent event) {
        return Map.of("Message", event.getMessage().getContentRaw());
    }

}
