package com.kuroneko.interaction;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public interface ButtonInteraction {
    String getId();
    Button getButton();
    void execute(ButtonInteractionEvent event);
}
