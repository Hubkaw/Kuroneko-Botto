package com.kuroneko.interaction;

import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ButtonInteraction {
    int getId();
    Button getButton();
    void execute(ButtonInteractionEvent event);
}
