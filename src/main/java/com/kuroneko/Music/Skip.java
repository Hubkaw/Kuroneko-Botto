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

import java.util.concurrent.TimeUnit;

public class Skip implements MusicInteraction {

    @Getter
    private final String name = "skip";

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member selfMember = event.getGuild().getSelfMember();
        OptionMapping amount = event.getOption("amount");
        TrackScheduler scheduler = PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler;
        if(event.getMember().getVoiceState().getChannel()==selfMember.getVoiceState().getChannel()) {
            if (amount == null || amount.getAsString().isBlank()){
                int skipped = scheduler.skip(1);
                event.replyEmbeds(skipResponse(skipped)).complete().deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
                return;
            }
            if (amount.getAsString().equalsIgnoreCase("all")){
                int i = scheduler.skipAll();
                event.replyEmbeds(skipResponse(i)).complete().deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
                return;
            }
            try {
                int skipped = scheduler.skip(amount.getAsInt());
                event.replyEmbeds(skipResponse(skipped)).complete().deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
                return;
            } catch (Exception e){
                MessageEmbed build = new KuronekoEmbed().setTitle("Baakaaaa!").setDescription("You can't provide " + amount.getAsString() + " here. Give me a number you mouth-breather").build();
                event.replyEmbeds(build).complete().deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
                return;
            }
        }
        MessageEmbed build = new KuronekoEmbed()
                .setTitle("Senpaiii~...")
                .setDescription("We are not in the same voice channel. Come join me at: "
                        + selfMember.getVoiceState().getChannel().getAsMention() + "!").build();
        event.replyEmbeds(build).complete().deleteOriginal().queueAfter(12, TimeUnit.SECONDS);
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
