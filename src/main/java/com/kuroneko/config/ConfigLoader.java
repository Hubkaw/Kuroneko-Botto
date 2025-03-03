package com.kuroneko.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.core.io.FileSystemResource;

import java.nio.file.Path;
import java.nio.file.Paths;


public class ConfigLoader {

    private static Config config;
    private static Dotenv ENV;
    ConfigLoader(Dotenv env) {
        ENV = env;
    }
    public static Config getConfig(){
        if (config == null){
            checkAndLoadConfig();
        }
        return config;
    }
    private static void checkAndLoadConfig(){
        Path p = Paths.get(System.getProperty("user.home"), "kuroneko_config.json");
        if(p.toFile().exists()){
           config = configLoader();
        }else{
          generateConfig();

        }
    }

    private static void generateConfig() {
        config = new Config();
        config.setDbUser(ENV.get("DB_USER"));
        config.setDbPassword(ENV.get("DB_PASSWORD"));
        config.setDiscordToken(ENV.get("DISCORD_TOKEN"));
        config.setRiotToken(ENV.get("RIOT_TOKEN"));
        config.setYoutubeToken(ENV.get("YOUTUBE_TOKEN"));
    }

    private static Config configLoader() {
        try {
            Path path = Paths.get(System.getProperty("user.home"), "kuroneko_config.json");
            FileSystemResource f = new FileSystemResource(path);
            String rawFile = new String(f.getContentAsByteArray());
            return new ObjectMapper().readValue(rawFile, Config.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

