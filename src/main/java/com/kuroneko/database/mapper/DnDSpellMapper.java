package com.kuroneko.database.mapper;

import com.kuroneko.database.entity.DnDSpellEntity;
import com.kuroneko.misc.SpellDetailsResponse;
import com.kuroneko.misc.SpellListResponse;

import java.util.List;

public class DnDSpellMapper {
    public static DnDSpellEntity map(SpellListResponse.SpellResult spellResult) {
        DnDSpellEntity dnDSpellEntity = new DnDSpellEntity();
        dnDSpellEntity.setIndex(spellResult.getIndex());
        dnDSpellEntity.setName(spellResult.getName());
        dnDSpellEntity.setDownloaded(false);
        dnDSpellEntity.setLevel(spellResult.getLevel());
        return dnDSpellEntity;
    }

    public static DnDSpellEntity mapDetails(DnDSpellEntity spellEntity, SpellDetailsResponse spellDetails) {
        spellEntity.setDescription(mergeStrings(spellDetails.getDescription()));
        spellEntity.setComponents(spellEntity.getComponents());
        spellEntity.setConcentration(spellEntity.isConcentration());
        spellEntity.setRange(spellDetails.getRange());
        spellEntity.setSchool(spellDetails.getSchool().getName());
        spellEntity.setAttackType(spellDetails.getAttackType());
        spellEntity.setMaterial(spellDetails.getMaterial());
        spellEntity.setDuration(spellDetails.getDuration());
        spellEntity.setDownloaded(true);
        spellEntity.setRitual(spellDetails.isRitual());
        spellEntity.setCastingTime(spellDetails.getCastingTime());
        spellEntity.setUpcast(mergeStrings(spellDetails.getHigherLevel()));
        spellDetails.getClasses()
                .forEach(c -> spellEntity.getClasses()
                        .add(new DnDSpellEntity.CharacterClass(c.getIndex(), c.getName())));
        return spellEntity;
    }

    private static String mergeStrings(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            if (!sb.isEmpty()) {
                sb.append("\n\n");
            }
            sb.append(string);
        }
        return sb.toString();
    }
}
