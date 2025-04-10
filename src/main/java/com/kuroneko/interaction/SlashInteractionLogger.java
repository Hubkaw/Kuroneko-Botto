package com.kuroneko.interaction;

import com.kuroneko.database.entity.ChannelEntity;
import com.kuroneko.database.entity.EventLogArgumentEntity;
import com.kuroneko.database.entity.EventLogEntryEntity;
import com.kuroneko.database.mapper.ChannelMapper;
import com.kuroneko.database.repository.ChannelRepository;
import com.kuroneko.database.repository.LoggedCommandRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@AllArgsConstructor
public class SlashInteractionLogger {

    private LoggedCommandRepository logRepository;
    private ChannelRepository channelRepository;

    @Async
    @Transactional
    public void log(@NotNull SlashCommandInteractionEvent event) {
        EventLogEntryEntity entry = new EventLogEntryEntity();
        entry.setEventName(event.getName());
        entry.setEventType(EventLogEntryEntity.EventType.SLASH_COMMAND);
        entry.setUserId(event.getUser().getIdLong());
        entry.setUsername(event.getUser().getName());
        entry.setTimestamp(event.getTimeCreated().toLocalDateTime());
        entry.setChannel(findOrCreateChannel(event));
        entry.setArguments(mapArguments(event, entry));
        logRepository.save(entry);
    }

    private ChannelEntity findOrCreateChannel(@NotNull SlashCommandInteractionEvent event) {
        return channelRepository.findById(event.getChannelId())
                .orElseGet(() -> {
                    ChannelEntity newChannel = ChannelMapper.map(event.getGuildChannel());
                    return channelRepository.save(newChannel);
                });
    }

    private List<EventLogArgumentEntity> mapArguments(@NotNull SlashCommandInteractionEvent event, EventLogEntryEntity loggedCommand) {
        return event.getOptions().stream()
                .map(o -> {
                    EventLogArgumentEntity arg = new EventLogArgumentEntity();
                    arg.setName(o.getName());
                    arg.setValue(o.getAsString());
                    arg.setLoggedCommand(loggedCommand);
                    return arg;
                }).toList();
    }

}
