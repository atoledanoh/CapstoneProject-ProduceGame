package com.atoledano.builders;

import com.atoledano.components.Type;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class MapLoader {

    public enum BLOCK {

        EMPTY(255, 255, 255), // white
        WALL(0, 0, 0), // black
        TABLE(255, 0, 0), // red
        DOOR(0, 255, 0), // green
        PLAYER(0, 0, 255), // blue
        CUSTOMER(255, 255, 0), // yellow
        PRODUCECRATE(0, 255, 255) // cyan
        // magenta
        // silver
        ; //gold

        final int color;

        BLOCK(int r, int g, int b) {
            color = r << 24 | g << 16 | b << 8 | 0xff;
        }

        boolean sameColor(int color) {
            return this.color == color;
        }
    }

    protected final World b2dWorld;
    protected final com.artemis.World world;
    protected final AssetManager assetManager;

    protected final TextureAtlas tileTextureAtlas;
    protected final Pixmap pixmap;

    protected final int mapWidth;
    protected final int mapHeight;

    protected int customerCount;
    protected int enumCounter;

    public MapLoader(World b2dWorld, com.artemis.World world) {
        this.b2dWorld = b2dWorld;
        this.world = world;

        assetManager = GameManager.getInstance().getAssetManager();
        pixmap = assetManager.get("maps/level_1.png", Pixmap.class);
        tileTextureAtlas = assetManager.get("img/newactors.pack", TextureAtlas.class);

        mapWidth = pixmap.getWidth();
        mapHeight = pixmap.getHeight();
    }

    public void loadMap() {
        ActorBuilder actorBuilder = ActorBuilder.init(b2dWorld, world);
        int color;
        customerCount = GameManager.totalCustomers;
        enumCounter = 0;
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                color = pixmap.getPixel(x, mapHeight - y - 1);
                if (BLOCK.WALL.sameColor(color)) {
                    actorBuilder.createWall(x + 0.5f, y + 0.5f, tileTextureAtlas);
                } else if (BLOCK.DOOR.sameColor(color)) {
                    actorBuilder.createDoor(x + 0.5f, y + 0.5f, tileTextureAtlas);
                } else if (BLOCK.TABLE.sameColor(color)) {
                    actorBuilder.createTable(x + 0.5f, y + 0.5f, tileTextureAtlas);
                    //todo change this to sound better
                    //produce creation
                    actorBuilder.createProduce(x + 0.51f, y + 0.51f);
                    GameManager.totalProduce++;
                } else if (BLOCK.PLAYER.sameColor(color)) {
                    actorBuilder.createPlayer(x + 0.5f, y + 0.5f, false);
                    GameManager.getInstance().setPlayerRespawnPosition(new Vector2(x + 0.5f, y + 0.5f));
                } else if (BLOCK.CUSTOMER.sameColor(color)) {
                    actorBuilder.createCustomer1(x + 0.5f, y + 0.5f, Type.getRandomType());
                } else if (BLOCK.PRODUCECRATE.sameColor(color)) {
                    actorBuilder.createBackBox(x + 0.5f, y + 0.5f, tileTextureAtlas);
                    if (enumCounter < Type.values().length) {
                        actorBuilder.createProduceCrate(x + 0.5f, y + 0.5f, enumCounter);
                        enumCounter++;
                    }
                } else if (BLOCK.EMPTY.sameColor(color)) {
                    int random = (int) (Math.random() * 10);
                    if (random < 1 && customerCount > 0) {
                        actorBuilder.createCustomer1(x + 0.5f, y + 0.5f, Type.getRandomType());
                        customerCount--;
                    }
                }
            }
        }
        actorBuilder.createTruck(3f, 22f);
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    protected Sprite createGroundSprite() {
        TextureRegion textureRegion = tileTextureAtlas.findRegion("Floor1");

        Sprite sprite = new Sprite();
        sprite.setRegion(textureRegion);
        sprite.setBounds(0, 0, 1, 1);

        return sprite;
    }

    protected Sprite createGroundSprite2() {
        TextureRegion textureRegion = tileTextureAtlas.findRegion("Floor2");

        Sprite sprite = new Sprite();
        sprite.setRegion(textureRegion);
        sprite.setBounds(0, 0, 1, 1);

        return sprite;
    }

}
