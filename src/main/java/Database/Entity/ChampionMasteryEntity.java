package Database.Entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@EqualsAndHashCode
@ToString
@Table(name = "champion_mastery",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"puuid", "champion"})})
public class ChampionMasteryEntity {

    @EmbeddedId
    private Pk id;

    @Column
    private int points;

    @Column
    private int level;

    @Column
    private int tokens;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "puuid", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private SummonerEntity summoner;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pk implements Serializable {
        @Column(name = "puuid", nullable = false, updatable = false, length = 80)
        private String objectId;

        @Column(name = "champion", nullable = false, updatable = false, length = 20)
        private String champion;
    }
}
