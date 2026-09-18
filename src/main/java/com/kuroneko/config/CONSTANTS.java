package com.kuroneko.config;

import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;

import java.util.List;

import static no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType.*;

public class CONSTANTS {
    public static final int LOWEST_DICE_VALUE = 2;
    public static final int HIGHEST_DICE_VALUE = 10000;
    public static final int LOWEST_DICE_AMOUNT = 1;
    public static final int HIGHEST_DICE_AMOUNT = 100;

    public static final int MASTERY_POINTS_BIG_MESSAGE_INTERVAL = 1000000;
    public static final int MASTERY_POINTS_MESSAGE_INTERVAL = 100000;
    public static final int MASTERY_LEVEL_MESSAGE_INTERVAL = 10;

    public static final int LOG_ARGUMENT_CHUNK_SIZE = 255;

    public static final List<GameQueueType> RELEVANT_QUEUES = List.of(RANKED_FLEX_SR, RANKED_SOLO_5X5, RANKED_PREMADE_5X5);
    public static final List<GameQueueType> RELEVANT_QUEUES_MATCH_HISTORY = List.of(RANKED_FLEX_SR, TEAM_BUILDER_RANKED_SOLO);

    public static final int API_FETCH_MATCHES_CRON = 20;
    public static final int API_FETCH_MATCHES_NEW_SUMMONER = 20;
    public static final int DB_FETCH_MATCHES = 25;

    public static final int FIRST_STREAK_MILESTONE = 5;
    public static final int NEXT_STREAK_MILESTONE = 3;
}
