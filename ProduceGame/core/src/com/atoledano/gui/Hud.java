package com.atoledano.gui;

import com.atoledano.components.Player;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Hud implements Disposable {

    private final SpriteBatch batch;

    private final TextureAtlas textureAtlas;

    private final Sprite bombSprite;
    private final Sprite bombTimerSprite;
    private final Texture bgTexture;
    private final Texture bombTimerTexture;

    private float stateTime;
    private final Sprite bigBombermanSprite;
    private final Animation bigBombermanAnimation;

    private final Sprite powerSprite;
    private final Sprite speedSprite;
    private final Sprite kickSprite;
    private final Sprite remoteSprite;

    private final float SCALE = 26f;
    private final Stage stage;
    private final BitmapFont font;
    private final Label fpsLabel;
    private final Label levelLabel;
    private final Label playerLivesLabel;
    private final Label xLabel;
    private final Label zLabel;

    private boolean showFPS = false;

    private final StringBuilder stringBuilder;

    private final float leftAlignment = 25.5f;

    public Hud(SpriteBatch batch, float width, float height) {
        this.batch = batch;

        AssetManager assetManager = GameManager.getInstance().getAssetManager();
        textureAtlas = assetManager.get("img/actors.pack", TextureAtlas.class);
        TextureAtlas textureAtlas2 = assetManager.get("img/newactors.pack", TextureAtlas.class);

        bombSprite = new Sprite(new TextureRegion(textureAtlas.findRegion("Bomb"), 0, 0, 16, 16));
        bombSprite.setBounds(25.0f, 11.5f, 1, 1);

        Pixmap pixmap = new Pixmap(5, 25, Pixmap.Format.RGBA8888);
        pixmap.setColor(32.0f / 255.0f, 93.0f / 255.0f, 153.0f / 255.0f, 1.0f);
        pixmap.fill();

        bgTexture = new Texture(pixmap);

        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        bombTimerTexture = new Texture(pixmap);
        pixmap.dispose();

        bombTimerSprite = new Sprite(bombTimerTexture);
        bombTimerSprite.setBounds(26f, 12.5f, 3.0f, 0.2f);

        TextureRegion itemTextureRegion = textureAtlas.findRegion("Items");
        powerSprite = new Sprite(new TextureRegion(itemTextureRegion, 16 * 1, 0, 16, 16));
        powerSprite.setBounds(leftAlignment, 9.0f, 1, 1);

        speedSprite = new Sprite(new TextureRegion(itemTextureRegion, 16 * 2, 0, 16, 16));
        speedSprite.setBounds(leftAlignment, 8.0f, 1, 1);

        kickSprite = new Sprite(new TextureRegion(itemTextureRegion, 16 * 3, 0, 16, 16));
        kickSprite.setBounds(leftAlignment, 7.0f, 1, 1);

        remoteSprite = new Sprite(new TextureRegion(itemTextureRegion, 16 * 4, 0, 16, 16));
        remoteSprite.setBounds(leftAlignment, 6.0f, 1, 1);

        Array<TextureRegion> keyFrames = new Array<TextureRegion>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                keyFrames.add(new TextureRegion(textureAtlas2.findRegion("Big_eye"), 64 * i, 64 * j, 64, 64));
            }
        }
        bigBombermanAnimation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        bigBombermanSprite = new Sprite(bigBombermanAnimation.getKeyFrame(0));
        bigBombermanSprite.setBounds(27.5f, 0.5f, 2f, 3f);
        stateTime = 0;

        FitViewport viewport = new FitViewport(width * SCALE, height * SCALE);
        stage = new Stage(viewport, batch);
        font = new BitmapFont(Gdx.files.internal("fonts/arcade.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        fpsLabel = new Label("FPS:", labelStyle);
        fpsLabel.setFontScale(0.3f);
        fpsLabel.setPosition(26 * SCALE, -0.8f * SCALE);
        fpsLabel.setVisible(showFPS);

        levelLabel = new Label("Level", labelStyle);
        levelLabel.setPosition(25.5f * SCALE, 3 * SCALE);
        levelLabel.setFontScale(0.4f);

        playerLivesLabel = new Label("" + GameManager.playerLives, labelStyle);
        playerLivesLabel.setFontScale(0.5f);
        playerLivesLabel.setPosition(26.8f * SCALE, 12.8f * SCALE);

        Image bombermanImage = new Image(new TextureRegion(textureAtlas.findRegion("Items"), 26 * 5, 0, 16, 16));
        bombermanImage.setPosition(leftAlignment * SCALE, 13.5f * SCALE);

        xLabel = new Label("X", labelStyle);
        xLabel.setFontScale(0.4f);
        xLabel.setPosition(26.8f * SCALE, 6.3f * SCALE);

        zLabel = new Label("Z", labelStyle);
        zLabel.setFontScale(0.4f);
        zLabel.setPosition(26.8f * SCALE, 5.3f * SCALE);

        stage.addActor(fpsLabel);
        stage.addActor(levelLabel);
        stage.addActor(playerLivesLabel);
        stage.addActor(bombermanImage);
        stage.addActor(xLabel);
        stage.addActor(zLabel);

        stringBuilder = new StringBuilder();

    }

    public void setLevelInfo(int level) {
        levelLabel.setText("Level " + level);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            showFPS = !showFPS;
            fpsLabel.setVisible(showFPS);
        }
    }

    public void draw(float delta) {
        handleInput();

        stateTime += delta;
        bigBombermanSprite.setRegion(bigBombermanAnimation.getKeyFrame(stateTime));

        if (GameManager.playerProducePower + 1 < Player.MAX_PRODUCE_POWER) {
            bombSprite.setRegion(new TextureRegion(textureAtlas.findRegion("Bomb"), 0, 0, 16, 16));
        } else {
            bombSprite.setRegion(new TextureRegion(textureAtlas.findRegion("Bomb"), 0, 16 * 1, 16, 16));
        }

        batch.begin();
        batch.draw(bgTexture, 25, 0);
        for (int i = 0; i < GameManager.playerProduceCapacity; i++) {
            float alpha;
            bombSprite.setPosition(25.0f + i % 5, 11.5f - i / 5);
            alpha = i >= GameManager.playerProduceLeft ? 0.5f : 1.0f;
            bombSprite.draw(batch, alpha);
        }

        bombTimerSprite.setSize((1.0f - GameManager.playerProduceRegeratingTimeLeft / GameManager.playerProduceRegeratingTime) * 3.0f, 0.2f);
        bombTimerSprite.draw(batch);

        if (GameManager.playerProducePower > 0) {
            for (int i = 0; i < GameManager.playerProducePower; i++) {
                powerSprite.setPosition(leftAlignment + i * 0.5f, 9.0f);
                powerSprite.draw(batch);
            }

        } else {
            powerSprite.setPosition(leftAlignment, 9.0f);
            powerSprite.draw(batch, 0.5f);
        }

        if (GameManager.playerMaxSpeed > 0) {
            for (int i = 0; i < GameManager.playerMaxSpeed; i++) {
                speedSprite.setPosition(leftAlignment + i * 0.5f, 8.0f);
                speedSprite.draw(batch);
            }
        } else {
            speedSprite.setPosition(leftAlignment, 8.0f);
            speedSprite.draw(batch, 0.5f);
        }

        kickSprite.draw(batch, GameManager.playerKickProduce ? 1.0f : 0.5f);

        bigBombermanSprite.draw(batch);

        batch.end();

        // update stage
        xLabel.setVisible(GameManager.playerKickProduce);

        playerLivesLabel.setText("" + GameManager.playerLives);

        if (showFPS) {
            stringBuilder.setLength(0);
            stringBuilder.append("FPS:").append(Gdx.graphics.getFramesPerSecond());
            fpsLabel.setText(stringBuilder.toString());
        }
        stage.draw();
    }

    @Override
    public void dispose() {
        bgTexture.dispose();
        bombTimerTexture.dispose();
        font.dispose();
        stage.dispose();
    }

}
