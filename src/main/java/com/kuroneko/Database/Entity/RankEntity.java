package com.kuroneko.Database.Entity;

import com.merakianalytics.orianna.types.common.Division;
import com.merakianalytics.orianna.types.common.Queue;
import com.merakianalytics.orianna.types.common.Tier;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@EqualsAndHashCode
@ToString
@Table(name = "rank",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"puuid", "queue"})})
public class RankEntity {

    @EmbeddedId
    private Pk id;

    @Column
    private Tier tier;

    @Column
    private Division division;

    @Column
    private String name;

    @Column
    private int leaguePoints;

    @Column
    private int wins;

    @Column
    private int losses;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "puuid", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private SummonerEntity summoner;


    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Pk implements Serializable {
        @Column(name = "puuid", nullable = false, updatable = false, length = 80)
        private String objectId;

        @Column(name = "queue", nullable = false, updatable = false)
        private Queue queue;
    }
}
