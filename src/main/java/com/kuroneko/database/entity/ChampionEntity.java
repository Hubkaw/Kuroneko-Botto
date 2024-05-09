package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class ChampionEntity {

    @Id
    private int id;

    private String name;

    private String title;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "champion", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ChampionMasteryEntity> championMasteries = new HashSet<>();
}
