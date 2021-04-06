package com.atoledano.components;

import com.artemis.Component;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Player extends Component {

    public enum State {
        IDLING_UP,
        IDLING_LEFT,
        IDLING_DOWN,
        IDLING_RIGHT,
        WALKING_UP,
        WALKING_LEFT,
        WALKING_DOWN,
        WALKING_RIGHT
    }

    public State state;

    public static final int MAX_PRODUCE_CAPACITY = 6;
    public static final int MAX_BOMB_CAPACITY = 10;
    public static final int MAX_BOMB_POWER = 6;

    public float maxSpeed;
    public float acceleration;
    public int bombPower;
    public int produceCapacity;
    public int bombCapacity;
    public int produceLeft;
    public int bombLeft;
    public boolean kickBomb;
    public boolean remoteBomb;
    public Array<Type> types;

    public Player(boolean resetPlayerAbilities) {
        state = State.IDLING_DOWN;

        if (resetPlayerAbilities) {
            GameManager.resetPlayerAbilities();
        }

        maxSpeed = 3.0f + GameManager.playerMaxSpeed * 1.2f;
        bombPower = 1 + GameManager.playerBombPower;
        produceCapacity = GameManager.playerProduceCapacity;
        bombCapacity = GameManager.playerBombCapacity;
        remoteBomb = GameManager.playerRemoteBomb;
        kickBomb = GameManager.playerKickBomb;

        acceleration = 1.0f;
        produceLeft = 0;
        bombLeft = 0;

        types = new Array<Type>();
    }
}
