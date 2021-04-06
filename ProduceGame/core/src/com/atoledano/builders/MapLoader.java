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
        PRODUCECRATE(0, 255, 255), // cyan
        BACKSHOP(255, 0, 255), // magenta
        RAT(128, 128, 128), // silver
        KAREN(128, 128, 0); //gold

        int color;

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

    protected TextureAtlas tileTextureAtlas;
    protected Pixmap pixmap;

    protected int mapWidth;
    protected int mapHeight;

    protected int level;
    protected int enemyCount;
    protected int enumCounter;

    protected final float radius = 0.46f;

    public MapLoader(World b2dWorld, com.artemis.World world, int level) {
        this.b2dWorld = b2dWorld;
        this.world = world;
        this.level = level;
        assetManager = GameManager.getInstance().getAssetManager();

        pixmap = assetManager.get("maps/level_" + level + ".png", Pixmap.class);
        switch (level) {
            case 3:
                tileTextureAtlas = assetManager.get("maps/area_3_tiles.pack", TextureAtlas.class);
                break;
            case 2:
            case 1:
            default:
                tileTextureAtlas = assetManager.get("maps/area_1_tiles.pack", TextureAtlas.class);
                break;
        }

        mapWidth = pixmap.getWidth();
        mapHeight = pixmap.getHeight();
    }

    public void loadMap() {
        ActorBuilder actorBuilder = ActorBuilder.init(b2dWorld, world);
        int color;
        enemyCount = GameManager.totalEnemies;
        enumCounter = 0;
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                color = pixmap.getPixel(x, mapHeight - y - 1);
                if (BLOCK.WALL.sameColor(color)) {
                    actorBuilder.createWall(x + 0.5f, y + 0.5f, mapWidth, mapHeight, tileTextureAtlas);
                } else if (BLOCK.DOOR.sameColor(color)) {
                    actorBuilder.createDoor(x + 0.5f, y + 0.5f, tileTextureAtlas);
                } else if (BLOCK.TABLE.sameColor(color)) {
                    actorBuilder.createTable(x + 0.5f, y + 0.5f, tileTextureAtlas);
                    //todo change this to sound better
                    //powerup / produce creation
                    actorBuilder.createPowerUp(x + 0.51f, y + 0.51f);
                } else if (BLOCK.PLAYER.sameColor(color)) {
                    actorBuilder.createPlayer(x + 0.5f, y + 0.5f, false);
                    GameManager.getInstance().setPlayerRespawnPosition(new Vector2(x + 0.5f, y + 0.5f));
                } else if (BLOCK.CUSTOMER.sameColor(color)) {
                    actorBuilder.createCustomer1(x + 0.5f, y + 0.5f);
                } else if (BLOCK.PRODUCECRATE.sameColor(color)) {
                    actorBuilder.createTable(x + 0.5f, y + 0.5f, tileTextureAtlas);
                    if (enumCounter< Type.values().length){
                        actorBuilder.createProduceCrate(x + 0.5f, y + 0.5f, enumCounter);
                        enumCounter++;
                    }
                } else if (BLOCK.BACKSHOP.sameColor(color)) {
                    //todo - nothing to do here
                } else if (BLOCK.EMPTY.sameColor(color)) {
                    int random = (int) (Math.random() * 10);
                    if (random < 1 && enemyCount > 0) {
                        actorBuilder.createCustomer1(x + 0.5f, y + 0.5f);
                        enemyCount--;
                    }
                }
            }
        }
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    protected Sprite createGroundSprite() {
        TextureRegion textureRegion = tileTextureAtlas.findRegion("ground");

        Sprite sprite = new Sprite();
        sprite.setRegion(textureRegion);
        sprite.setBounds(0, 0, 1, 1);

        return sprite;
    }
}
