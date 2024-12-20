package com.kuroneko.interaction.rp.inititative;

import com.kuroneko.misc.KuronekoEmbed;
import com.kuroneko.misc.RNG;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InitiativeCheckManager {

    private final Map<String, InitiativeCheck> initiativeChecks;
    private final RNG rng;
    private final ScheduledExecutorService scheduler;
    public static final long INITIATIVE_TIMEOUT = 5;

    public InitiativeCheckManager(RNG rng) {
        this.initiativeChecks = new HashMap<>();
        this.rng = rng;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void addParticipant(String channelId, String name, int modifier) {
        addParticipant(channelId, new Participant(name, modifier, rng));
    }

    public void addParticipant(String channelId, Participant participant) {
        InitiativeCheck check = initiativeChecks.get(channelId);
        if (check == null) {
            initiativeChecks.put(channelId, new InitiativeCheck());
            check = initiativeChecks.get(channelId);
            scheduler.schedule(() -> {
                InteractionHook originalMessage = initiativeChecks.get(channelId).getOriginalMessage();
                originalMessage.editOriginalEmbeds(buildEmbed(channelId)).queue();
                originalMessage.editOriginalComponents().queue();
                endInitiative(channelId);
            }, INITIATIVE_TIMEOUT, TimeUnit.MINUTES);
        }
        check.getParticipants().add(participant);
    }

    public void addParticipants(String channelId, List<Participant> participants) {
        participants.forEach(p -> addParticipant(channelId, p));
    }

    public InitiativeCheck getInitiativeCheck(String channelId) {
        return initiativeChecks.get(channelId);
    }

    public void endInitiative(String channelId) {
        initiativeChecks.remove(channelId);
    }

    public MessageEmbed buildEmbed(String channelId) {

        InitiativeCheck initiativeCheck = initiativeChecks.get(channelId);

        if (initiativeCheck == null) {
            throw new IllegalArgumentException("Initiative check not found: " + channelId);
        }

        KuronekoEmbed kuronekoEmbed = new KuronekoEmbed();
        kuronekoEmbed.setTitle("Finished Initiative Check");
        kuronekoEmbed.setThumbnail(null);

        AtomicInteger no = new AtomicInteger(1);
        StringBuilder sb = new StringBuilder();
        initiativeCheck.getParticipants().stream()
                .sorted()
                .forEach(p -> {
                    sb.append(no.getAndIncrement());
                    sb.append(". ");
                    sb.append(p.toString());
                    sb.append("\n");
                });
        kuronekoEmbed.setDescription(sb.toString());
        kuronekoEmbed.setFooter("Faito!");
        return kuronekoEmbed.build();
    }
}
