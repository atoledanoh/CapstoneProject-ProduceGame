package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.atoledano.builders.ActorBuilder;
import com.atoledano.components.Bomb;
import com.atoledano.components.RigidBody;
import com.atoledano.components.State;
import com.atoledano.components.Transform;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class ProduceSystem extends IteratingSystem {

    protected ComponentMapper<Bomb> mBomb;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<State> mState;

    private boolean moveable;

    private final Vector2 fromV;
    private final Vector2 toV;

    public ProduceSystem() {
        super(Aspect.all(Bomb.class, RigidBody.class, Transform.class, State.class));

        fromV = new Vector2();
        toV = new Vector2();
    }

    @Override
    protected void process(int entityId) {
        Bomb bomb = mBomb.get(entityId);
        State state = mState.get(entityId);
        RigidBody rigidBody = mRigidBody.get(entityId);

        Body body = rigidBody.body;

        bomb.countDown -= world.getDelta();

        if (bomb.countDown <= 0) {
            // explode
            bomb.state = Bomb.State.EXPLODING;
        }

        switch (bomb.state) {
            case EXPLODING:
                // destroy itself
                World b2dWorld = body.getWorld();
                b2dWorld.destroyBody(body);
                world.delete(entityId);
                break;
            case MOVING_UP:
                if (checkMovable(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x, body.getPosition().y + 0.55f))) {
                    body.setLinearVelocity(0, bomb.speed);
                } else {
                    body.setLinearVelocity(0, 0);
                    body.setTransform(MathUtils.floor(body.getPosition().x) + 0.5f, MathUtils.floor(body.getPosition().y) + 0.5f, 0);
                    bomb.state = Bomb.State.NORMAL;
                }
                break;
            case MOVING_DOWN:
                if (checkMovable(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x, body.getPosition().y - 0.55f))) {
                    body.setLinearVelocity(0, -bomb.speed);
                } else {
                    body.setLinearVelocity(0, 0);
                    body.setTransform(MathUtils.floor(body.getPosition().x) + 0.5f, MathUtils.floor(body.getPosition().y) + 0.5f, 0);
                    bomb.state = Bomb.State.NORMAL;
                }
                break;
            case MOVING_LEFT:
                if (checkMovable(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x - 0.55f, body.getPosition().y))) {
                    body.setLinearVelocity(-bomb.speed, 0);
                } else {
                    body.setLinearVelocity(0, 0);
                    body.setTransform(MathUtils.floor(body.getPosition().x) + 0.5f, MathUtils.floor(body.getPosition().y) + 0.5f, 0);
                    bomb.state = Bomb.State.NORMAL;
                }
                break;

            case MOVING_RIGHT:
                if (checkMovable(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x + 0.55f, body.getPosition().y))) {
                    body.setLinearVelocity(bomb.speed, 0);
                } else {
                    body.setLinearVelocity(0, 0);
                    body.setTransform(MathUtils.floor(body.getPosition().x) + 0.5f, MathUtils.floor(body.getPosition().y) + 0.5f, 0);
                    bomb.state = Bomb.State.NORMAL;
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
        moveable = true;

        RayCastCallback rayCastCallback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getFilterData().categoryBits == GameManager.TABLE_BIT
                        | fixture.getFilterData().categoryBits == GameManager.DOOR_BIT
                        | fixture.getFilterData().categoryBits == GameManager.BOMB_BIT
                        | fixture.getFilterData().categoryBits == GameManager.ENEMY_BIT
                        | fixture.getFilterData().categoryBits == GameManager.POWERUP_BIT
                        | fixture.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                    moveable = false;
                    return 0;
                }
                return 0;
            }
        };

        b2dWorld.rayCast(rayCastCallback, from, to);
        return moveable;
    }
}
