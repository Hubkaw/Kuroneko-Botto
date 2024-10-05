package com.kuroneko.interaction.rp.roll;

import com.kuroneko.database.entity.ChannelEntity;
import com.kuroneko.database.entity.MemberChannelEntity;
import com.kuroneko.database.mapper.ChannelMapper;
import com.kuroneko.database.repository.ChannelRepository;
import com.kuroneko.database.repository.MemberChannelRepository;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.KuronekoEmbed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

@Component
@AllArgsConstructor
public class RegisterRollImageInteraction implements SlashInteraction {

    private final static String name = "register-roll-image";
    private final static String linkOption = "link";

    private MemberChannelRepository mcRepository;
    private ChannelRepository channelRepository;

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        if (event.getMember() == null) {
            MessageEmbed build = new KuronekoEmbed().setTitle("Something went wrong").setDescription("You can only use this command on public channels").build();
            event.replyEmbeds(build).setEphemeral(true).queue();
            return;
        }

        Optional<ChannelEntity> foundChannel = channelRepository.findById(event.getChannelId());
        ChannelEntity channel = foundChannel.orElseGet(() -> channelRepository.save(ChannelMapper.map(event.getGuildChannel())));

        InteractionHook hook = event.deferReply().complete();
        OptionMapping option = event.getInteraction().getOption(linkOption);
        if (option != null && !option.getAsString().isBlank()) {
            try {
                new URL(option.getAsString()).toURI();
            } catch (URISyntaxException | MalformedURLException e) {
                hook.sendMessageEmbeds(errorMessage()).queue();
                return;
            }
        } else {
            hook.sendMessageEmbeds(errorMessage()).queue();
            return;
        }

        Optional<MemberChannelEntity> memberChannelEntity = mcRepository.findById(new MemberChannelEntity.Pk(event.getMember().getIdLong(), event.getChannelId()));
        MemberChannelEntity mc = memberChannelEntity.orElseGet(() -> createMemberChannel(event.getMember().getIdLong(), channel));

        mc.setRollImageLink(option.getAsString());
        mcRepository.save(mc);

        MessageEmbed build = new KuronekoEmbed().setTitle("Image set")
                .setDescription("New roll image set Senpai~~")
                .setThumbnail(mc.getRollImageLink())
                .build();
        hook.sendMessageEmbeds(build).queue();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(name, "register an image to be displayed when you use /roll")
                .addOption(OptionType.STRING, linkOption, "Link to the image", true, false);
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
    }

    private MemberChannelEntity createMemberChannel(long memberId, ChannelEntity channel) {
        MemberChannelEntity mc = new MemberChannelEntity();
        mc.setMemberId(memberId);
        mc.setChannel(channel);
        return mc;
    }


    private MessageEmbed errorMessage(){
        EmbedBuilder eb = new KuronekoEmbed();
        eb.setTitle("Invalid Image Link");
        eb.setDescription("Please provide a valid image link Senpai~~");
        return eb.build();
    }

}
