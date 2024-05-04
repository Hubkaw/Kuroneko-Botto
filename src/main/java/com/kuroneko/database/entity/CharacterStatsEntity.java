package com.kuroneko.database.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "main_stats")
public class CharacterStatsEntity {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne(mappedBy = "stats")
    private PlayerCharacterEntity character;

    private int warriorSkill;
    private int ballisticSkill;
    private int strength;
    private int toughness;
    private int agility;
    private int intelligence;
    private int willPower;
    private int felicity;
}
