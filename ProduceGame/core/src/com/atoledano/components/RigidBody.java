package com.atoledano.components;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;

public class RigidBody extends Component {
    public final Body body;

    public RigidBody(Body body) {
        this.body = body;
    }
}
