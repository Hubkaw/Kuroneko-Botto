package com.kuroneko.interaction.rp.roll;


import com.kuroneko.misc.RNG;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class Dices {

    private static final Pattern dicePattern = Pattern.compile("(([0-9]*)?[d|k|D|K])?([0-9]+)([*])?([+-])?([0-9]+)?");
    private static final Logger log = LoggerFactory.getLogger(Dices.class);

    boolean isValid;
    private boolean isBonusPerRoll;
    private int flatBonus;
    private int amount;
    private int value;
    private String pattern;
    boolean isRolled = false;
    List<Integer> results = new ArrayList<>();

    public Dices(String input) {
        try {

            Matcher matcher = dicePattern.matcher(input);
            if (matcher.matches()) {
                amount = matcher.group(2) == null || matcher.group(2).isBlank() ? 1 : Integer.parseInt(matcher.group(2));
                value = Integer.parseInt(matcher.group(3));
                pattern = input;
                isBonusPerRoll = matcher.group(4) != null && !matcher.group(4).isBlank();
                if (matcher.group(5) != null && !matcher.group(5).isBlank() && matcher.group(6) != null && !matcher.group(6).isBlank()) {
                    flatBonus = Integer.parseInt(matcher.group(5) + matcher.group(6));
                } else {
                    flatBonus = 0;
                }
                isValid = amount <= 100 && value <= 10000;
            } else {
                isValid = false;
            }
        } catch (Exception e) {
            isValid = false;
            log.info("Unhandled dice input: {}", input);
        }
    }

    public List<Integer> roll(RNG rng) {
        if (!isValid) {
            return null;
        }
        if (isRolled) {
            return results;
        }
        for (int i = 0; i < amount; i++) {
            results.add(rng.rollInt(value) + 1);
        }
        isRolled = true;
        return results;
    }

    public String getResultAsString(RNG rng) {
        if (!isValid) {
            throw new IllegalArgumentException("Invalid dice format");
        }
        if (!isRolled && rng == null) {
            throw new IllegalStateException("Dices not rolled");
        }
        if (!isRolled) {
            roll(rng);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("**").append(amount).append("d").append(value);
        if (flatBonus != 0) {
            if (isBonusPerRoll) {
                sb.append("*");
            }
            if (flatBonus > 0) {
                sb.append("+");
            }
            sb.append(flatBonus);
        }
        sb.append("**: [");
        if (getResults().size() > 1 || flatBonus != 0) {
            sb.append("(");
        }
        int sum = 0;
        for (int i = 0; i < amount; i++) {
            if (i != 0) {
                sb.append(" -");
            }
            Integer i1 = results.get(i);
            sb.append(" ").append(i1).append(" ");
            sum += isBonusPerRoll ? i1 + flatBonus : i1;
        }

        if (flatBonus != 0) {
            sb.append("/ ");
            if (isBonusPerRoll) {
                sb.append("*");
            }
            if (flatBonus > 0) {
                sb.append("+");
            }
            sb.append(flatBonus).append(" ");
        }

        if (getResults().size() > 1 || flatBonus != 0) {
            sb.append(") ");
            int total = isBonusPerRoll ? sum : sum + flatBonus;
            sb.append("S: ").append(total);
        }
        if (getResults().size() > 1) {
            String avg = new DecimalFormat("#.###").format(((double) sum) / ((double) amount));
            sb.append(" A: ").append(avg);
        }
        sb.append("]\n");

        return sb.toString();
    }

}
