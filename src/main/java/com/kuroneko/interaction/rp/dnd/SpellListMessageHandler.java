package com.kuroneko.interaction.rp.dnd;

import com.kuroneko.database.entity.DnDSpellEntity;
import com.kuroneko.misc.KuronekoEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpellListMessageHandler {

    public MessageEmbed handle(List<DnDSpellEntity> spells) {

        KuronekoEmbed kuronekoEmbed = new KuronekoEmbed();
        kuronekoEmbed.setThumbnail(null);
        kuronekoEmbed.setTitle("Spell List");

        if (spells.size() > 25 ) {
            kuronekoEmbed.setDescription("Please be more specific. I have found " + spells.size() + " spells.");
        } else {
            kuronekoEmbed.setDescription(listSpells(spells));
        }

        return kuronekoEmbed.build();
    }

    private String listSpells(List<DnDSpellEntity> spells) {
        StringBuilder sb = new StringBuilder();
        for (DnDSpellEntity spell : spells) {
            if (!sb.isEmpty()){
                sb.append("\n");
            }
            sb.append("**");
            sb.append(spell.getName());
            sb.append("**");
        }
        return sb.toString();
    }
}
