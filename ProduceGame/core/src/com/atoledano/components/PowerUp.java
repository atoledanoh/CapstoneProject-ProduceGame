package com.atoledano.components;

import com.artemis.Component;

public class PowerUp extends Component {

    public enum State {
        NORMAL,
        MOVING_UP,
        MOVING_DOWN,
        MOVING_LEFT,
        MOVING_RIGHT,
        EXPLODING
    }

    public boolean isDestroyed;
    public PowerUp.State state;
    public Type type;
    public int power;
    public float speed;

    public PowerUp() {
        this.type = Type.getRandomType();
        this.isDestroyed = false;
        this.speed = 6.0f;
        state = PowerUp.State.NORMAL;
    }

    public PowerUp(Type type) {
        this.isDestroyed = false;
        this.type = type;
        this.speed = 6.0f;
        state = PowerUp.State.NORMAL;
    }

    public void setMove(PowerUp.State state) {
        this.state = state;
    }
}
