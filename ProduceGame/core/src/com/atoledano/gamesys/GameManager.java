package com.atoledano.gamesys;

import com.artemis.Entity;
import com.atoledano.components.Type;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.LinkedList;
import java.util.Queue;

public class GameManager implements Disposable {

    private static final GameManager instance = new GameManager();

    public static final int TOTAL_LEVELS = 3;

    private final AssetManager assetManager;

    public static final float PPM = 16.0f;

    public static final short NOTHING_BIT = 0;
    public static final short TABLE_BIT = 1;
    public static final short DOOR_BIT = 1 << 1;
    public static final short PLAYER_BIT = 1 << 2;
    public static final short ENEMY_BIT = 1 << 3;
    public static final short PRODUCE_BIT = 1 << 4;
    public static final short PRODUCECRATE_BIT = 1 << 5;

    public static boolean infiniteLives = true;
    public static boolean resetPlayerAbilities = false;  // reset player abilities after dying

    public static int playerProduceCapacity = 6;
    public static int playerMaxSpeed = 0;
    public static boolean playerKickProduce = true;
    public static boolean playerRemoveProduce = true;

    private final Vector2 playerRespawnPosition;

    public static int playerLives = 3;

    public static int totalEnemies;
    public static int enemiesLeft;

    public static Array<Type> types;

    private final Queue<Entity> produceQueue;

    private final String soundPath = "sounds/";
    private final String musicPath = "music/";

    private String currentMusic = "";


    private GameManager() {
        // load resources
        assetManager = new AssetManager();

        // load actors 
        assetManager.load("img/newactors.pack", TextureAtlas.class);

        // load sounds
        assetManager.load("sounds/Pickup.ogg", Sound.class);
        assetManager.load("sounds/PlaceBomb.ogg", Sound.class);
        assetManager.load("sounds/KickBomb.ogg", Sound.class);
        assetManager.load("sounds/Powerup.ogg", Sound.class);
        assetManager.load("sounds/served.wav", Sound.class);
        assetManager.load("sounds/Pause.ogg", Sound.class);
        assetManager.load("sounds/Teleport.ogg", Sound.class);

        // load music
        assetManager.load("music/trololo_8-bit.mp3", Music.class);
        assetManager.load("music/EduardKhil.ogg", Music.class);

        // load maps
        assetManager.load("maps/level_1.png", Pixmap.class);
        assetManager.load("img/newactors.pack", TextureAtlas.class);

        assetManager.finishLoading();

        playerRespawnPosition = new Vector2();

        produceQueue = new LinkedList<>();

        types = new Array<Type>();
    }

    public static GameManager getInstance() {
        return instance;
    }

    public static void resetPlayerAbilities() {
        playerMaxSpeed = 0;
        playerKickProduce = true;
        playerRemoveProduce = true;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void playSound(String soundName) {
        playSound(soundName, 0.2f, 1.0f, 0f);
    }

    public void playSound(String soundName, float volume, float pitch, float pan) {
        Sound sound = assetManager.get(soundPath + soundName, Sound.class);
        sound.play(volume, pitch, pan);
    }

    public void playMusic(String musicName, boolean isLooping) {
        Music music = assetManager.get(musicPath + musicName);
        music.setVolume(1f);
        if (currentMusic.equals(musicName)) {
            music.setLooping(isLooping);
            if (!music.isPlaying()) {
                music.play();
            }
            return;
        }

        stopMusic();
        music.setLooping(isLooping);
        music.play();
        currentMusic = musicName;
    }

    public void playMusic() {
        if (currentMusic.isEmpty()) {
            return;
        }
        Music music = assetManager.get(musicPath + currentMusic, Music.class);
        music.play();
    }

    public void stopMusic() {
        if (currentMusic.isEmpty()) {
            return;
        }
        Music music = assetManager.get(musicPath + currentMusic, Music.class);
        if (music.isPlaying()) {
            music.stop();
        }
    }

    public void pauseMusic() {
        if (currentMusic.isEmpty()) {
            return;
        }

        Music music = assetManager.get(musicPath + currentMusic, Music.class);
        if (music.isPlaying()) {
            music.pause();
        }
    }

    public Queue<Entity> getRemoteBombDeque() {
        return produceQueue;
    }

    public void setPlayerRespawnPosition(Vector2 position) {
        playerRespawnPosition.set(position);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

}
