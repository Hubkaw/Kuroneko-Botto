package com.kuroneko.logger;

import com.kuroneko.database.entity.EventLogEntryEntity;
import com.kuroneko.service.LoggingService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class SlashInteractionLogger {

    private LoggingService loggingService;

    @Async
    public void log(@NotNull SlashCommandInteractionEvent event) {

        loggingService.log(
                event.getUser(),
                event.getTimeCreated().toLocalDateTime(),
                event.getName(),
                EventLogEntryEntity.EventType.SLASH_COMMAND,
                event.getGuildChannel(),
                prepareArgs(event)
        );
    }

    private Map<String, String> prepareArgs(SlashCommandInteractionEvent event) {
        HashMap<String, String> result = new HashMap<>();
        event.getOptions().forEach(o -> {
            if (o != null && !o.getAsString().isBlank()) {
                result.put(o.getName(), o.getAsString());
            }
        });
        return result;
    }
}
