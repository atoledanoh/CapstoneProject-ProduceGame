package com.atoledano.components;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Type {
    CHERRY, ORANGE, TURNIP1, APPLE;

//    PEPPER1, PEACH, CARROT, CUCUMBER,
//    STRAWBERRY, BROCCOLI, PINEAPPLE, WATERMELON, TURNIP2, POTATO, BANANA, LETTUCE,
//    TOMATO1, CHILI, CORN, VEGGIE1, EGGPLANT, CELERY, PEPPER2, MUSHROOM1,
//    ONION1, GRAPES1, TOMATO2, CAULIFLOWER, MUSHROOM2, LEMON, GRAPES2, MUSHROOM3,
//    SQUASH, VEGGIE2;

    private static final List<Type> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Type getRandomType() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
