package com.kuroneko.interaction.music;

import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.lavaplayer.PlayerManager;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@AllArgsConstructor
public abstract class MusicInteraction implements SlashInteraction {

    protected PlayerManager playerManager;

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "");
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
    }
}
