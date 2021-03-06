package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.IteratingSystem;
import com.atoledano.builders.ActorBuilder;
import com.atoledano.components.Transform;
import com.atoledano.components.*;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.Queue;

public class PlayerSystem extends IteratingSystem {

    protected ComponentMapper<Player> playerComponentMapper;
    protected ComponentMapper<RigidBody> rigidBodyComponentMapper;
    protected ComponentMapper<State> stateComponentMapper;
    protected ComponentMapper<Renderer> rendererComponentMapper;

    private boolean hitting;
    private boolean kicking;
    private Produce kickingProduce;
    private final Vector2 fromV;
    private final Vector2 toV;

    public PlayerSystem() {
        super(Aspect.all(Player.class, Transform.class, Renderer.class, RigidBody.class, State.class));
        fromV = new Vector2();
        toV = new Vector2();
    }

    @Override
    protected void process(int entityId) {
        Player player = playerComponentMapper.get(entityId);
        RigidBody rigidBody = rigidBodyComponentMapper.get(entityId);
        State state = stateComponentMapper.get(entityId);
        Renderer renderer = rendererComponentMapper.get(entityId);

        Body body = rigidBody.body;

        Vector2 linearVelocity = body.getLinearVelocity();

        float maxSpeed = player.maxSpeed;

        // player movement controls
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (hitBombVertical(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x, body.getPosition().y + 0.5f))) {
                if (Math.abs(linearVelocity.y) < maxSpeed) {
                    body.applyLinearImpulse(new Vector2(0, player.acceleration * body.getMass()), body.getWorldCenter(), true);
                }
            }

            player.state = Player.State.WALKING_UP;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (hitBombVertical(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x, body.getPosition().y - 0.5f))) {
                if (Math.abs(linearVelocity.y) < maxSpeed) {
                    body.applyLinearImpulse(new Vector2(0, -player.acceleration * body.getMass()), body.getWorldCenter(), true);
                }
            }

            player.state = Player.State.WALKING_DOWN;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (hitBombHorizontal(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x - 0.5f, body.getPosition().y))) {
                if (Math.abs(linearVelocity.x) < maxSpeed) {
                    body.applyLinearImpulse(new Vector2(-player.acceleration * body.getMass(), 0), body.getWorldCenter(), true);
                }
            }

            player.state = Player.State.WALKING_LEFT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (hitBombHorizontal(body, fromV.set(body.getPosition()), toV.set(body.getPosition().x + 0.5f, body.getPosition().y))) {
                if (Math.abs(linearVelocity.x) < maxSpeed) {
                    body.applyLinearImpulse(new Vector2(player.acceleration * body.getMass(), 0), body.getWorldCenter(), true);
                }
            }

            player.state = Player.State.WALKING_RIGHT;
        }

        // set bomb or kick bomb
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            kicking = false;
            if (player.kickBomb) {
                // check if player is facing a bomb, if so, kick it
                switch (player.state) {
                    case WALKING_UP:
                        if (checkCanKickBomb(body, fromV.set(body.getPosition()), toV.set(new Vector2(body.getPosition().x, body.getPosition().y + 0.6f)))) {
                            kickingProduce.setMove(Produce.State.MOVING_UP);
                            GameManager.getInstance().playSound("KickBomb.ogg");
                        }
                        break;
                    case WALKING_DOWN:
                        if (checkCanKickBomb(body, fromV.set(body.getPosition()), toV.set(new Vector2(body.getPosition().x, body.getPosition().y - 0.6f)))) {
                            kickingProduce.setMove(Produce.State.MOVING_DOWN);
                            GameManager.getInstance().playSound("KickBomb.ogg");
                        }
                        break;
                    case WALKING_LEFT:
                        if (checkCanKickBomb(body, fromV.set(body.getPosition()), toV.set(new Vector2(body.getPosition().x - 0.6f, body.getPosition().y)))) {
                            kickingProduce.setMove(Produce.State.MOVING_LEFT);
                            GameManager.getInstance().playSound("KickBomb.ogg");
                        }
                        break;
                    case WALKING_RIGHT:
                        if (checkCanKickBomb(body, fromV.set(body.getPosition()), toV.set(new Vector2(body.getPosition().x + 0.6f, body.getPosition().y)))) {
                            kickingProduce.setMove(Produce.State.MOVING_RIGHT);
                            GameManager.getInstance().playSound("KickBomb.ogg");
                        }
                        break;
                    default:
                        break;
                }
            }

            if (!kicking && player.produceLeft > 0) {
                // create bomb
                ActorBuilder actorBuilder = ActorBuilder.init(body.getWorld(), world);

                GameManager.getInstance().getRemoteBombDeque().offer(actorBuilder.createProduce(
                        player.types.pop(),
                        body.getPosition().x, body.getPosition().y));
                GameManager.types.pop();

                player.produceLeft--;
                GameManager.getInstance().playSound("PlaceBomb.ogg");
            }

        }

        // trigger remote bomb
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) && player.remoteBomb) {
            Queue<Entity> remoteBombQueue = GameManager.getInstance().getRemoteBombDeque();

            // clean those bombs which have already exploded
            while (!remoteBombQueue.isEmpty() && remoteBombQueue.peek().getComponent(Produce.class) == null) {
                remoteBombQueue.remove();
            }

            Entity remoteBombEntity = remoteBombQueue.poll();
            if (remoteBombEntity != null) {
                Produce remoteProduce = remoteBombEntity.getComponent(Produce.class);
                remoteProduce.isDestroyed = true;
            }
        }

        if (linearVelocity.len2() < 0.1f) {
            switch (player.state) {
                case WALKING_UP:
                    player.state = Player.State.IDLING_UP;
                    break;
                case WALKING_DOWN:
                    player.state = Player.State.IDLING_DOWN;
                    break;
                case WALKING_LEFT:
                    player.state = Player.State.IDLING_LEFT;
                    break;
                case WALKING_RIGHT:
                    player.state = Player.State.IDLING_RIGHT;
                    break;
                default:
                    break;
            }
        }


        Filter filter = body.getFixtureList().get(0).getFilterData();
        filter.maskBits = -1;
        body.getFixtureList().get(0).setFilterData(filter);
        renderer.setColor(Color.WHITE);

        switch (player.state) {

            case WALKING_UP:
                state.setCurrentState("walking_up");
                break;
            case WALKING_LEFT:
                state.setCurrentState("walking_left");
                break;
            case WALKING_DOWN:
                state.setCurrentState("walking_down");
                break;
            case WALKING_RIGHT:
                state.setCurrentState("walking_right");
                break;
            case IDLING_LEFT:
                state.setCurrentState("idling_left");
                break;
            case IDLING_RIGHT:
                state.setCurrentState("idling_right");
                break;
            case IDLING_UP:
                state.setCurrentState("idling_up");
                break;
            case IDLING_DOWN:
            default:
                state.setCurrentState("idling_down");
                break;
        }

    }

    protected boolean checkCanKickBomb(Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        kickingProduce = null;
        kicking = false;

        RayCastCallback rayCastCallback = (fixture, point, normal, fraction) -> {
            if (fixture.getFilterData().categoryBits == GameManager.PRODUCE_BIT) {
                Entity bombEntity = (Entity) fixture.getBody().getUserData();
                kickingProduce = bombEntity.getComponent(Produce.class);
                return 0;
            }
            return 0;
        };

        b2dWorld.rayCast(rayCastCallback, fromV, toV);
        if (kickingProduce != null) {
            kicking = true;
        }
        return kicking;
    }

    protected boolean hitBombVertical(final Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        hitting = false;

        RayCastCallback rayCastCallback = (fixture, point, normal, fraction) -> {
            if (fixture.getBody() == body) {
                return 1;
            }

            if (fraction < 1.0f && fixture.getFilterData().categoryBits == GameManager.PRODUCE_BIT) {
                hitting = true;
            }
            return 0;
        };

        for (int i = 0; i < 3; i++) {
            Vector2 tmpV = new Vector2(toV);
            b2dWorld.rayCast(rayCastCallback, fromV, tmpV.add((1 - i) * 0.4f, 0));

        }
        return !hitting;
    }

    protected boolean hitBombHorizontal(final Body body, Vector2 fromV, Vector2 toV) {
        World b2dWorld = body.getWorld();
        hitting = false;

        RayCastCallback rayCastCallback = (fixture, point, normal, fraction) -> {
            if (fixture.getBody() == body) {
                return 1;
            }

            if (fraction < 1.0f && fixture.getFilterData().categoryBits == GameManager.PRODUCE_BIT) {
                hitting = true;
            }
            return 0;
        };

        for (int i = 0; i < 3; i++) {
            Vector2 tmpV = new Vector2(toV);
            b2dWorld.rayCast(rayCastCallback, fromV, tmpV.add(0, (1 - i) * 0.4f));
        }
        return !hitting;
    }
}
