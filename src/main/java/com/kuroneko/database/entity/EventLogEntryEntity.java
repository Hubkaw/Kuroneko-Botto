package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventLogEntryEntity {

    @Id
    @GeneratedValue
    private Long id;
    private EventType eventType;
    private String eventName;
    private LocalDateTime timestamp;
    private Long userId;
    private String username;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private ChannelEntity channel;

    @OneToMany(mappedBy = "loggedCommand", cascade = CascadeType.ALL)
    private List<EventLogArgumentEntity> arguments = new ArrayList<>();

    public enum EventType {
        SLASH_COMMAND,
        LEAGUE_UPDATE,
        BUTTON_INTERACTION,
        CUSTOM_MESSAGE_RESPONSE
    }
}
