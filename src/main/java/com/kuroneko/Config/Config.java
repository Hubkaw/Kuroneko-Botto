package com.kuroneko.Config;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;


@NoArgsConstructor
@AllArgsConstructor
public class Config {
    @Getter
    private String discordToken;
    @Getter
    private String ownerID;
    @Getter
    private String riotToken;
    @Getter
    private Long olekID;
    @Getter
    private String dbUser;
    @Getter
    private String dbPassword;

    private static Config config;

    private final static String defaultPath = System.getProperty("user.home") + "/kuroneko_config.json";


    public static Config getConfig(){
        if (config == null){
            createConfig();
        }
        return config;
    }

    private static void createConfig(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(defaultPath);
            config = objectMapper.readValue(file, Config.class);

        } catch (Exception e) {
            System.out.println(defaultPath + " might be empty");
            e.printStackTrace();
        }
    }

}
