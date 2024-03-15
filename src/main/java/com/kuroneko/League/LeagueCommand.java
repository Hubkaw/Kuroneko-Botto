package com.kuroneko.League;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class LeagueCommand {

    public abstract void execute(MessageReceivedEvent messageReceivedEvent, String command);
}
