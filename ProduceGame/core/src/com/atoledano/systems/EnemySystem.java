package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.atoledano.builders.ActorBuilder;
import com.atoledano.components.Enemy;
import com.atoledano.components.RigidBody;
import com.atoledano.components.State;
import com.atoledano.components.Transform;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class EnemySystem extends IteratingSystem {

    protected ComponentMapper<Enemy> mEnemy;
    protected ComponentMapper<RigidBody> mRigidBody;
    protected ComponentMapper<State> mState;
    protected ComponentMapper<Transform> mTransform;

    private boolean hit;
    private final Vector2 fromVector;
    private final Vector2 toVector;

    private Enemy enemy;
    private RigidBody rigidBody;
    private State state;

    public EnemySystem() {
        super(Aspect.all(Enemy.class, Transform.class, RigidBody.class, State.class));
        fromVector = new Vector2();
        toVector = new Vector2();
    }

    protected boolean hitSomethingVertical(final Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        hit = false;

        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fraction < 1.0f) {
                    hit = true;
                }
                return 0;
            }
        };

        for (int i = 0; i < 3; i++) {
            Vector2 tmpV = new Vector2(toV);
            b2dWorld.rayCast(rayCastCallback, fromV, tmpV.add((1 - i) * 0.4f, 0));

        }
        return hit;
    }

    protected boolean hitSomethingHorizontal(final Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        hit = false;

        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fraction < 1.0f) {
                    hit = true;
                }
                return 0;
            }
        };

        for (int i = 0; i < 3; i++) {
            Vector2 tmpV = new Vector2(toV);
            b2dWorld.rayCast(rayCastCallback, fromV, tmpV.add(0, (1 - i) * 0.4f));

        }
        return hit;
    }

    protected void changeWalkingState(Enemy enemy) {
        enemy.state = Enemy.State.getRandomWalkingState();
    }

    @Override
    protected void process(int entityId) {
        enemy = mEnemy.get(entityId);
        rigidBody = mRigidBody.get(entityId);
        state = mState.get(entityId);

        switch (enemy.type) {
            case "boss1":
                break;
            case "bomb":
                break;
            default:
                handleBasics(entityId);
                break;
        }
    }

    private void handleBasics(int entityId) {
        Body body = rigidBody.body;

        if (enemy.receivedDamage > 0) {
            enemy.damage(enemy.receivedDamage);
            enemy.receivedDamage = 0;
        }

        if (enemy.hp <= 0) {
            enemy.state = Enemy.State.DYING;
            enemy.lifetime = 0;
        } else {
            enemy.lifetime += world.getDelta();
        }

        switch (enemy.state) {
            case DYING:
                state.setCurrentState("dying");
                Filter filter = body.getFixtureList().get(0).getFilterData();
                filter.maskBits = GameManager.NOTHING_BIT;
                body.getFixtureList().get(0).setFilterData(filter);
                ActorBuilder actorBuilder = ActorBuilder.init(body.getWorld(), world);
                while (GameManager.enemiesLeft < GameManager.totalEnemies) {
                    actorBuilder.createCustomer1(body.getPosition().x, body.getPosition().y);
                }

                if (state.getStateTime() <= 0) {
                    GameManager.getInstance().playSound(enemy.getDieSound(), 1.0f, MathUtils.random(0.8f, 1.2f), 0);
                }

                if (state.getStateTime() > 0.6f) {
                    // decrease enemy count
                    GameManager.enemiesLeft--;

                    body.getWorld().destroyBody(body);
                    mRigidBody.set(entityId, false);
                    mEnemy.set(entityId, false);
                    mState.set(entityId, false);
                    Transform transform = mTransform.get(entityId);
                    transform.z = 999;
                }
                break;
            case WALKING_LEFT:
                state.setCurrentState("walking_left");
                if (body.getLinearVelocity().x > -enemy.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(-enemy.getSpeed() * body.getMass(), 0), body.getWorldCenter(), true);
                }
                if (hitSomethingHorizontal(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x - 0.5f, body.getPosition().y))) {
                    changeWalkingState(enemy);
                }
                break;
            case WALKING_RIGHT:
                state.setCurrentState("walking_right");
                if (body.getLinearVelocity().x < enemy.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(enemy.getSpeed() * body.getMass(), 0), body.getWorldCenter(), true);
                }
                if (hitSomethingHorizontal(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x + 0.5f, body.getPosition().y))) {
                    changeWalkingState(enemy);
                }
                break;
            case WALKING_UP:
                state.setCurrentState("walking_up");
                if (body.getLinearVelocity().y < enemy.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(0, enemy.getSpeed() * body.getMass()), body.getWorldCenter(), true);
                }
                if (hitSomethingVertical(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x, body.getPosition().y + 0.5f))) {
                    changeWalkingState(enemy);
                }
                break;
            case WALKING_DOWN:
            default:
                state.setCurrentState("walking_down");
                if (body.getLinearVelocity().y > -enemy.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(0, -enemy.getSpeed() * body.getMass()), body.getWorldCenter(), true);
                }
                if (hitSomethingVertical(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x, body.getPosition().y - 0.5f))) {
                    changeWalkingState(enemy);
                }
                break;
        }
    }
}
