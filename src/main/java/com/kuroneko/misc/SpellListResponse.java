package com.kuroneko.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpellListResponse {

    @JsonProperty("count")
    private int count;

    @JsonProperty("results")
    private List<SpellResult> results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpellResult {

        @JsonProperty("index")
        private String index;

        @JsonProperty("name")
        private String name;

        @JsonProperty("level")
        private int level;

        @JsonProperty("url")
        private String url;
    }
}