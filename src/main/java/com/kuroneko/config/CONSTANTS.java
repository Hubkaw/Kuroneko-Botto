package com.kuroneko.config;

import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;

import java.util.List;

public class CONSTANTS {
    public static final int LOWEST_DICE_VALUE = 2;
    public static final int HIGHEST_DICE_VALUE = 10000;
    public static final int LOWEST_DICE_AMOUNT = 1;
    public static final int HIGHEST_DICE_AMOUNT = 100;

    public static final int MASTERY_POINTS_BIG_MESSAGE_INTERVAL = 1000000;
    public static final int MASTERY_POINTS_MESSAGE_INTERVAL = 100000;
    public static final int MASTERY_LEVEL_MESSAGE_INTERVAL = 10;

    public static final int LOG_ARGUMENT_CHUNK_SIZE = 255;

    public static final List<GameQueueType> RELEVANT_QUEUES = List.of(GameQueueType.RANKED_FLEX_SR, GameQueueType.RANKED_SOLO_5X5, GameQueueType.TEAM_BUILDER_RANKED_SOLO);

    public static final int API_FETCH_MATCHES_CRON = 10;
    public static final int API_FETCH_MATCHES_NEW_SUMMONER = 25;
    public static final int FIND_MATCHES_COUNT = 50;
}
