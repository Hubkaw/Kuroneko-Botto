package com.kuroneko.config;

import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.basic.cache.impl.EmptyCacheProvider;
import no.stelar7.api.r4j.basic.calling.DataCall;
import no.stelar7.api.r4j.impl.R4J;
import org.springframework.stereotype.Component;

@Component
public class RiotApi extends R4J {

    public RiotApi(KuronekoTokens appTokens) {
        super(new APICredentials(appTokens.riot()));
        DataCall.setCacheProvider(EmptyCacheProvider.INSTANCE);
    }
}
