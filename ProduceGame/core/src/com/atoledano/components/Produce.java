package com.atoledano.components;

import com.artemis.Component;

public class Produce extends Component {

    public enum State {
        NORMAL,
        MOVING_UP,
        MOVING_DOWN,
        MOVING_LEFT,
        MOVING_RIGHT
    }

    public State state;
    public Type type;
    public float speed;
    public boolean isDestroyed = false;

    public Produce() {
        this.speed = 6.0f;
        state = State.NORMAL;
        type = Type.getRandomType();
    }

    public void setMove(Produce.State state) {
        this.state = state;
    }
}
