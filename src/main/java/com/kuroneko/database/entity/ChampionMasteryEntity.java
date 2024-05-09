package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"summoner", "champion"})})
public class ChampionMasteryEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "summoner")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private SummonerEntity summoner;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "champion")
    private ChampionEntity champion;

    private int points;

    private int level;

}
