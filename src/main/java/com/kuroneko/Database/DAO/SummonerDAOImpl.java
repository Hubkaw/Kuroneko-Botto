package com.kuroneko.Database.DAO;

import com.kuroneko.Database.Entity.SummonerEntity;
import lombok.NoArgsConstructor;
import org.hibernate.LockMode;
import org.hibernate.Session;

import java.util.List;

@NoArgsConstructor
public class SummonerDAOImpl extends KuronekoDAO<SummonerEntity> implements SummonerDAO {
    @Override
    public SummonerEntity getSummonerByName(String name) {
        Session session = createSession();
        if (session == null)
            return null;
        SummonerEntity summonerEntity = session.get(SummonerEntity.class, name, LockMode.READ);
        session.close();
        return summonerEntity;
    }


    @Override
    public void saveSummoner(SummonerEntity summonerEntity) {
       super.save(summonerEntity);
    }

    @Override
    public List<SummonerEntity> getSummoners() {
        return getAll(SummonerEntity.class);
    }

    @Override
    public void saveOrUpdate(SummonerEntity summoner) {
        super.saveOrUpdate(summoner);
    }

    @Override
    public void delete( SummonerEntity summonerEntity) {
        super.delete(summonerEntity);
    }
}
