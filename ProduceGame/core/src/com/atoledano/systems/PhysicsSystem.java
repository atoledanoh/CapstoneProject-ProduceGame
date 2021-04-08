package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.atoledano.components.Customer;
import com.atoledano.components.RigidBody;
import com.atoledano.components.Transform;

public class PhysicsSystem extends IteratingSystem {

    protected ComponentMapper<Transform> transformComponentMapper;
    protected ComponentMapper<RigidBody> rigidBodyComponentMapper;
    protected ComponentMapper<Customer> customerComponentMapper;

    public PhysicsSystem() {
        super(Aspect.all(Transform.class, RigidBody.class));
    }

    @Override
    protected void process(int entityId) {
        Transform transform = transformComponentMapper.get(entityId);
        RigidBody rigidBody = rigidBodyComponentMapper.get(entityId);

        transform.setPosition(rigidBody.body.getPosition());
    }
}
