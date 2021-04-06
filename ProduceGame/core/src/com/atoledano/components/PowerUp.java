package com.atoledano.components;

import com.artemis.Component;

public class PowerUp extends Component {

    public Type type;

    public boolean isDestroyed;

    public PowerUp() {
        this(false);
    }

    public PowerUp(boolean isDestroyed) {
        this.type = Type.getRandomType();
        this.isDestroyed = isDestroyed;
    }
}
