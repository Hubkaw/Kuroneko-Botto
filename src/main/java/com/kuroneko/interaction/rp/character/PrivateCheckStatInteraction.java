package com.kuroneko.interaction.rp.character;


import com.kuroneko.database.repository.PlayerCharacterRepository;
import com.kuroneko.misc.RNG;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

@Component
public class PrivateCheckStatInteraction extends CheckStatInteraction{
    public PrivateCheckStatInteraction(PlayerCharacterRepository pcr,
                                      RNG rng){
        super(pcr, rng);
    }

    @Override
    public String getName() {
        return "private-check";
    }

    @Override
    void sendMessage(SlashCommandInteractionEvent event, MessageEmbed embed) {
        event.replyEmbeds(embed).setEphemeral(true).queue();
        PrivateChannel dm = event.getOption("gm").getAsUser().openPrivateChannel().complete();
        dm.sendMessageEmbeds(embed).queue();
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Test umiejętności")
                .addOption(OptionType.STRING, "stat", "wybierz umiejętność", true, true)
                .addOption(OptionType.MENTIONABLE, "gm", "@ twojego GMa", true)
                .addOption(OptionType.INTEGER, "bonus", "bonus sytuacyjny", false);
    }
}
