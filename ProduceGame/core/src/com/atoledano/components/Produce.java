package com.atoledano.components;

import com.artemis.Component;

public class Produce extends Component {

    public enum State {
        NORMAL,
        MOVING_UP,
        MOVING_DOWN,
        MOVING_LEFT,
        MOVING_RIGHT,
        EXPLODING
    }

    public boolean isDestroyed;
    public Produce.State state;
    public final Type type;
    public int power;
    public final float speed;

    public Produce() {
        this.type = Type.getRandomType();
        this.isDestroyed = false;
        this.speed = 6.0f;
        state = Produce.State.NORMAL;
    }

    public Produce(Type type) {
        this.isDestroyed = false;
        this.type = type;
        this.speed = 6.0f;
        state = Produce.State.NORMAL;
    }

    public void setMove(Produce.State state) {
        this.state = state;
    }
}
