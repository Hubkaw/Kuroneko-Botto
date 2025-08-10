package com.kuroneko.logger;

import com.kuroneko.database.entity.EventLogEntryEntity;
import com.kuroneko.service.LoggingService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class LeagueUpdateLogger {

    private LoggingService loggingService;
    private JDA jda;

    @Async
    public void log(MessageEmbed embed, GuildMessageChannelUnion channel) {

        loggingService.log(
                jda.getSelfUser(),
                LocalDateTime.now(),
                embed.getTitle(),
                EventLogEntryEntity.EventType.LEAGUE_UPDATE,
                channel,
                prepareArgs(embed)
        );

    }

    private Map<String, String> prepareArgs(MessageEmbed embed) {
        HashMap<String, String> result = new HashMap<>();
        result.put("Descripion", embed.getDescription());
        if (embed.getFooter() != null
                && embed.getFooter().getText() != null
                && !embed.getFooter().getText().isBlank()) {
            result.put("Footer", embed.getFooter().getText());
        }
        return result;
    }

}
