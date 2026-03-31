package com.kuroneko.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kuroneko.id")
public record KuronekoIDs(
        long owner,
        long olek,
        long ponyskiller
) {
}
