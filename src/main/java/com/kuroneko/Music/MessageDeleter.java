package com.kuroneko.Music;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.InteractionHook;

@AllArgsConstructor
public class MessageDeleter implements ReplyRemover{

    InteractionHook interaction;

    @Getter
    int delay;

    @Override
    public Runnable getAction() {
        return () -> interaction.deleteOriginal().queue();
    }

    public MessageDeleter(InteractionHook interaction){
        this(interaction, 12);
    }
}
