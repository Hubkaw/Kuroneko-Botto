package com.kuroneko.message;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface CustomMessageResponse {
    boolean isRelevant(MessageReceivedEvent event);
    void execute(MessageReceivedEvent event);
}
