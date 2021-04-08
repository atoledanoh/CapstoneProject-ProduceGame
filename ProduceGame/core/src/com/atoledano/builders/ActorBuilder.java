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
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class ActorBuilder {

    private static final ActorBuilder instance = new ActorBuilder();

    protected final float radius = 0.45f;

    private World b2dWorld;
    private com.artemis.World world;

    private AssetManager assetManager;

    private ActorBuilder() {
    }

    public static ActorBuilder init(World b2dWorld, com.artemis.World world) {
        instance.b2dWorld = b2dWorld;
        instance.world = world;
        instance.assetManager = GameManager.getInstance().getAssetManager();

        return instance;
    }

    public void createWall(float x, float y, TextureAtlas tileTextureAtlas) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.TABLE_BIT;
        fixtureDef.filter.maskBits = -1;
        body.createFixture(fixtureDef);

        polygonShape.dispose();

        Renderer renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("Wall"), 0, 0, 70, 70), 16 / GameManager.PPM, 16 / GameManager.PPM);
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
        fixtureDef.filter.maskBits = -1;
        body.createFixture(fixtureDef);

        polygonShape.dispose();

        Renderer renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("Table"), 0, 0, 70, 70), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        new EntityBuilder(world)
                .with(
                        new Transform(x, y, 1f, 1f, 0),
                        renderer
                )
                .build();
    }

    public void createBackBox(float x, float y, TextureAtlas tileTextureAtlas) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.TABLE_BIT;
        fixtureDef.filter.maskBits = -1;
        body.createFixture(fixtureDef);

        polygonShape.dispose();

        Renderer renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("Box"), 0, 0, 70, 70), 16 / GameManager.PPM, 16 / GameManager.PPM);
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
        fixtureDef.filter.maskBits = GameManager.CUSTOMER_BIT | GameManager.PRODUCE_BIT;
        body.createFixture(fixtureDef);

        polygonShape.dispose();

        Renderer renderer = new Renderer(new TextureRegion(tileTextureAtlas.findRegion("Door"), 0, 0, 70, 70), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        Entity e = new EntityBuilder(world)
                .with(
                        new Door(),
                        new Transform(x, y, 1, 1, 0),
                        renderer
                )
                .build();

        body.setUserData(e);
    }

    public void createCustomer1(float x, float y, Type type) {
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
        fixtureDef.filter.categoryBits = GameManager.CUSTOMER_BIT;
        fixtureDef.filter.maskBits = -1;
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

        //todo remove this
        textureRegion = textureAtlas.findRegion("Produce");
        keyFrames.add(new TextureRegion(textureRegion, (type.ordinal() % 8) * 32, (type.ordinal() / 8) * 32, 32, 32));
        textureRegion = textureAtlas.findRegion("Customer1");


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
                        new Customer(1, 0.8f, type),
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
        fixtureDef.filter.maskBits = -1;
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

        keyFrames.clear();

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

    public Entity createProduce(Type type, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(MathUtils.floor(x) + 0.5f, MathUtils.floor(y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.45f, 0.45f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.PRODUCE_BIT;
        fixtureDef.filter.maskBits = GameManager.PRODUCE_BIT | GameManager.DOOR_BIT | GameManager.CUSTOMER_BIT | GameManager.PRODUCECRATE_BIT | GameManager.PLAYER_BIT;
        body.createFixture(fixtureDef);
        polygonShape.dispose();

        Produce produce = new Produce(type);
        int i = produce.type.ordinal();

        TextureAtlas textureAtlas = assetManager.get("img/newactors.pack", TextureAtlas.class);

        Renderer renderer = new Renderer(new TextureRegion(textureAtlas.findRegion("Produce"), (i % 8) * 32, (i / 8) * 32, 32, 32), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        // entity
        Entity e = new EntityBuilder(world)
                .with(
                        new Produce(type),
                        new Transform(body.getPosition().x, body.getPosition().y, 1, 1, 0),
                        new RigidBody(body),
                        new State("normal"),
                        renderer
                )
                .build();

        body.setUserData(e);
        return e;
    }

    public void createProduce(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(MathUtils.floor(x) + 0.5f, MathUtils.floor(y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.PRODUCE_BIT;
        fixtureDef.filter.maskBits = GameManager.CUSTOMER_BIT;
        body.createFixture(fixtureDef);

        Produce produce = new Produce();
        int i = produce.type.ordinal();

        TextureAtlas textureAtlas = assetManager.get("img/newactors.pack", TextureAtlas.class);

        Renderer renderer = new Renderer(new TextureRegion(textureAtlas.findRegion("Produce"), (i % 8) * 32, (i / 8) * 32, 32, 32), 16 / GameManager.PPM, 16 / GameManager.PPM);
        renderer.setOrigin(16 / GameManager.PPM / 2, 16 / GameManager.PPM / 2);

        Entity e = new EntityBuilder(world)
                .with(
                        produce,
                        new RigidBody(body),
                        new Transform(body.getPosition().x, body.getPosition().y, 1, 1, 0),
                        new State("normal"),
                        renderer
                )
                .build();

        body.setUserData(e);
        polygonShape.dispose();
    }

    public void createProduceCrate(float x, float y, int index) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(MathUtils.floor(x) + 0.5f, MathUtils.floor(y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.PRODUCECRATE_BIT;
        fixtureDef.filter.maskBits = -1;
        body.createFixture(fixtureDef);

        ProduceCrate produceCrate = new ProduceCrate(Type.values()[index]);
        int i = produceCrate.type.ordinal();

        TextureAtlas textureAtlas = assetManager.get("img/newactors.pack", TextureAtlas.class);

        Renderer renderer = new Renderer(new TextureRegion(textureAtlas.findRegion("Produce"), (i % 8) * 32, (i / 8) * 32, 32, 32), 16 / GameManager.PPM, 16 / GameManager.PPM);
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

    public void createTruck(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(MathUtils.floor(x) + 0.5f, MathUtils.floor(y) + 0.5f);

        Body body = b2dWorld.createBody(bodyDef);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.filter.categoryBits = GameManager.TABLE_BIT;
        fixtureDef.filter.maskBits = 0;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        polygonShape.dispose();

        TextureAtlas textureAtlas = assetManager.get("img/newactors.pack", TextureAtlas.class);

        Renderer renderer = new Renderer(new TextureRegion(textureAtlas.findRegion("Truck"), 0, 0, 61, 128), 32 / GameManager.PPM, 64 / GameManager.PPM);
        renderer.setOrigin(32 / GameManager.PPM / 2, 64 / GameManager.PPM / 2);

        new EntityBuilder(world)
                .with(
                        new Transform(x, y, 1f, 1f, 0),
                        renderer
                )
                .build();
    }

}
