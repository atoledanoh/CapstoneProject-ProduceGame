package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.atoledano.builders.ActorBuilder;
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

        switch (door.state) {
            case EXPLODING:
                state.setCurrentState("exploding");
                if (state.getStateTime() > 0.6f) {
                    body.getWorld().destroyBody(body);
                    world.delete(entityId);

                    // chance to create PowerUp item
                    if (Math.random() < 0.2) {
                        ActorBuilder actorBuilder = ActorBuilder.init(body.getWorld(), world);
                        actorBuilder.createPowerUp(body.getPosition().x, body.getPosition().y);
                    }
                }
                break;
            case NORMAL:
            default:
                state.setCurrentState("normal");
                break;
        }
    }

}