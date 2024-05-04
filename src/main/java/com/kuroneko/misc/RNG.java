package com.kuroneko.misc;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RNG {

    private final Random random = new Random(System.currentTimeMillis());

    public int rollInt(int cap){
        return random.nextInt(cap);
    }
}
