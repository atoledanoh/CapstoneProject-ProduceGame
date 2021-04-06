package com.atoledano.builders;

import com.artemis.Entity;
import com.artemis.utils.EntityBuilder;
import com.atoledano.components.Transform;
import com.atoledano.components.*;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class ActorBuilder {

    private static final ActorBuilder instance = new ActorBuilder();

    protected final float radius = 0.45f;

    private World b2dWorld;
    private com.artemis.World world;

    private AssetManager assetManager;

    private final Vector2 fromV = new Vector2();
    private final Vector2 toV = new Vector2();
    private boolean canExplodeThrough;

    private ActorBuilder() {
    }

    public static ActorBuilder init(World b2dWorld, com.artemis.World world) {
        instance.b2dWorld = b2dWorld;
        instance.world = world;
        instance.assetManager = GameManager.getInstance().getAssetManager();

        return instance;
    }

    public void createWall(float x, float y, float mapWidth, float mapHeight, TextureAtlas tileTextureAtlas) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.TABLE_BIT;
        fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.ENEMY_BIT | GameManager.BOMB_BIT;
        body.createFixture(fixtureDef);

        polygonShape.dispose();

        Renderer renderer;

        if (x < 1.0f) {
            if (y < 1.0f) {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 0, 16 * 2, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
            } else if (y > mapHeight - 1) {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 0, 16 * 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            } else {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 0, 16 * 1, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            }
        } else if (x > mapWidth - 1) {
            if (y < 1.0f) {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 16 * 2, 16 * 2, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            } else if (y > mapHeight - 1) {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 16 * 2, 16 * 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            } else {
                renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 16 * 2, 16 * 1, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

            }
        } else if (y < 1.0f) {
            renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 16 * 1, 16 * 2, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

        } else if (y > mapHeight - 1) {
            renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 16 * 1, 16 * 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);

        } else {
            renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("wall"), 0, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        }

        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);
        new EntityBuilder(world)
                .with(
                        new Transform(x, y, 1f, 1f, 0),
                        renderer
                )
                .build();
    }

    public void createTable(float x, float y, TextureAtlas tileTextureAtlas) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.TABLE_BIT;
        fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.ENEMY_BIT | GameManager.BOMB_BIT;
        body.createFixture(fixtureDef);

        polygonShape.dispose();

        Renderer renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("indestructible"), 0, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        new EntityBuilder(world)
                .with(
                        new Transform(x, y, 1f, 1f, 0),
                        renderer
                )
                .build();
    }

    public void createDoor(float x, float y, TextureAtlas tileTextureAtlas) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.DOOR_BIT;
        fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.ENEMY_BIT | GameManager.BOMB_BIT | GameManager.EXPLOSION_BIT;
        body.createFixture(fixtureDef);

        polygonShape.dispose();

        HashMap<String, Animation> anims = new HashMap<>();
        TextureRegion textureRegion = tileTextureAtlas.findRegion("door");

        Animation anim;
        Array<TextureRegion> keyFrames = new Array<>();
        for (int i = 0; i < 4; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 16));
        }
        anim = new Animation(0.15f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("normal", anim);

        keyFrames.clear();
        for (int i = 4; i < 10; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 16));
        }
        anim = new Animation(0.125f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("exploding", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        Entity e = new EntityBuilder(world)
                .with(
                        new Door(),
                        new Transform(x, y, 1, 1, 0),
                        new RigidBody(body),
                        new State("normal"),
                        renderer,
                        new Anim(anims)
                )
                .build();

        body.setUserData(e);
    }

    public void createCustomer1(float x, float y) {
        // box2d
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 12.0f;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.ENEMY_BIT;
        fixtureDef.filter.maskBits = GameManager.POWERUP_BIT | GameManager.PLAYER_BIT | GameManager.TABLE_BIT;
        body.createFixture(fixtureDef);

        circleShape.dispose();

        // animation
        HashMap<String, Animation> anims = new HashMap<>();
        TextureAtlas textureAtlas = assetManager.get("img/newactors.pack", TextureAtlas.class);
        TextureRegion textureRegion = textureAtlas.findRegion("Customer1");
        Animation anim;

        Array<TextureRegion> keyFrames = new Array<>();
        // walking down
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 0, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_down", anim);

        keyFrames.clear();
        // walking up
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 96, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_up", anim);

        keyFrames.clear();
        // walking left
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 32, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_left", anim);

        keyFrames.clear();
        // walking right
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 64, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_right", anim);

        keyFrames.clear();
        // dying
        for (int i = 0; i < 1; i++) {
            // no dying sprite
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 0, 0));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("dying", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 24), 16 / GameManager.PPM, 24 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new EntityBuilder(world)
                .with(
                        new Enemy(1, 0.8f),
                        new Transform(x, y, 1, 1, 0),
                        new RigidBody(body),
                        new State("walking_down"),
                        renderer,
                        new Anim(anims)
                )
                .build();
        body.setUserData(e);
    }

    public void createCustomer2(float x, float y) {
        // box2d
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 12.0f;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.ENEMY_BIT;
        fixtureDef.filter.maskBits = Enemy.defaultMaskBits;
        body.createFixture(fixtureDef);

        circleShape.dispose();

        // animation
        HashMap<String, Animation> anims = new HashMap<>();
        TextureAtlas textureAtlas = assetManager.get("img/newactors.pack", TextureAtlas.class);
        TextureRegion textureRegion = textureAtlas.findRegion("Customer2");
        Animation anim;

        Array<TextureRegion> keyFrames = new Array<>();
        // walking down
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 0, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_down", anim);

        keyFrames.clear();
        // walking up
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 96, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_up", anim);

        keyFrames.clear();
        // walking left
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 32, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_left", anim);

        keyFrames.clear();
        // walking right
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 64, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_right", anim);

        keyFrames.clear();
        // dying
        for (int i = 0; i < 1; i++) {
            // no dying sprite
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 0, 0));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("dying", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 24), 16 / GameManager.PPM, 24 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new EntityBuilder(world)
                .with(
                        new Enemy(1, 1.2f, "EnemyDie1.ogg"),
                        new Transform(x, y, 1, 1, 0),
                        new RigidBody(body),
                        new State("walking_down"),
                        renderer,
                        new Anim(anims)
                )
                .build();
        body.setUserData(e);
    }

    public void createCustomer3(float x, float y) {
        // box2d
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 12.0f;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.ENEMY_BIT;
        fixtureDef.filter.maskBits = Enemy.defaultMaskBits;
        body.createFixture(fixtureDef);

        circleShape.dispose();

        // animation
        HashMap<String, Animation> anims = new HashMap<>();
        TextureAtlas textureAtlas = assetManager.get("img/newactors.pack", TextureAtlas.class);
        TextureRegion textureRegion = textureAtlas.findRegion("Customer3");
        Animation anim;

        Array<TextureRegion> keyFrames = new Array<>();
        // walking down
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 0, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_down", anim);

        keyFrames.clear();
        // walking up
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 96, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_up", anim);

        keyFrames.clear();
        // walking left
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 32, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_left", anim);

        keyFrames.clear();
        // walking right
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 64, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_right", anim);

        keyFrames.clear();
        // dying
        for (int i = 0; i < 1; i++) {
            // no dying sprite
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 0, 0));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("dying", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 24), 16 / GameManager.PPM, 24 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new EntityBuilder(world)
                .with(
                        new Enemy(1, 1.6f, "EnemyDie2.ogg"),
                        new Transform(x, y, 1, 1, 0),
                        new RigidBody(body),
                        new State("walking_down"),
                        renderer,
                        new Anim(anims)
                )
                .build();
        body.setUserData(e);
    }

    public void createPlayer(float x, float y, boolean resetPlayerAbilities) {
        // box2d
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearDamping = 12.0f;

        Body body = b2dWorld.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.PLAYER_BIT;
        fixtureDef.filter.maskBits = Player.defaultMaskBits;
        body.createFixture(fixtureDef);
        circleShape.dispose();

        // animation
        HashMap<String, Animation> anims = new HashMap<>();
        TextureAtlas textureAtlas2 = assetManager.get("img/newactors.pack", TextureAtlas.class);
        TextureRegion textureRegion = textureAtlas2.findRegion("Player");
        Animation anim;

        Array<TextureRegion> keyFrames = new Array<>();
        // walking up
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 96, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_up", anim);

        // walking left
        keyFrames.clear();
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 32, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_left", anim);

        // walking down
        keyFrames.clear();
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 0, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_down", anim);

        // walking right
        keyFrames.clear();
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 32, 64, 32, 32));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("walking_right", anim);

        // idling up
        keyFrames.clear();
        keyFrames.add(new TextureRegion(textureRegion, 32, 96, 32, 32));
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("idling_up", anim);

        // idling left
        keyFrames.clear();
        keyFrames.add(new TextureRegion(textureRegion, 32, 32, 32, 32));
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("idling_left", anim);

        // idling down
        keyFrames.clear();
        keyFrames.add(new TextureRegion(textureRegion, 32, 0, 32, 32));
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("idling_down", anim);

        // idling right
        keyFrames.clear();
        keyFrames.add(new TextureRegion(textureRegion, 32, 64, 32, 32));
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("idling_right", anim);

        // dying
        keyFrames.clear();
        for (int i = 0; i < 1; i++) {
            // no dying sprite
            keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 0, 0));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        anims.put("dying", anim);

        // teleporting
        keyFrames.clear();
        keyFrames.add(new TextureRegion(textureRegion, 32, 0, 32, 32));
        keyFrames.add(new TextureRegion(textureRegion, 32, 32, 32, 32));
        keyFrames.add(new TextureRegion(textureRegion, 32, 64, 32, 32));
        keyFrames.add(new TextureRegion(textureRegion, 32, 96, 32, 32));
        anim = new Animation(0.05f, keyFrames, Animation.PlayMode.LOOP);
        anims.put("teleporting", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 24), 16 / GameManager.PPM, 24 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new com.artemis.utils.EntityBuilder(world)
                .with(
                        new Player(resetPlayerAbilities),
                        new Transform(x, y, 1, 1, 0),
                        new RigidBody(body),
                        new State("idling_down"),
                        renderer,
                        new Anim(anims)
                )
                .build();

        body.setUserData(e);
    }

    public void createBomb(Player player, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(MathUtils.floor(x) + 0.5f, MathUtils.floor(y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.45f, 0.45f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.BOMB_BIT;
        fixtureDef.filter.maskBits = Bomb.defaultMaskBits;
        body.createFixture(fixtureDef);
        polygonShape.dispose();

        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        HashMap<String, Animation> anims = new HashMap<>();
        TextureRegion textureRegion = textureAtlas.findRegion("Bomb");

        Animation anim;
        Array<TextureRegion> keyFrames = new Array<>();
        if (player.bombPower >= Player.MAX_BOMB_POWER) {
            for (int i = 0; i < 3; i++) {
                keyFrames.add(new TextureRegion(textureRegion, i * 16, 16 * 1, 16, 16));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 16));
            }
        }
        anim = new Animation(0.15f, keyFrames, Animation.PlayMode.LOOP_PINGPONG);
        anims.put("normal", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new EntityBuilder(world)
                .with(
                        new Bomb(player.bombPower, 2.0f),
                        new Transform(body.getPosition().x, body.getPosition().y, 1, 1, 0),
                        new RigidBody(body),
                        new State("normal"),
                        renderer,
                        new Anim(anims)
                )
                .build();

        body.setUserData(e);
    }

    public Entity createRemoteBomb(Player player, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(MathUtils.floor(x) + 0.5f, MathUtils.floor(y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.45f, 0.45f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.BOMB_BIT;
        fixtureDef.filter.maskBits = Bomb.defaultMaskBits;
        body.createFixture(fixtureDef);
        polygonShape.dispose();

        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        HashMap<String, Animation> anims = new HashMap<>();
        TextureRegion textureRegion = textureAtlas.findRegion("Bomb");

        Animation anim;
        Array<TextureRegion> keyFrames = new Array<>();
        if (player.bombPower >= Player.MAX_BOMB_POWER) {
            for (int i = 3; i < 5; i++) {
                keyFrames.add(new TextureRegion(textureRegion, i * 16, 16 * 1, 16, 16));
            }
        } else {
            for (int i = 3; i < 5; i++) {
                keyFrames.add(new TextureRegion(textureRegion, i * 16, 0, 16, 16));
            }
        }
        anim = new Animation(0.15f, keyFrames, Animation.PlayMode.LOOP_PINGPONG);
        anims.put("normal", anim);

        Renderer renderer = new Renderer(new TextureRegion(textureRegion, 0, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new EntityBuilder(world)
                .with(
                        new Bomb(player.bombPower, 16.0f),
                        new Transform(body.getPosition().x, body.getPosition().y, 1, 1, 0),
                        new RigidBody(body),
                        new State("normal"),
                        renderer,
                        new Anim(anims)
                )
                .build();

        body.setUserData(e);
        return e;
    }

    private boolean checkCanExplodeThrough(Vector2 fromV, Vector2 toV) {
        canExplodeThrough = true;
        RayCastCallback rayCastCallback = new RayCastCallback() {

            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                if (fixture.getFilterData().categoryBits == GameManager.TABLE_BIT) {
                    canExplodeThrough = false;
                    return 0;
                }

                if (fixture.getFilterData().categoryBits == GameManager.DOOR_BIT) {
                    canExplodeThrough = false;
                    Entity e = (Entity) fixture.getBody().getUserData();
                    Door door = e.getComponent(Door.class);
                    door.state = Door.State.EXPLODING;
                    return 0;
                }
                return 0;
            }
        };

        b2dWorld.rayCast(rayCastCallback, fromV, toV);
        return canExplodeThrough;
    }

    public void createExplosion(float x, float y, int power) {
        x = MathUtils.floor(x) + 0.5f;
        y = MathUtils.floor(y) + 0.5f;

        TextureRegion textureRegion = assetManager.get("img/newactors.pack", TextureAtlas.class).findRegion("Explosion");
        HashMap<String, Animation> anims = new HashMap<>();

        Array<TextureRegion> keyFrames = new Array<>();
        Animation anim;

        // center
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        Body explosionBody = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.3f, 0.3f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
        fixtureDef.filter.maskBits = GameManager.ENEMY_BIT;
        fixtureDef.isSensor = true;
        explosionBody.createFixture(fixtureDef);

        Entity e = new EntityBuilder(world)
                .with(
                        new Explosion(),
                        new Transform(x, y, 1, 1, 0),
                        new RigidBody(explosionBody),
                        new State("exploding")
                )
                .build();
        explosionBody.setUserData(e);

        // up
        for (int i = 0; i < power; i++) {
            if (!checkCanExplodeThrough(fromV.set(x, y + i), toV.set(x, y + i + 1))) {
                break;
            }

            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y + i + 1);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = GameManager.ENEMY_BIT;
            fixtureDef.isSensor = true;
            explosionBody.createFixture(fixtureDef);

            new EntityBuilder(world)
                    .with(
                            new Explosion(),
                            new Transform(x, y + i + 1, 1, 1, 0),
                            new RigidBody(explosionBody),
                            new State("exploding")
                    )
                    .build();
            explosionBody.setUserData(e);
        }

        // down
        for (int i = 0; i < power; i++) {
            if (!checkCanExplodeThrough(fromV.set(x, y - i), toV.set(x, y - i - 1))) {
                break;
            }

            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x, y - i - 1);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = GameManager.ENEMY_BIT;
            fixtureDef.isSensor = true;
            explosionBody.createFixture(fixtureDef);

            new EntityBuilder(world)
                    .with(
                            new Explosion(),
                            new Transform(x, y - i - 1, 1, 1, 0),
                            new RigidBody(explosionBody),
                            new State("exploding")
                    )
                    .build();
            explosionBody.setUserData(e);
        }

        // left
        for (int i = 0; i < power; i++) {
            if (!checkCanExplodeThrough(fromV.set(x - i, y), toV.set(x - i - 1, y))) {
                break;
            }

            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x - i - 1, y);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = GameManager.ENEMY_BIT;
            fixtureDef.isSensor = true;
            explosionBody.createFixture(fixtureDef);

            new EntityBuilder(world)
                    .with(
                            new Explosion(),
                            new Transform(x - i - 1, y, 1, 1, 0),
                            new RigidBody(explosionBody),
                            new State("exploding")
                    )
                    .build();
            explosionBody.setUserData(e);
        }

        // right
        for (int i = 0; i < power; i++) {
            if (!checkCanExplodeThrough(fromV.set(x + i, y), toV.set(x + i + 1, y))) {
                break;
            }

            // box2d
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x + i + 1, y);
            explosionBody = b2dWorld.createBody(bodyDef);
            fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.EXPLOSION_BIT;
            fixtureDef.filter.maskBits = GameManager.ENEMY_BIT;
            fixtureDef.isSensor = true;
            explosionBody.createFixture(fixtureDef);

            new EntityBuilder(world)
                    .with(
                            new Explosion(),
                            new Transform(x + i + 1, y, 1, 1, 0),
                            new RigidBody(explosionBody),
                            new State("exploding")
                    )
                    .build();
            explosionBody.setUserData(e);
        }

        polygonShape.dispose();
    }

    public void createPowerUp(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(MathUtils.floor(x) + 0.5f, MathUtils.floor(y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.POWERUP_BIT;
        fixtureDef.filter.maskBits = GameManager.ENEMY_BIT;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        PowerUp powerUp = new PowerUp();
        int i;
        switch (powerUp.type) {
            case APPLE:
                i = 6;
                break;
            case ORANGE:
                i = 7;
                break;
            case ONE_UP:
                i = 5;
                break;
            case REMOTE:
                i = 4;
                break;
            case KICK:
                i = 3;
                break;
            case SPEED:
                i = 2;
                break;
            case POWER:
                i = 1;
                break;
            case AMMO:
            default:
                i = 0;
                break;

        }

        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        Renderer renderer = new Renderer(new TextureRegion(textureAtlas.findRegion("Items"), i * 16, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        Entity e = new EntityBuilder(world)
                .with(
                        powerUp,
                        new RigidBody(body),
                        new Transform(body.getPosition().x, body.getPosition().y, 1, 1, 0),
                        new State("normal"),
                        renderer
                )
                .build();

        body.setUserData(e);
        polygonShape.dispose();
    }

    public void createProduceCrate(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(MathUtils.floor(x) + 0.5f, MathUtils.floor(y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.PRODUCECRATE_BIT;
        fixtureDef.filter.maskBits = GameManager.PLAYER_BIT;
//        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        ProduceCrate produceCrate = new ProduceCrate();
        int i;
        switch (produceCrate.type) {
            case APPLE:
                i = 6;
                break;
            case ORANGE:
                i = 7;
                break;
            case ONE_UP:
                i = 5;
                break;
            case REMOTE:
                i = 4;
                break;
            case KICK:
                i = 3;
                break;
            case SPEED:
                i = 2;
                break;
            case POWER:
                i = 1;
                break;
            case AMMO:
            default:
                i = 0;
                break;

        }

        TextureAtlas textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        Renderer renderer = new Renderer(new TextureRegion(textureAtlas.findRegion("Items"), i * 16, 0, 16, 16), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        Entity e = new EntityBuilder(world)
                .with(
                        produceCrate,
                        new RigidBody(body),
                        new Transform(body.getPosition().x, body.getPosition().y, 1, 1, 0),
                        new State("normal"),
                        renderer
                )
                .build();

        body.setUserData(e);
        polygonShape.dispose();
    }
}
