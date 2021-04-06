package com.atoledano.components;

import com.artemis.Component;
import com.atoledano.gamesys.GameManager;

public class Player extends Component {

    public enum State {
        IDLING_UP,
        IDLING_LEFT,
        IDLING_DOWN,
        IDLING_RIGHT,
        WALKING_UP,
        WALKING_LEFT,
        WALKING_DOWN,
        WALKING_RIGHT,
        TELEPORTING,
        DYING
    }

    public State state;

    public static short defaultMaskBits = GameManager.TABLE_BIT | GameManager.ENEMY_BIT | GameManager.BOMB_BIT | GameManager.PRODUCECRATE_BIT;

    public static final int MAX_BOMB_CAPACITY = 10;
    public static final int MAX_BOMB_POWER = 6;

    public float maxSpeed;
    public float acceleration;
    public int hp;
    public int bombPower;
    public int bombCapacity;
    public int bombLeft;
    public boolean kickBomb;

    public Player(boolean resetPlayerAbilities) {
        state = State.IDLING_DOWN;

        if (resetPlayerAbilities) {
            GameManager.resetPlayerAbilities();
        }

        maxSpeed = 3.0f + GameManager.playerMaxSpeed * 1.2f;
        bombPower = 1 + GameManager.playerBombPower;
        bombCapacity = GameManager.playerBombCapacity;
        kickBomb = GameManager.playerKickBomb;

        hp = 1;
        acceleration = 1.0f;
        bombLeft = 5;
    }
}
