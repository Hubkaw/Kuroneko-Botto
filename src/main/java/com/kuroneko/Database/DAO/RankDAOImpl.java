package com.kuroneko.Database.DAO;

import com.kuroneko.Database.Entity.RankEntity;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class RankDAOImpl extends KuronekoDAO<RankEntity> implements RankDAO{

    @Override
    public List<RankEntity> getAllBySummoner(String summonerName) {
        Session session = createSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<RankEntity> query = cb.createQuery(RankEntity.class);
            CriteriaQuery<RankEntity> cr =
                    query.where(cb.like(query.from(RankEntity.class).get("summoner_name"), summonerName));
            return session.createQuery(cr).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public void saveOrUpdate(RankEntity rank) {
        super.saveOrUpdate(rank);
    }

}
