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


    public static final int MAX_PRODUCE_CAPACITY = 10;
    public static final int MAX_PRODUCE_POWER = 6;

    public float maxSpeed;
    public float acceleration;
    public int hp;
    public int producePower;
    public int produceCapacity;
    public int produceLeft;
    public boolean kickProduce;
    public boolean remoteProduce;

    public Player(boolean resetPlayerAbilities) {
        state = State.IDLING_DOWN;

        if (resetPlayerAbilities) {
            GameManager.resetPlayerAbilities();
        }

        maxSpeed = 3.0f + GameManager.playerMaxSpeed * 1.2f;
        producePower = 1 + GameManager.playerProducePower;
        produceCapacity = GameManager.playerProduceCapacity;
        kickProduce = GameManager.playerKickProduce;
        remoteProduce = GameManager.playerRemoteProduce;

        hp = 1;
        acceleration = 1.0f;
        produceLeft = 5;
    }
}
