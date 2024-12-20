package com.kuroneko.interaction.rp.inititative;

import com.kuroneko.misc.RNG;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Participant implements Comparable<Participant> {


    private static final Pattern pattern = Pattern.compile("^(([(0-9]+)\\s?[xX])?([\\w ]+)(\\s?[+-]\\s?[0-9]+)?$", Pattern.UNICODE_CHARACTER_CLASS);

    @Getter
    private String name;
    private int initiative;


    public Participant(String name, int modifier, RNG rng) {
        this.name = name;
        this.initiative = rng.rollInt(20) + 1 + modifier;
    }

    public static List<Participant> parse(String line, RNG rng) {
        Matcher matcher = pattern.matcher(line);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid character added " + line);
        }

        int amount = matcher.group(2) != null ? Integer.parseInt(matcher.group(2).replace(" ", "")) : 1;
        String name = matcher.group(3);
        int modifier = matcher.group(4) != null ? Integer.parseInt(matcher.group(4).replace(" ", "")) : 0;

        List<Participant> participants = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            if (amount > 1) {
                participants.add(new Participant(name + " " + i, modifier, rng));
            } else {
                participants.add(new Participant(name, modifier, rng));
            }
        }
        return participants;
    }

    @Override
    public int compareTo(Participant other) {
        return Integer.compare(other.initiative, this.initiative);
    }

    @Override
    public String toString() {
        return String.format("%s - Initiative: **%d**", name, initiative);
    }

}