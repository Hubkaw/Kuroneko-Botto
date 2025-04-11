package com.kuroneko.interaction;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
public class ButtonInteractionManager extends ListenerAdapter {

    private final Map<String, ButtonInteraction> buttonMap = new TreeMap<>();
    private ButtonInteractionLogger logger;

    public ButtonInteractionManager(Set<ButtonInteraction> buttonInteractions,
                                    ButtonInteractionLogger logger) {
        this.logger = logger;
        buttonInteractions.forEach(bi -> buttonMap.put(bi.getId(), bi));
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        ButtonInteraction buttonInteraction = buttonMap.get(event.getButton().getId());

        if (buttonInteraction != null) {
            logger.log(event);
            buttonInteraction.execute(event);
        }
    }
}
