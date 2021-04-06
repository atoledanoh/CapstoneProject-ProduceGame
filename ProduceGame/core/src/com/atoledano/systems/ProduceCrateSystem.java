package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.atoledano.components.ProduceCrate;
import com.atoledano.components.Renderer;
import com.atoledano.components.RigidBody;
import com.atoledano.components.State;

public class ProduceCrateSystem extends IteratingSystem {

    protected ComponentMapper<ProduceCrate> mProduceCrate;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<Renderer> mRenderer;
    protected ComponentMapper<State> mState;

    public ProduceCrateSystem() {
        super(Aspect.all(ProduceCrate.class, RigidBody.class, Renderer.class, State.class));
    }

    @Override
    protected void process(int entityId) {
        ProduceCrate produceCrate = mProduceCrate.get(entityId);
        RigidBody rigidBody = mRigidBody.get(entityId);
        Renderer renderer = mRenderer.get(entityId);
        State state = mState.get(entityId);
    }

}
