package com.kuroneko.config;

import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.impl.R4J;
import org.springframework.stereotype.Component;

@Component
public class RiotApi extends R4J {
    public RiotApi() {
        super(new APICredentials(ConfigLoader.getConfig().getRiotToken()));
    }
}
