package com.kuroneko.database.mapper;
import com.kuroneko.database.entity.CharacterStatsEntity;
import com.kuroneko.misc.Ability;
import java.util.Map;

public class StatMapper {
    public static CharacterStatsEntity map(Map<Ability, Integer> source){
        CharacterStatsEntity characterStatsEntity = new CharacterStatsEntity();
        characterStatsEntity.setAgility(source.get(Ability.ZR));
        characterStatsEntity.setIntelligence(source.get(Ability.INT));
        characterStatsEntity.setFelicity(source.get(Ability.OGD));
        characterStatsEntity.setStrength(source.get(Ability.K));
        characterStatsEntity.setToughness(source.get(Ability.ODP));
        characterStatsEntity.setBallisticSkill(source.get(Ability.US));
        characterStatsEntity.setWillPower(source.get(Ability.SW));
        characterStatsEntity.setWarriorSkill(source.get(Ability.WW));
        return characterStatsEntity;
    }
}
