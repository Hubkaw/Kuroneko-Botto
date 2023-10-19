package Database.Mappers;

import Database.Entity.RankEntity;
import Database.Entity.SummonerEntity;
import com.merakianalytics.orianna.types.core.summoner.Summoner;

public class SummonerMapper {
    public static SummonerEntity map(Summoner apiSummoner) {
        SummonerEntity summoner = new SummonerEntity();
        summoner.setPuuid(apiSummoner.getPuuid());
        summoner.setName(apiSummoner.getName());
        summoner.setAccountId(apiSummoner.getAccountId());
        summoner.setId(apiSummoner.getId());
        summoner.setLevel(apiSummoner.getLevel());

        return summoner;
    }
}
