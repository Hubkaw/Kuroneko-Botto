package com.kuroneko.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpellDetailsResponse {

    @JsonProperty("index")
    private String index;

    @JsonProperty("name")
    private String name;

    @JsonProperty("desc")
    private List<String> description;

    @JsonProperty("higher_level")
    private List<String> higherLevel;

    @JsonProperty("range")
    private String range;

    @JsonProperty("components")
    private List<String> components;

    @JsonProperty("material")
    private String material;

    @JsonProperty("ritual")
    private boolean ritual;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("concentration")
    private boolean concentration;

    @JsonProperty("casting_time")
    private String castingTime;

    @JsonProperty("level")
    private int level;

    @JsonProperty("attack_type")
    private String attackType;

    @JsonProperty("damage")
    private Damage damage;

    @JsonProperty("school")
    private School school;

    @JsonProperty("classes")
    private List<ClassInfo> classes;

    @JsonProperty("subclasses")
    private List<Subclass> subclasses;

    @JsonProperty("url")
    private String url;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Damage {
        @JsonProperty("damage_type")
        private Reference damageType;

        @JsonProperty("damage_at_slot_level")
        private Map<String, String> damageAtSlotLevel;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class School {
        @JsonProperty("index")
        private String index;

        @JsonProperty("name")
        private String name;

        @JsonProperty("url")
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassInfo {
        @JsonProperty("index")
        private String index;

        @JsonProperty("name")
        private String name;

        @JsonProperty("url")
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Subclass {
        @JsonProperty("index")
        private String index;

        @JsonProperty("name")
        private String name;

        @JsonProperty("url")
        private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reference {
        @JsonProperty("index")
        private String index;

        @JsonProperty("name")
        private String name;

        @JsonProperty("url")
        private String url;
    }
}