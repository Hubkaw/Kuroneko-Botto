package com.kuroneko.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@IdClass(MemberChannelEntity.Pk.class)
@Table
public class MemberChannelEntity {

    @Id
    private Long memberId;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private ChannelEntity channel;

    private String rollImageLink;


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Pk implements Serializable {
        Long memberId;
        String channel;
    }
}