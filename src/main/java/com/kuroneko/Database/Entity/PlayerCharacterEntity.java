package com.kuroneko.database.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@IdClass(PlayerCharacterEntity.Pk.class)
@Table(name = "player_character")
public class PlayerCharacterEntity {

    @Id
    private String owner;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private ChannelEntity channel;

    private String name;
    private String imageLink;

    @OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private CharacterStatsEntity stats;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Pk implements Serializable {
        String owner;
        String channel;
    }
}
