package com.kuroneko.interaction.rp.dnd;

import com.kuroneko.database.entity.DnDSpellEntity;
import com.kuroneko.misc.KuronekoEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

@Component
public class SingleSpellMessageHandler {

    public MessageEmbed handle(DnDSpellEntity spell) {
        EmbedBuilder embedBuilder = new KuronekoEmbed().setTitle(spell.getName()).setThumbnail(null);

        MessageEmbed.Field classes = new MessageEmbed.Field("Classes:", writeClasses(spell), true);
        embedBuilder.addField(classes);

        MessageEmbed.Field school = new MessageEmbed.Field("School:", spell.getSchool(), true);
        embedBuilder.addField(school);

        MessageEmbed.Field level = new MessageEmbed.Field("Spell Level:", Integer.toString(spell.getLevel()), true);
        embedBuilder.addField(level);

        MessageEmbed.Field description = new MessageEmbed.Field("Description:", spell.getDescription(), false);
        embedBuilder.addField(description);

        if (spell.getUpcast() != null && !spell.getUpcast().isBlank()) {
            MessageEmbed.Field upcast = new MessageEmbed.Field("Higher Level:", spell.getUpcast(), false);
            embedBuilder.addField(upcast);
        }

        if(spell.getComponents() != null && !spell.getComponents().isBlank()) {
            MessageEmbed.Field components = new MessageEmbed.Field("Components:", spell.getComponents(), true);
            embedBuilder.addField(components);
        }

        if (spell.getMaterial() != null && !spell.getMaterial().isBlank()) {
            MessageEmbed.Field material = new MessageEmbed.Field("Material:", spell.getMaterial(), false);
            embedBuilder.addField(material);
        }

        MessageEmbed.Field castTime = new MessageEmbed.Field("Cast Time:", spell.getCastingTime(), true);
        embedBuilder.addField(castTime);

        MessageEmbed.Field ritual = new MessageEmbed.Field("Ritual:", yesNo(spell.isRitual()), true);
        embedBuilder.addField(ritual);

        MessageEmbed.Field concentration = new MessageEmbed.Field("Concentration:", yesNo(spell.isConcentration()), true);
        embedBuilder.addField(concentration);
        return embedBuilder.build();
    }

    private String writeClasses(DnDSpellEntity spell) {
        StringBuilder sb = new StringBuilder();
        spell.getClasses().forEach(c -> {
            if (!sb.isEmpty()) {
                sb.append(", ");
            }
            sb.append(c.getName());
        });
        return sb.toString();
    }

    private String yesNo(Boolean bool) {
        return bool ? "Yes" : "No";
    }
}
