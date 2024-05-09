package com.kuroneko.database.mapper;

import com.kuroneko.database.entity.ChannelEntity;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;

public class ChannelMapper {
    public static ChannelEntity map(GuildMessageChannelUnion channel) {
        ChannelEntity newChannelEntity = new ChannelEntity();
        newChannelEntity.setChannelId(channel.getId());
        newChannelEntity.setGuildId(channel.getGuild().getIdLong());
        newChannelEntity.setChannelName(channel.getName());
        return newChannelEntity;
    }
}
