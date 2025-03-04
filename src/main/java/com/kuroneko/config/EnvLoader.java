package com.kuroneko.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class EnvLoader {

    public static Dotenv Env() {
        Path envPath = Paths.get(".env");
        if(Files.exists(envPath)) {
            System.out.println(".env loaded");
            return Dotenv.load();
        }
        else{
            System.err.println(".env not loaded");
            return null;
        }
    }
}
