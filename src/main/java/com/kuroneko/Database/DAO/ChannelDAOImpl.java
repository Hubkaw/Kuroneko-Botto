package com.kuroneko.Database.DAO;

import com.kuroneko.Database.Entity.ChannelEntity;
import org.hibernate.Session;

import java.util.List;

public class ChannelDAOImpl extends KuronekoDAO<ChannelEntity> implements ChannelDAO{
    @Override
    public ChannelEntity getById(String id) {
        Session session = createSession();
        if (session == null)
            return null;
        ChannelEntity channelEntity = session.get(ChannelEntity.class, id);
        session.close();
        return channelEntity;
    }

    @Override
    public List<ChannelEntity> getAll() {
        return super.getAll(ChannelEntity.class);
    }

    @Override
    public void saveChannel(ChannelEntity channelEntity) {
        super.save(channelEntity);
    }

    @Override
    public void updateChannel(ChannelEntity channelEntity) {
        super.update(channelEntity);
    }

    @Override
    public void saveOrUpdate(ChannelEntity channelEntity) {
        super.saveOrUpdate(channelEntity);
    }

    @Override
    public void delete(ChannelEntity channelEntity) {
        super.delete(channelEntity);
    }
}
