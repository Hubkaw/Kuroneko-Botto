package com.kuroneko.Database.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode
@ToString
@Table(name = "channel")
public class ChannelEntity {

    @Id
    @Column(length = 30)
    private String channelId;

    @Column
    private String channelName;

    @Column
    private Long guildId;

    @ManyToMany(cascade = {CascadeType.PERSIST},
            fetch = FetchType.EAGER)
    @JoinTable(
            name = "registered_in_channels",
            joinColumns = {@JoinColumn(name = "channelId")},
            inverseJoinColumns = {@JoinColumn(name = "puuid")})
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<SummonerEntity> summoners = new HashSet<>();
}
