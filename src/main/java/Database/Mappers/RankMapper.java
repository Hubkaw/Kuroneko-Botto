package Database.Mappers;

import Database.Entity.RankEntity;
import com.merakianalytics.orianna.types.core.league.LeagueEntry;

public class RankMapper {
    public static RankEntity map(LeagueEntry pos){
            RankEntity rankEntity = new RankEntity();
            rankEntity.setDivision(pos.getDivision());
            rankEntity.setName(pos.getLeague().getName());
            rankEntity.setTier(pos.getTier());
            rankEntity.setId(new RankEntity.Pk(pos.getSummoner().getPuuid(), pos.getQueue()));
            rankEntity.setLeaguePoints(pos.getLeaguePoints());
            rankEntity.setWins(pos.getWins());
            rankEntity.setLosses(pos.getLosses());
            return rankEntity;
    }
}
