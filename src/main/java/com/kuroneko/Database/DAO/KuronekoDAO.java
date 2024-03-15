package com.kuroneko.Database.DAO;

import com.kuroneko.Database.Entity.SummonerEntity;
import com.kuroneko.Config.KuronekoSessionFactory;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;

public abstract class KuronekoDAO<T> {

    List<T> getAll(Class<T> t) {
        Session session = createSession();
        if (session == null)
            return null;
        CriteriaQuery<T> query = session.getCriteriaBuilder().createQuery(t);
        query.from(SummonerEntity.class);
        List<T> list = session.createQuery(query).getResultList();
        session.close();
        return list;
    }

    T save(T t) {
        Session session = createSession();
        if (session == null)
            return null;
        session.beginTransaction();
        Serializable saved = session.save(t);
        session.flush();
        session.close();
        return (T) saved;
    }

    void update(T t) {
        Session session = createSession();
        if (session == null)
            return;
        session.beginTransaction();

        session.update(t);
        session.flush();
        session.close();
    }

    void saveOrUpdate(T t) {
        Session session = createSession();
        if (session == null)
            return;
        session.beginTransaction();

        session.saveOrUpdate(t);
        session.flush();
        session.close();
    }

    T getById(Class<T> t, Serializable id){
        Session session = createSession();
        if (session == null)
            return null;
        T result = session.get(t, id);
        session.close();
        return result;
    }

    void delete(T t) {
        Session session = createSession();
        if (session == null)
            return;
        session.beginTransaction();
        session.delete(t);
        session.flush();
        session.close();
    }

    Session createSession() {
        try {
            return KuronekoSessionFactory.getSessionFactory().openSession();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
