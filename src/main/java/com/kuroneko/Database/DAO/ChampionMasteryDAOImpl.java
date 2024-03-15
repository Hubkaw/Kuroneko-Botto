package com.kuroneko.Database.DAO;

import com.kuroneko.Database.Entity.ChampionMasteryEntity;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class ChampionMasteryDAOImpl extends KuronekoDAO<ChampionMasteryEntity> implements ChampionMasteryDAO {
    @Override
    public List<ChampionMasteryEntity> getAllBySummoner(String nickname) {
        Session session = createSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ChampionMasteryEntity> query = cb.createQuery(ChampionMasteryEntity.class);
            CriteriaQuery<ChampionMasteryEntity> cr =
                    query.where(cb.like(query.from(ChampionMasteryEntity.class).get("summoner_name"), nickname));
            return session.createQuery(cr).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public void saveOrUpdate(ChampionMasteryEntity championMasteryEntity) {
        super.saveOrUpdate(championMasteryEntity);
    }

    @Override
    public void saveOrUpdate(List<ChampionMasteryEntity> championMasteryEntities) {
        Session session = createSession();
        if (session == null)
            return;

        session.beginTransaction();
        championMasteryEntities.forEach(session::saveOrUpdate);
        session.flush();
        session.close();
    }
}
