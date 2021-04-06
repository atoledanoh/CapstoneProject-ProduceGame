package com.atoledano.components;

import com.artemis.Component;

public class Door extends Component {
    public enum State {
        NORMAL,
        EXPLODING
    }

    public State state;

    public Door() {
        state = State.NORMAL;
    }
}
