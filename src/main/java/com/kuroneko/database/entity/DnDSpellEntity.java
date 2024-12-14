package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "dnd_spell", indexes = {
        @Index(columnList = "name", unique = true, name = "idx_spell_name"),
})
public class DnDSpellEntity {

    @Id
    @Column(name = "index")
    private String index;

    @Column(name = "name")
    private String name;

    @Column(length = 4096)
    private String description;

    @Column(length = 4096)
    private String upcast;

    @Column
    private String range;

    @Column
    private String components;

    @Column(length = 4096)
    private String material;

    @Column
    private boolean ritual;

    @Column
    private String duration;

    @Column
    private boolean concentration;

    @Column
    private String castingTime;

    @Column
    private int level;

    @Column
    private String attackType;

    @Column
    private String school;

    @Column
    private boolean downloaded;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "spell_classes",
            joinColumns = @JoinColumn(name = "spell_id", referencedColumnName = "index"),
            inverseJoinColumns = @JoinColumn(name = "class_name", referencedColumnName = "index")
    )
    private List<CharacterClass> classes;

    @Entity
    @Table(name = "dnd_class")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CharacterClass {

        @Id
        @Column(name = "index")
        private String index;

        @Column
        private String name;
    }


}
