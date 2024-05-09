package com.kuroneko.misc;

import no.stelar7.api.r4j.basic.constants.types.lol.TierDivisionType;

public class LeagueTierHelper {
    public static int giveTierValueAsc(TierDivisionType tier) {
        return switch (tier) {
            case IRON_V -> 0;
            case IRON_IV -> 1;
            case IRON_III -> 2;
            case IRON_II -> 3;
            case IRON_I -> 4;
            case BRONZE_V -> 5;
            case BRONZE_IV -> 6;
            case BRONZE_III -> 7;
            case BRONZE_II -> 8;
            case BRONZE_I -> 9;
            case SILVER_V -> 10;
            case SILVER_IV -> 11;
            case SILVER_III -> 12;
            case SILVER_II -> 13;
            case SILVER_I -> 14;
            case GOLD_V -> 15;
            case GOLD_IV -> 16;
            case GOLD_III -> 17;
            case GOLD_II -> 18;
            case GOLD_I -> 19;
            case PLATINUM_V -> 20;
            case PLATINUM_IV -> 21;
            case PLATINUM_III -> 22;
            case PLATINUM_II -> 23;
            case PLATINUM_I -> 24;
            case EMERALD_V -> 25;
            case EMERALD_IV -> 26;
            case EMERALD_III -> 27;
            case EMERALD_II -> 28;
            case EMERALD_I -> 29;
            case DIAMOND_V -> 30;
            case DIAMOND_IV -> 31;
            case DIAMOND_III -> 32;
            case DIAMOND_II -> 33;
            case DIAMOND_I -> 34;
            case MASTER_I -> 35;
            case GRANDMASTER_I -> 36;
            case CHALLENGER_I -> 37;
            default -> -1;
        };
    }

    public static int compareTiers(TierDivisionType tier1, TierDivisionType tier2) {
        int x = giveTierValueAsc(tier1);
        int y = giveTierValueAsc(tier2);
        return Integer.compare(x, y);
    }
}
