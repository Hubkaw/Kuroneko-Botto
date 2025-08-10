package com.kuroneko.logger;

import com.kuroneko.database.entity.EventLogEntryEntity;
import com.kuroneko.service.LoggingService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@AllArgsConstructor
public class ButtonInteractionLogger {

    private LoggingService loggingService;

    @Async
    public void log(ButtonInteractionEvent event) {
        loggingService.log(
                event.getUser(),
                event.getTimeCreated().toLocalDateTime(),
                event.getInteraction().getButton().getId(),
                EventLogEntryEntity.EventType.BUTTON_INTERACTION,
                event.getGuildChannel(),
                new HashMap<>());
    }
}
