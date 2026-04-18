package com.kuroneko.interaction.rp.inititative;

import com.kuroneko.interaction.ButtonInteraction;
import lombok.Getter;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class InitiativeFinishButton implements ButtonInteraction {

    @Getter
    private Button button;
    private final InitiativeCheckManager icm;

    public InitiativeFinishButton(InitiativeCheckManager icm) {
        this.icm = icm;
        button = Button.primary("initiative-finish", "Finish");
    }

    @Override
    public int getId() {
        return button.getUniqueId();
    }

    @Override
    public void execute(ButtonInteractionEvent event) {

        InitiativeCheck initiativeCheck = icm.getInitiativeCheck(event.getChannelId());
        if (initiativeCheck == null) {
            return;
        }

        MessageEmbed embed = icm.buildEmbed(event.getChannelId());

        initiativeCheck.getOriginalMessage().editOriginalEmbeds(embed).queue();
        initiativeCheck.getOriginalMessage().editOriginalComponents().queue();
        icm.endInitiative(event.getChannelId());
    }

}
