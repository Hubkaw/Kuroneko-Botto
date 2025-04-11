package com.kuroneko.interaction;

import com.kuroneko.database.entity.ChannelEntity;
import com.kuroneko.database.entity.EventLogEntryEntity;
import com.kuroneko.database.mapper.ChannelMapper;
import com.kuroneko.database.repository.ChannelRepository;
import com.kuroneko.database.repository.EventLogEntryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ButtonInteractionLogger {

    private EventLogEntryRepository logRepository;
    private ChannelRepository channelRepository;

    @Async
    @Transactional
    public void log(ButtonInteractionEvent event) {
        EventLogEntryEntity entry = new EventLogEntryEntity();
        entry.setEventType(EventLogEntryEntity.EventType.BUTTON_INTERACTION);
        entry.setEventName(event.getInteraction().getButton().getId());
        entry.setTimestamp(event.getTimeCreated().toLocalDateTime());
        entry.setUserId(event.getUser().getIdLong());
        entry.setUsername(event.getUser().getName());
        entry.setChannel(findOrCreateChannel(event));
        logRepository.save(entry);
    }

    private ChannelEntity findOrCreateChannel(ButtonInteractionEvent event) {
        return channelRepository.findById(event.getChannelId()).orElseGet(() -> {
            ChannelEntity newChannel = ChannelMapper.map(event.getGuildChannel());
            return channelRepository.save(newChannel);
        });
    }
}
