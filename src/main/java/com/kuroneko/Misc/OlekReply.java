package com.kuroneko.Misc;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OlekReply extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        nieKumam(event);

        Message message = event.getMessage();
        if(message.getContentRaw().toLowerCase().contains("rumia")
            || message.getEmbeds().stream().anyMatch(e -> isInEmbed(e, "rumia"))){
            message.reply("https://tenor.com/view/rumia-dance-gif-22869742").queue();
        }
    }

    private boolean isInEmbed(MessageEmbed embed, String string){
        if (embed.getTitle() != null && embed.getTitle().toLowerCase().contains(string)){
            return true;
        }
        if (embed.getDescription() != null && embed.getDescription().toLowerCase().contains(string)){
            return true;
        }
        return embed.getFields().stream().anyMatch(
                f -> f.getValue() != null
                        && f.getValue().toLowerCase().contains(string));
    }

    private void nieKumam(@NotNull MessageReceivedEvent event){
        if (event.getAuthor().getIdLong() != 296344793246728196L)
            return;
        if (event.getMessage().getContentRaw().toLowerCase().contains("nie kumam")){
            event.getMessage().reply(new StringBuilder(event.getMessage().getContentRaw()).reverse().toString()).queue();
        }
    }
}
