package com.kuroneko.interaction.rp.inititative;

import com.kuroneko.misc.KuronekoEmbed;
import com.kuroneko.misc.RNG;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class InitiativeCheck {

    private List<Participant> participants = new ArrayList<>();

    @Getter
    @Setter
    private InteractionHook originalMessage;

    public Participant addParticipant(String name, int modifier, RNG rng) {
        Participant participant = new Participant(name, modifier, rng);
        participants.add(participant);
        return participant;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Participant p : participants) {
            sb.append(p.toString()).append("\n");
        }
        return sb.toString();
    }

    public MessageEmbed buildEmbed() {
        KuronekoEmbed kuronekoEmbed = new KuronekoEmbed();
        kuronekoEmbed.setTitle("Initiative Check");
        kuronekoEmbed.setThumbnail(null);

        StringBuilder sb = new StringBuilder();
        AtomicInteger no = new AtomicInteger(1);
        participants.stream()
                .sorted(Comparator.comparing(Participant::getName))
                .forEach(p -> {
                    sb.append(no.getAndIncrement());
                    sb.append(". ");
                    sb.append(p.getName());
                    sb.append("\n");
                });
        kuronekoEmbed.setDescription(sb.toString());
        kuronekoEmbed.setFooter("All new messages in this channel will be deleted until the check is completed.\nThis check will automatically end after " + InitiativeCheckManager.INITIATIVE_TIMEOUT + " minutes.");
        return kuronekoEmbed.build();
    }
}