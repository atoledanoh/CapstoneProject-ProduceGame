package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.atoledano.components.Door;
import com.atoledano.components.RigidBody;
import com.atoledano.components.State;
import com.badlogic.gdx.physics.box2d.Body;

public class DoorSystem extends IteratingSystem {

    protected ComponentMapper<Door> mDoor;
    protected ComponentMapper<State> mState;
    protected ComponentMapper<RigidBody> mRigidBody;

    public DoorSystem() {
        super(Aspect.all(Door.class, State.class));
    }

    @Override
    protected void process(int entityId) {
        Door door = mDoor.get(entityId);
        State state = mState.get(entityId);
        RigidBody rigidBody = mRigidBody.get(entityId);
        Body body = rigidBody.body;
    }
}
