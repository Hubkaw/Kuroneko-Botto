package com.kuroneko.interaction.rp.roll;


import com.kuroneko.misc.RNG;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kuroneko.config.CONSTANTS.*;

public class Dices {

    private static final Pattern dicePattern = Pattern.compile("(([0-9]*)?([dkeDKE]))?([0-9]+)([*])?([+-])?([0-9]+)?");
    private static final Logger log = LoggerFactory.getLogger(Dices.class);

    private String input;
    private int amount = 1;
    private int value;
    private int bonus = 0;
    private boolean isBonusPerRoll;
    private boolean isExploding;

    private List<RollOutcome> rollOutcomes = new ArrayList<>();

    public Dices(String input, RNG rng) {
        Matcher matcher = dicePattern.matcher(input);
        if (!matcher.matches()) {
            log.info("Unhandled dice input: {}", input);
            throw new IllegalArgumentException("Invalid dice");
        }

        try {
            if (matcher.group(2) != null || !matcher.group(2).isBlank()) {
                amount = Integer.parseInt(matcher.group(2));
            }

            isExploding = matcher.group(3) != null && !matcher.group(3).isBlank() && matcher.group(3).equalsIgnoreCase("e");

            value = Integer.parseInt(matcher.group(4));

            isBonusPerRoll = matcher.group(5) != null && !matcher.group(5).isBlank();

            int sign;
            if (matcher.group(6) == null || matcher.group(6).isBlank() || !matcher.group(6).equals("-")) {
                sign = 1;
            } else {
                sign = -1;
            }

            if (matcher.group(7) != null && !matcher.group(7).isBlank()) {
                bonus = Integer.parseInt(matcher.group(7)) * sign;
            }

            if (amount < LOWEST_DICE_AMOUNT || amount > HIGHEST_DICE_AMOUNT
                    || value < LOWEST_DICE_VALUE || value > HIGHEST_DICE_VALUE) {
                throw new IllegalArgumentException("Dice out of bounds");
            }
        } catch (Exception e) {
            log.info("Unhandled dice input: {}", input);
            throw new IllegalArgumentException("Invalid dice");
        }
        this.input = input.toLowerCase();

        roll(rng);
    }

    private void roll(RNG rng) {
        for (int i = 0; i < amount; i++) {
            rollOutcome(rng);
        }
    }


    private void rollOutcome(RNG rng) {
        rollOutcome(rng, false);
    }

    private void rollOutcome(RNG rng, boolean isExploded) {
        RollOutcome rollOutcome = new RollOutcome();
        rollOutcome.isExploded = isExploded;
        rollOutcome.setValue(rng.rollInt(value) + 1);
        if (bonus != 0 && isBonusPerRoll) {
            rollOutcome.setBonus(bonus);
        }
        rollOutcomes.add(rollOutcome);
        if (isExploding && rollOutcome.getValue() == value) {
            rollOutcome.setExploding(true);
            rollOutcome(rng, true);
        }
    }


    public String getAsString() {
        return "**%s:** [(%s) SUM: **%s**]\n".formatted(input, createDiceString(), getSum());
    }

    private String createDiceString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < rollOutcomes.size(); i++) {
            RollOutcome r = rollOutcomes.get(i);

            sb.append(r.getValue() + r.getBonus());

            if (i != rollOutcomes.size() - 1) {
                if (r.isExploding) {
                    sb.append("!");
                } else {
                    sb.append(" ~ ");
                }
            }
        }

        if (!isBonusPerRoll && bonus != 0) {
            sb.append(" / ").append(bonus);
        }

        return sb.toString();
    }

    public Integer getSum() {
        AtomicInteger outcome = new AtomicInteger(0);
        if (isBonusPerRoll) {
            rollOutcomes.forEach(r -> {
                outcome.getAndAdd(r.getValue());
                outcome.getAndAdd(r.getBonus());
            });
        } else {
            rollOutcomes.forEach(r -> {
                outcome.getAndAdd(r.getValue());
            });
            outcome.getAndAdd(bonus);
        }
        return outcome.get();
    }


    @Data
    private class RollOutcome {
        private int value;
        private int bonus = 0;
        private boolean isExploding = false;
        private boolean isExploded = false;
    }

}
