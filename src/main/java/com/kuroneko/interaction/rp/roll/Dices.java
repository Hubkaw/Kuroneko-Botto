package com.kuroneko.interaction.rp.roll;


import com.kuroneko.misc.RNG;
import lombok.Data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class Dices {

    private static final Pattern dicePattern = Pattern.compile("(([0-9]*)?[d|k|D|K])?([0-9]+)");

    boolean isValid;
    private int amount;
    private int value;
    private String pattern;
    boolean isRolled = false;
    List<Integer> results = new ArrayList<>();

    public Dices(String input) {
        Matcher matcher = dicePattern.matcher(input);
        if (matcher.matches()) {
            amount = matcher.group(2) == null || matcher.group(2).isBlank() ? 1 : Integer.parseInt(matcher.group(2));
            value = Integer.parseInt(matcher.group(3));
            pattern = input;
            isValid = amount <= 100 && value <= 10000;
        } else {
            isValid = false;
        }
    }

    public List<Integer> roll(RNG rng){
        if (!isValid) {
            return null;
        }
        if (isRolled){
            return results;
        }
        for(int i = 0; i < amount; i++){
            results.add(rng.rollInt(value) + 1);
        }
        isRolled = true;
        return results;
    }

    public String getResultAsString(RNG rng){
        if (!isValid) {
            throw  new IllegalArgumentException("Invalid dice format");
        }
        if (!isRolled && rng == null){
            throw new IllegalStateException("Dices not rolled");
        }
        if (!isRolled){
            roll(rng);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("**").append(amount).append("d").append(value).append("**: [");
        if (getResults().size()>1){
            sb.append("(");
        }
        int sum = 0;
        for (int i = 0; i < amount; i++) {
            if (i != 0) {
                sb.append(" -");
            }
            Integer i1 = results.get(i);
            sb.append(" ").append(i1).append(" ");
            sum += i1;
        }

        if (getResults().size() > 1) {
            sb.append(") ");
            String avg = new DecimalFormat("#.###").format(((double) sum) / ((double) amount));
            sb.append("SUM: ").append(sum).append(" AVG: ").append(avg);
        }
        sb.append("]\n");

        return sb.toString();
    }

    public String getResultAsString(){
        return getResultAsString(null);
    }
}
