package Database.DAO;

import Database.Entity.ChannelEntity;

import java.util.List;

public interface ChannelDAO {

    ChannelEntity getById(String id);
    List<ChannelEntity> getAll();
    void saveChannel(ChannelEntity channelEntity);

    void updateChannel(ChannelEntity channelEntity);

    void saveOrUpdate(ChannelEntity channelEntity);

    void delete(ChannelEntity channelEntity);
}
