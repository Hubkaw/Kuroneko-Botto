package com.kuroneko.interaction.league;

import com.kuroneko.database.entity.ChannelEntity;
import com.kuroneko.database.entity.SummonerEntity;
import com.kuroneko.database.mapper.ChannelMapper;
import com.kuroneko.database.repository.ChannelRepository;
import com.kuroneko.interaction.SlashInteraction;
import com.kuroneko.misc.KuronekoEmbed;
import com.kuroneko.service.ChampionMasteryService;
import com.kuroneko.service.DDragonService;
import com.kuroneko.service.RankService;
import com.kuroneko.service.SummonerService;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.basic.constants.api.regions.RegionShard;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;
import no.stelar7.api.r4j.pojo.shared.RiotAccount;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Component
public class RegisterSummonerInteraction implements SlashInteraction {

    private static final Pattern RIOT_ID_PATTERN = Pattern.compile("([0-9a-zA-Z ]{3,20})#([0-9a-zA-Z ]{3,5})");

    private R4J riotApi;
    private SummonerService summonerService;
    private ChampionMasteryService cmService;
    private RankService rankService;
    private ChannelRepository channelRepository;
    private DDragonService dDragonService;

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        InteractionHook deferred = event.deferReply().setEphemeral(false).complete();
        if (event.getMember() == null ||
                (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)
                        && !event.getMember().getPermissions(event.getGuildChannel()).contains(Permission.MANAGE_CHANNEL))) {
            MessageEmbed embed = new KuronekoEmbed().setTitle("No Sufficient Permission").setDescription("To use this command you need Administrator or Manage Channel permissions senpai.").build();
            deferred.sendMessageEmbeds(embed).setEphemeral(true).queue();
            return;
        }
        String name = event.getOption("name").getAsString();
        Matcher matcher = RIOT_ID_PATTERN.matcher(name);
        if (!matcher.matches()) {
            MessageEmbed invalidRiotId = new KuronekoEmbed().setTitle("Invalid Riot ID").setDescription("You have to provide a valid Riot ID").build();
            Message complete = deferred.sendMessageEmbeds(invalidRiotId).setEphemeral(false).complete();
            complete.delete().queueAfter(15, TimeUnit.SECONDS);
            return;
        }
        LeagueShard region;
        if (event.getOption("region") != null) {
            LeagueShard providedRegion = LeagueShard.fromString(event.getOption("region").getAsString()).orElse(null);
            region = providedRegion == null ? LeagueShard.EUN1 : providedRegion;
        } else {
            region = LeagueShard.EUN1;
        }

        RiotAccount account = riotApi.getAccountAPI().getAccountByTag(RegionShard.EUROPE, matcher.group(1), matcher.group(2));
        if (account == null) {
            MessageEmbed invalidRiotId = new KuronekoEmbed().setTitle("Invalid Riot ID").setDescription("There is no %s on %s".formatted(name, region.prettyName())).build();
            Message complete = deferred.sendMessageEmbeds(invalidRiotId).setEphemeral(false).complete();
            complete.delete().queueAfter(15, TimeUnit.SECONDS);
            return;
        }
        SummonerEntity summonerEntity = summonerService.getSummonerById(account.getPUUID());
        if (summonerEntity == null) {
            Summoner summoner = riotApi.getLoLAPI().getSummonerAPI().getSummonerByPUUID(region, account.getPUUID());
            String riotID = account.getName() + "#" + account.getTag();
            summonerEntity = createSummonerEntity(summoner, riotID);
        }

        ChannelEntity channelEntity = channelRepository.findById(event.getChannel().getId()).orElse(null);
        if (channelEntity == null) {
            channelEntity = channelRepository.save(ChannelMapper.map(event.getGuildChannel()));
        }

        if (channelEntity.getSummoners().contains(summonerEntity)) {
            String icon = dDragonService.getIconLink(summonerEntity.getIconId());
            MessageEmbed invalidRiotId = new KuronekoEmbed()
                    .setTitle("Already Tracking")
                    .setDescription("I am already tracking %s's progress on this channel. Stop bothering me.".formatted(name))
                    .setThumbnail(icon)
                    .build();
            Message complete = deferred.sendMessageEmbeds(invalidRiotId).setEphemeral(false).complete();
            complete.delete().queueAfter(15, TimeUnit.SECONDS);
            return;
        }

        channelEntity.getSummoners().add(summonerEntity);
        ChannelEntity flush = channelRepository.saveAndFlush(channelEntity);
        summonerEntity.getChannels().add(flush);
        SummonerEntity saved = summonerService.save(summonerEntity);

        String icon = dDragonService.getIconLink(summonerEntity.getIconId());

        MessageEmbed embed = new KuronekoEmbed().setTitle("Summoner Registered")
                .setDescription("I will now track progress of %s on this channel".formatted(saved.getRiotId()))
                .setThumbnail(icon)
                .build();
        deferred.sendMessageEmbeds(embed).setEphemeral(false).queue();
    }

    @Override
    public String getName() {
        return "register-lol";
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(getName(), "Registers Summoner to this channel")
                .addOption(OptionType.STRING, "name", "Summoner name", true, false)
                .addOption(OptionType.STRING, "region", "Region (default EUNE)", false, false);
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {

    }

    private SummonerEntity createSummonerEntity(Summoner summoner, String name) {
        summonerService.saveNewSummoner(summoner, name);
        cmService.createChampionMasteryForKnownSummoner(summoner);
        return rankService.createRanksForKnownSummoner(summoner);
    }

}
