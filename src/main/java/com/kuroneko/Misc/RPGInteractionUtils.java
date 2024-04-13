package com.kuroneko.misc;

import com.kuroneko.database.entity.CharacterStatsEntity;

public class RPGInteractionUtils {
    public static String writeDescription(CharacterStatsEntity cse) {
        return """
                ```
                +------+------+------+-----+------+------+------+------+
                |  WW  |  US  |  K   | Odp |  Zr  | Int  |  SW  | Ogd  |
                +------+------+------+-----+------+------+------+------+
                |  %s  |  %s  |  %s  | %s  |  %s  |  %s  |  %s  |  %s  |
                +------+------+------+-----+------+------+------+------+
                ```
                """.formatted(
                formatStat(cse.getWarriorSkill()),
                formatStat(cse.getBallisticSkill()),
                formatStat(cse.getStrength()),
                formatStat(cse.getToughness()),
                formatStat(cse.getAgility()),
                formatStat(cse.getIntelligence()),
                formatStat(cse.getWillPower()),
                formatStat(cse.getFelicity())
        );
    }
    private static String formatStat(int stat) {
        return stat < 10 ? " " + stat : Integer.toString(stat);
    }
}
