package com.kuroneko.Music;

import com.kuroneko.Config.KuronekoEmbed;
import com.kuroneko.LavaPlayer.PlayerManager;
import com.kuroneko.LavaPlayer.TrackScheduler;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Skip implements MusicInteraction {

    @Getter
    private final String name = "skip";

    @Override
    public ReplyRemover execute(SlashCommandInteractionEvent event) {
        Member selfMember = event.getGuild().getSelfMember();
        OptionMapping amount = event.getOption("amount");
        TrackScheduler scheduler = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler;
        if(event.getMember().getVoiceState().getChannel()==selfMember.getVoiceState().getChannel()) {
            if (amount == null || amount.getAsString().isBlank()){
                scheduler.skip(1);
                return new MessageDeleter(event.replyEmbeds(skipResponse(1)).complete());
            }
            if (amount.getAsString().equalsIgnoreCase("all")){
                int i = scheduler.skipAll();
                return new MessageDeleter(event.replyEmbeds(skipResponse(i)).complete());
            }
            try {
                scheduler.skip(amount.getAsInt());
                return new MessageDeleter(event.replyEmbeds(skipResponse(amount.getAsInt())).complete());
            } catch (Exception e){
                MessageEmbed build = new KuronekoEmbed().setTitle("Baakaaaa!").setDescription("You can't provide " + amount.getAsString() + " here. Give me a number you mouth-breather").build();
                return new MessageDeleter(event.replyEmbeds(build).complete());
            }
        }
        MessageEmbed build = new KuronekoEmbed()
                .setTitle("Senpaiii~...")
                .setDescription("We are not in the same voice channel. Come join me at: "
                        + selfMember.getVoiceState().getChannel().getAsMention() + "!").build();
        return new MessageDeleter(event.replyEmbeds(build).complete());
    }

    private MessageEmbed skipResponse(int i){
        return new KuronekoEmbed().setTitle("Skip").setDescription("Skipped " + i + " entries.").build();
    }

    @Override
    public CommandData getCommand() {
        SlashCommandData commandData = Commands.slash(getName(), "skip current song");
        commandData.addOption(OptionType.STRING, "amount", "Amount of songs to skip. e.g. 5, 13, all", false);
        return commandData;
    }
}
