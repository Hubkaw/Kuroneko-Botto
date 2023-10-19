package Database.DAO;

import Database.Entity.SummonerEntity;

import java.util.List;

public interface SummonerDAO {
    SummonerEntity getSummonerByName(String name);
    void saveSummoner(SummonerEntity summoner);
    List<SummonerEntity> getSummoners();
    void saveOrUpdate(SummonerEntity summoner);

    void delete(SummonerEntity summonerEntity);
}
