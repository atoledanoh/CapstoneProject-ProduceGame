package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.atoledano.builders.ActorBuilder;
import com.atoledano.components.*;
import com.atoledano.components.Transform;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class CustomerSystem extends IteratingSystem {

    protected ComponentMapper<Customer> customerComponentMapper;
    protected ComponentMapper<RigidBody> rigidBodyComponentMapper;
    protected ComponentMapper<State> stateComponentMapper;
    protected ComponentMapper<Transform> transformComponentMapper;

    private boolean hit;
    private final Vector2 fromVector;
    private final Vector2 toVector;

    private Customer customer;
    private RigidBody rigidBody;
    private State state;

    public CustomerSystem() {
        super(Aspect.all(Customer.class, Transform.class, RigidBody.class, State.class));
        fromVector = new Vector2();
        toVector = new Vector2();
    }

    protected boolean hitSomethingVertical(final Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        hit = false;

        RayCastCallback rayCastCallback = (fixture, point, normal, fraction) -> {
            if (fraction < 1.0f) {
                hit = true;
            }
            return 0;
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

        RayCastCallback rayCastCallback = (fixture, point, normal, fraction) -> {
            if (fraction < 1.0f) {
                hit = true;
            }
            return 0;
        };

        for (int i = 0; i < 3; i++) {
            Vector2 tmpV = new Vector2(toV);
            b2dWorld.rayCast(rayCastCallback, fromV, tmpV.add(0, (1 - i) * 0.4f));

        }
        return hit;
    }

    protected void changeWalkingState(Customer customer) {
        customer.state = Customer.State.getRandomWalkingState();
    }

    @Override
    protected void process(int entityId) {
        customer = customerComponentMapper.get(entityId);
        rigidBody = rigidBodyComponentMapper.get(entityId);
        state = stateComponentMapper.get(entityId);
        handleBasics(entityId);
    }

    private void handleBasics(int entityId) {
        Body body = rigidBody.body;

        if (customer.receivedDamage > 0) {
            customer.damage(customer.receivedDamage);
            customer.receivedDamage = 0;
        }

        if (customer.hp <= 0) {
            customer.state = Customer.State.DYING;
            customer.lifetime = 0;
        } else {
            customer.lifetime += world.getDelta();
        }

        switch (customer.state) {
            case DYING:
                state.setCurrentState("dying");
                Filter filter = body.getFixtureList().get(0).getFilterData();
                filter.maskBits = GameManager.NOTHING_BIT;
                body.getFixtureList().get(0).setFilterData(filter);
                ActorBuilder actorBuilder = ActorBuilder.init(body.getWorld(), world);
                while (GameManager.customersLeft < GameManager.totalCustomers) {
                    actorBuilder.createCustomer1(body.getPosition().x, body.getPosition().y, Type.getRandomType());
                }

                if (state.getStateTime() <= 0) {
                    GameManager.getInstance().playSound(customer.getDieSound(), 1.0f, MathUtils.random(0.8f, 1.2f), 0);
                }

                if (state.getStateTime() > 0.6f) {
                    // decrease customer count
                    GameManager.customersLeft--;

                    body.getWorld().destroyBody(body);
                    rigidBodyComponentMapper.set(entityId, false);
                    customerComponentMapper.set(entityId, false);
                    stateComponentMapper.set(entityId, false);
                    Transform transform = transformComponentMapper.get(entityId);
                    transform.z = 999;
                }
                break;
            case WALKING_LEFT:
                state.setCurrentState("walking_left");
                if (body.getLinearVelocity().x > -customer.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(-customer.getSpeed() * body.getMass(), 0), body.getWorldCenter(), true);
                }
                if (hitSomethingHorizontal(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x - 0.5f, body.getPosition().y))) {
                    changeWalkingState(customer);
                }
                break;
            case WALKING_RIGHT:
                state.setCurrentState("walking_right");
                if (body.getLinearVelocity().x < customer.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(customer.getSpeed() * body.getMass(), 0), body.getWorldCenter(), true);
                }
                if (hitSomethingHorizontal(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x + 0.5f, body.getPosition().y))) {
                    changeWalkingState(customer);
                }
                break;
            case WALKING_UP:
                state.setCurrentState("walking_up");
                if (body.getLinearVelocity().y < customer.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(0, customer.getSpeed() * body.getMass()), body.getWorldCenter(), true);
                }
                if (hitSomethingVertical(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x, body.getPosition().y + 0.5f))) {
                    changeWalkingState(customer);
                }
                break;
            case WALKING_DOWN:
            default:
                state.setCurrentState("walking_down");
                if (body.getLinearVelocity().y > -customer.getSpeed()) {
                    body.applyLinearImpulse(new Vector2(0, -customer.getSpeed() * body.getMass()), body.getWorldCenter(), true);
                }
                if (hitSomethingVertical(body, fromVector.set(body.getPosition()), toVector.set(body.getPosition().x, body.getPosition().y - 0.5f))) {
                    changeWalkingState(customer);
                }
                break;
        }
    }
}
