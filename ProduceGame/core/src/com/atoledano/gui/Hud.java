package com.atoledano.gui;

import com.atoledano.components.Type;
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

    private final Texture bgTexture;
    private final Sprite bigEyeSprite;
    private final Sprite slotSprite;
    private Sprite inventorySlot;
    private final Array<Sprite> sprites;
    private final Array<Sprite> inventory;
    private final Animation bigEyeAnimation;
    private float stateTime;
    private final Stage stage;
    private final BitmapFont font;
    private final Label fpsLabel;
    private final Label customersLabel;
    private final Label percentageLabel;

    private boolean showFPS = true;

    private final StringBuilder stringBuilderFps;
    private final StringBuilder stringBuilderCustomers;
    private final StringBuilder stringBuilderPercentage;

    private final float leftAlignment = 25.5f;

    public Hud(SpriteBatch batch, float width, float height) {
        this.batch = batch;

        sprites = new Array<>(34);
        inventory = new Array<>(6);
        AssetManager assetManager = GameManager.getInstance().getAssetManager();
        TextureAtlas textureAtlas = assetManager.get("img/newactors.pack", TextureAtlas.class);

        Pixmap pixmap = new Pixmap(5, 25, Pixmap.Format.RGBA8888);
        pixmap.setColor(32.0f / 255.0f, 93.0f / 255.0f, 153.0f / 255.0f, 1.0f);
        pixmap.fill();

        bgTexture = new Texture(pixmap);

        slotSprite = new Sprite(new TextureRegion(textureAtlas.findRegion("Slot"), 0, 0, 40, 40));
        slotSprite.setBounds(leftAlignment, 16f, 2f, 2f);


        for (Type type : Type.values()) {
            int i = type.ordinal();
            sprites.add(new Sprite(new TextureRegion(textureAtlas.findRegion("Produce"), (i % 8) * 32, (i / 8) * 32, 32, 32)));
        }

        //big eye set up
        Array<TextureRegion> keyFrames = new Array<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                keyFrames.add(new TextureRegion(textureAtlas.findRegion("Big_eye"), 64 * i, 64 * j, 64, 64));
            }
        }
        bigEyeAnimation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        bigEyeSprite = new Sprite(bigEyeAnimation.getKeyFrame(0));
        bigEyeSprite.setBounds(leftAlignment, 0.5f, 3f, 3f);

        stateTime = 0;

        float SCALE = 26f;
        FitViewport viewport = new FitViewport(width * SCALE, height * SCALE);
        stage = new Stage(viewport, batch);
        font = new BitmapFont(Gdx.files.internal("fonts/arcade.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        fpsLabel = new Label("FPS: ", labelStyle);
        fpsLabel.setFontScale(.8f);
        fpsLabel.setPosition(25.5f * SCALE, 19f * SCALE);
        fpsLabel.setVisible(true);

        customersLabel = new Label("Served: ", labelStyle);
        customersLabel.setFontScale(.6f);
        customersLabel.setPosition(25.5f * SCALE, 10f * SCALE);
        customersLabel.setVisible(true);

        percentageLabel = new Label("%: ", labelStyle);
        percentageLabel.setFontScale(.6f);
        percentageLabel.setPosition(25.5f * SCALE, 8f * SCALE);
        percentageLabel.setVisible(true);

        Image hudImage = new Image(new TextureRegion(textureAtlas.findRegion("Logo"), 0, 0, 128, 128));
        hudImage.setBounds(25 * SCALE, 20 * SCALE, 128, 128);


        stage.addActor(fpsLabel);
        stage.addActor(customersLabel);
        stage.addActor(percentageLabel);

        stage.addActor(hudImage);

        stringBuilderFps = new StringBuilder();
        stringBuilderCustomers = new StringBuilder();
        stringBuilderPercentage = new StringBuilder();
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
        bigEyeSprite.setRegion(bigEyeAnimation.getKeyFrame(stateTime));

        batch.begin();

        batch.draw(bgTexture, 25, 0);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                slotSprite.setPosition(leftAlignment + i * 2, 16 - j * 2);
                slotSprite.draw(batch);
            }
        }

        if (GameManager.types.size > 0) {
            for (int i = 0; i < GameManager.types.size; i++) {
                for (Type type : Type.values()) {
                    if (GameManager.types.get(i) == type) {
                        inventory.add(sprites.get(type.ordinal()));
                        inventory.set(i, sprites.get(type.ordinal()));
                        inventorySlot = inventory.get(i);
                    }
                }
                for (int j = 0; j < 1; j++) {
                    inventorySlot.setBounds(leftAlignment + (i % 2) * 2, 16 - (i / 2) * 2, 2, 2);
                    inventorySlot.draw(batch);
                }
            }
        }

        bigEyeSprite.draw(batch);

        batch.end();

        stringBuilderFps.setLength(0);
        stringBuilderFps.append("FPS: ").append(Gdx.graphics.getFramesPerSecond());
        fpsLabel.setText(stringBuilderFps.toString());

        stringBuilderCustomers.setLength(0);
        stringBuilderCustomers.append("Served: ").append(GameManager.customersServed);
        customersLabel.setText(stringBuilderCustomers.toString());

        stringBuilderPercentage.setLength(0);
        stringBuilderPercentage.append("Percentage: \n").append(((GameManager.totalProduce - GameManager.soldProduce) * 100 / GameManager.totalProduce));
//        stringBuilderPercentage.append("Percentage: \n").append(((GameManager.totalProduce)));
        percentageLabel.setText(stringBuilderPercentage.toString());

        stage.draw();
    }

    @Override
    public void dispose() {
        bgTexture.dispose();
        font.dispose();
        stage.dispose();
    }

}
