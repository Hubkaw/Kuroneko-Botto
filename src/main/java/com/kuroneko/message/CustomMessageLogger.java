package com.kuroneko.message;

import com.kuroneko.database.entity.ChannelEntity;
import com.kuroneko.database.entity.EventLogArgumentEntity;
import com.kuroneko.database.entity.EventLogEntryEntity;
import com.kuroneko.database.mapper.ChannelMapper;
import com.kuroneko.database.repository.ChannelRepository;
import com.kuroneko.database.repository.EventLogEntryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CustomMessageLogger {

    private EventLogEntryRepository logRepository;
    private ChannelRepository channelRepository;

    @Async
    @Transactional
    public void log(MessageReceivedEvent event, CustomMessageResponse handler) {
        EventLogEntryEntity entry = new EventLogEntryEntity();
        entry.setEventName(handler.getClass().getSimpleName());
        entry.setEventType(EventLogEntryEntity.EventType.CUSTOM_MESSAGE_RESPONSE);
        entry.setUserId(event.getAuthor().getIdLong());
        entry.setUsername(event.getAuthor().getName());
        entry.setTimestamp(event.getMessage().getTimeCreated().toLocalDateTime());
        entry.setChannel(findOrCreateChannel(event));
        entry.setArguments(mapArguments(event, entry));
        logRepository.save(entry);
    }

    private ChannelEntity findOrCreateChannel(MessageReceivedEvent event) {
        return channelRepository.findById(event.getChannel().getId()).orElseGet(() -> {
            ChannelEntity channel = ChannelMapper.map(event.getGuildChannel());
            return channelRepository.save(channel);
        });
    }

    private List<EventLogArgumentEntity> mapArguments(MessageReceivedEvent event, EventLogEntryEntity entry) {
        ArrayList<EventLogArgumentEntity> result = new ArrayList<>();
        String content = event.getMessage().getContentRaw();
        int chunkSize = 255;
        for (int i = 0, y = 0; i < content.length(); i+=chunkSize, y++ ){
            EventLogArgumentEntity argumentEntity = new EventLogArgumentEntity();
            argumentEntity.setName("Message " + y);
            argumentEntity.setValue(content.substring(i, Math.min(i+chunkSize, content.length())));
            argumentEntity.setLoggedCommand(entry);
            result.add(argumentEntity);
        }
        return result;
    }

}
