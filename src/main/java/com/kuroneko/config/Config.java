package com.kuroneko.config;

import lombok.Data;

@Data
public class Config {
    private String discordToken;
    private String ownerID;
    private String riotToken;
    private Long olekID;
    private String dbUser;
    private String dbPassword;
}
