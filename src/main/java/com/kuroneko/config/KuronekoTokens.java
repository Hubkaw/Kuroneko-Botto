package com.kuroneko.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kuroneko.tokens")
public record KuronekoTokens(
        String youtube,
        String discord,
        String riot
) {
}