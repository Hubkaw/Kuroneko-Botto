package com.kuroneko.service;

import com.kuroneko.database.entity.ChannelEntity;
import com.kuroneko.database.entity.EventLogArgumentEntity;
import com.kuroneko.database.entity.EventLogEntryEntity;
import com.kuroneko.database.mapper.ChannelMapper;
import com.kuroneko.database.repository.ChannelRepository;
import com.kuroneko.database.repository.EventLogEntryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kuroneko.config.CONSTANTS.LOG_ARGUMENT_CHUNK_SIZE;

@Component
@AllArgsConstructor
public class LoggingService {

    private EventLogEntryRepository logRepository;
    private ChannelRepository channelRepository;

    @Transactional
    public void log(
            User author,
            LocalDateTime timestamp,
            String eventName,
            EventLogEntryEntity.EventType eventType,
            GuildMessageChannelUnion channel,
            Map<String, String> args) {

        EventLogEntryEntity entry = new EventLogEntryEntity();
        entry.setEventName(eventName);
        entry.setEventType(eventType);
        entry.setUserId(author.getIdLong());
        entry.setUsername(author.getName());
        entry.setTimestamp(timestamp);
        entry.setChannel(findOrCreateChannel(channel));
        entry.setArguments(mapArguments(args, entry));
        logRepository.save(entry);
    }

    private ChannelEntity findOrCreateChannel(GuildMessageChannelUnion guildChannel) {
        return channelRepository.findById(guildChannel.getId()).orElseGet(() -> {
            ChannelEntity channel = ChannelMapper.map(guildChannel);
            return channelRepository.save(channel);
        });
    }

    private List<EventLogArgumentEntity> mapArguments(Map<String, String> args, EventLogEntryEntity entry) {

        if (args == null || args.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<EventLogArgumentEntity> result = new ArrayList<>();

        args.forEach((k, v) -> {

            for (int i = 0, y = 0; i < v.length(); i += LOG_ARGUMENT_CHUNK_SIZE, y++) {
                EventLogArgumentEntity argumentEntity = new EventLogArgumentEntity();

                if (y == 0) {
                    argumentEntity.setName(k);
                } else {
                    argumentEntity.setName(k + " " + y);
                }

                argumentEntity.setValue(v.substring(i, Math.min(i + LOG_ARGUMENT_CHUNK_SIZE, v.length())));
                argumentEntity.setLoggedCommand(entry);
                result.add(argumentEntity);
            }

        });
        return result;
    }
}
