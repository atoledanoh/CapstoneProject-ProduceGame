package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.atoledano.components.*;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class ProduceSystem extends IteratingSystem {

    protected ComponentMapper<Produce> produceComponentMapper;
    protected ComponentMapper<RigidBody> rigidBodyComponentMapper;
    protected ComponentMapper<State> stateComponentMapper;

    private boolean movable;

    private final Vector2 fromV;
    private final Vector2 toV;


    public ProduceSystem() {
        super(Aspect.all(Produce.class, RigidBody.class, Transform.class, State.class));

        fromV = new Vector2();
        toV = new Vector2();
    }

    @Override
    protected void process(int entityId) {
        Produce produce = produceComponentMapper.get(entityId);
        State state = stateComponentMapper.get(entityId);
        RigidBody rigidBody = rigidBodyComponentMapper.get(entityId);

        Body body = rigidBody.body;

        if (produce.isDestroyed) {
            // destroy
            rigidBody.body.getWorld().destroyBody(rigidBody.body);
            world.delete(entityId);
        }

        switch (produce.state) {
            case EXPLODING:
                // destroy itself
                World b2dWorld = body.getWorld();
                b2dWorld.destroyBody(body);
                world.delete(entityId);
                break;
            case MOVING_UP:
                if (checkMovable(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x, body.getPosition().y + 0.55f))) {
                    body.setLinearVelocity(0, produce.speed);
                } else {
                    body.setLinearVelocity(0, 0);
                    body.setTransform(MathUtils.floor(body.getPosition().x) + 0.5f, MathUtils.floor(body.getPosition().y) + 0.5f, 0);
                    produce.state = Produce.State.NORMAL;
                }
                break;
            case MOVING_DOWN:
                if (checkMovable(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x, body.getPosition().y - 0.55f))) {
                    body.setLinearVelocity(0, -produce.speed);
                } else {
                    body.setLinearVelocity(0, 0);
                    body.setTransform(MathUtils.floor(body.getPosition().x) + 0.5f, MathUtils.floor(body.getPosition().y) + 0.5f, 0);
                    produce.state = Produce.State.NORMAL;
                }
                break;
            case MOVING_LEFT:
                if (checkMovable(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x - 0.55f, body.getPosition().y))) {
                    body.setLinearVelocity(-produce.speed, 0);
                } else {
                    body.setLinearVelocity(0, 0);
                    body.setTransform(MathUtils.floor(body.getPosition().x) + 0.5f, MathUtils.floor(body.getPosition().y) + 0.5f, 0);
                    produce.state = Produce.State.NORMAL;
                }
                break;

            case MOVING_RIGHT:
                if (checkMovable(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x + 0.55f, body.getPosition().y))) {
                    body.setLinearVelocity(produce.speed, 0);
                } else {
                    body.setLinearVelocity(0, 0);
                    body.setTransform(MathUtils.floor(body.getPosition().x) + 0.5f, MathUtils.floor(body.getPosition().y) + 0.5f, 0);
                    produce.state = Produce.State.NORMAL;
                }
                break;
            case NORMAL:
            default:
                state.setCurrentState("normal");
                break;
        }
    }

    private boolean checkMovable(Body body, Vector2 from, Vector2 to) {
        World b2dWorld = body.getWorld();
        movable = true;

        RayCastCallback rayCastCallback = (fixture, point, normal, fraction) -> {
            if (fixture.getFilterData().categoryBits == GameManager.TABLE_BIT
                    | fixture.getFilterData().categoryBits == GameManager.DOOR_BIT
                    | fixture.getFilterData().categoryBits == GameManager.PRODUCE_BIT
                    | fixture.getFilterData().categoryBits == GameManager.CUSTOMER_BIT
                    | fixture.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                movable = false;
                return 0;
            }
            return 0;
        };

        b2dWorld.rayCast(rayCastCallback, from, to);
        return movable;
    }

}