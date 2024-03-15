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
@Table(name = "registered", uniqueConstraints = {@UniqueConstraint(columnNames = {"summoner_name"})})
public class SummonerEntity {

    @Column(name = "summoner_name", length = 16)
    private String name;

    @Id
    @Column(length = 80)
    private String puuid;

    @Column
    private String id;

    @Column
    private String accountId;

    @Column
    private int level;

    @ManyToMany(mappedBy = "summoners",
            fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ChannelEntity> channels = new HashSet<>();

    @OneToMany(
            mappedBy = "summoner",
            fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<RankEntity> ranks = new HashSet<>();

    @OneToMany(
            mappedBy = "summoner",
            fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ChampionMasteryEntity> championMasteries = new HashSet<>();

}
