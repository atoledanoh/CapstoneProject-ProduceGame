package com.atoledano.components;

public enum Type {
    AMMO,   // 0
    POWER,  // 1
    SPEED,  // 2
    KICK,   // 3
    REMOTE, // 4
    ONE_UP, // 5
    APPLE, // 6
    ORANGE; // 7

    public static Type getRandomType() {
        int index;
        int random = (int) (Math.random() * 2);

        if (random < 1) {
            index = 6; // APPLE
        } else {
            index = 7; // ORANGE
        }

        return values()[index];
    }
}
