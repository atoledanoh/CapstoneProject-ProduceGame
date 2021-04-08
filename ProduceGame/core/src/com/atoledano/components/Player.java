package com.atoledano.components;

import com.artemis.Component;
import com.atoledano.gamesys.GameManager;
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

    public final float maxSpeed;
    public final float acceleration;
    public final int produceCapacity;
    public int produceLeft;
    public final boolean kickBomb;
    public final boolean remoteBomb;
    public final Array<Type> types;

    public Player(boolean resetPlayerAbilities) {
        state = State.IDLING_DOWN;

        if (resetPlayerAbilities) {
            GameManager.resetPlayerAbilities();
        }

        maxSpeed = 3.0f + GameManager.playerMaxSpeed * 1.2f;
        produceCapacity = GameManager.playerProduceCapacity;
        remoteBomb = GameManager.playerRemoveProduce;
        kickBomb = GameManager.playerKickProduce;

        acceleration = 1.0f;
        produceLeft = 0;

        types = new Array<>();
    }
}
