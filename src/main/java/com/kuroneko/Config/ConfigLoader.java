package com.kuroneko.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;


public class ConfigLoader {

    private static Config config;

    public static Config getConfig(){
        if (config == null){
            config = configLoader();
        }
        return config;
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

