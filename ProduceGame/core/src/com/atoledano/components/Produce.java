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

    public float countDown;
    public State state;
    public Type type;
    public int power;
    public float speed;

    public Produce() {
        this(1, 2.0f);
    }

    public Produce(int power) {
        this(power, 2.0f);
    }

    public Produce(int power, float countDown) {
        this.power = power;
        this.countDown = countDown;
        this.speed = 6.0f;
        state = State.NORMAL;
        type = Type.getRandomType();
    }

    public void setMove(Produce.State state) {
        this.state = state;
    }
}
