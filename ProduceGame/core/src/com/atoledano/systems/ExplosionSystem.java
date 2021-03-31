package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.atoledano.components.Explosion;
import com.atoledano.components.RigidBody;
import com.atoledano.components.State;

public class ExplosionSystem extends IteratingSystem {

    protected ComponentMapper<Explosion> mExplosion;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<State> mState;

    public ExplosionSystem() {
        super(Aspect.all(Explosion.class, State.class));
    }

    @Override
    protected void process(int entityId) {
//        Explosion explosion = mExplosion.get(entityId);
        RigidBody rigidBody = mRigidBody.get(entityId);
        State state = mState.get(entityId);
        if (state.getStateTime() > 0.75f) {
            rigidBody.body.getWorld().destroyBody(rigidBody.body);
            world.delete(entityId);
        }
    }

}
