package com.kuroneko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KuronekoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KuronekoApplication.class, args);
    }

}
