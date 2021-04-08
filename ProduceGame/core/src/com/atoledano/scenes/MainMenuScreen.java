package com.atoledano.scenes;

import com.atoledano.ProduceGame;
import com.atoledano.gamesys.GameManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen extends ScreenAdapter {

    private final ProduceGame game;
    private final SpriteBatch batch;
    private FitViewport viewport;
    private Stage stage;

    private BitmapFont font;

    private Texture backgroundTexture;

    private Texture indicationsTexture;
    private Image indications;

    private Image indicator0;
    private Image indicator1;
    private float indicatorX;
    private float indicatorY;
    private int currentSelection;
    private boolean selected;

    public MainMenuScreen(ProduceGame game) {
        this.game = game;
        this.batch = game.getSpriteBatch();
    }

    @Override
    public void show() {
        viewport = new FitViewport(1280, 720);
        stage = new Stage(viewport, batch);

        font = new BitmapFont(Gdx.files.internal("fonts/arcade.fnt"));

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Label titleLabel = new Label("Untitled Produce Game", labelStyle);
        titleLabel.setFontScale(2f);
        titleLabel.setPosition(1280 * 0.5f - titleLabel.getWidth(), 480);

        Label easyLabel = new Label("Weekday", labelStyle);
        easyLabel.setFontScale(2f);
        easyLabel.setPosition(1280 * 0.5f - easyLabel.getWidth(), 360);

        Label normalLabel = new Label("Weekend", labelStyle);
        normalLabel.setFontScale(2f);
        normalLabel.setPosition(1280 * 0.5f - normalLabel.getWidth(), 240);

        Label hardLabel = new Label("Holiday", labelStyle);
        hardLabel.setFontScale(2f);
        hardLabel.setPosition(1280 * 0.5f - hardLabel.getWidth(), 120);

        Pixmap pixmap = new Pixmap(1280, 720, Pixmap.Format.RGB888);
        pixmap.setColor(34.0f / 255.0f, 139.0f / 255.0f, 34.0f / 255.0f, 1.0f);
        pixmap.fill();
        backgroundTexture = new Texture("img/produce.jpg");
        pixmap.dispose();
        Image background = new Image(backgroundTexture);

        indicatorX = 400f;
        indicatorY = 360f;

        TextureAtlas textureAtlas = GameManager.getInstance().getAssetManager().get("img/newactors.pack", TextureAtlas.class);
        indicator0 = new Image(new TextureRegion(textureAtlas.findRegion("MainMenuLogo"), 0, 0, 40, 26));
        indicator0.setSize(80f, 52f);
        indicator0.setPosition(indicatorX, indicatorY);

        indicator1 = new Image(new TextureRegion(textureAtlas.findRegion("MainMenuLogo"), 40, 0, 40, 26));
        indicator1.setSize(80f, 52f);
        indicator1.setPosition(indicatorX, indicatorY);
        indicator1.setVisible(false);

        indicationsTexture = new Texture("img/indications.png");
        indications = new Image(indicationsTexture);
        indications.setPosition(1280f - indications.getWidth() - 12f, 12f);

        stage.addActor(background);
        stage.addActor(indications);
        stage.addActor(titleLabel);
        stage.addActor(easyLabel);
        stage.addActor(normalLabel);
        stage.addActor(hardLabel);
        stage.addActor(indicator0);
        stage.addActor(indicator1);

        currentSelection = 0;
        selected = false;

        GameManager.getInstance().playMusic("trololo_8-bit.mp3", true);
        Gdx.input.setInputProcessor(stage);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !selected) {
            GameManager.getInstance().playSound("Pickup.ogg");
            currentSelection--;
            if (currentSelection < 0) {
                currentSelection += 3;
            }

            float newIndicatorY = indicatorY - currentSelection * 120f;

            MoveToAction moveToAction = new MoveToAction();
            moveToAction.setPosition(indicatorX, newIndicatorY);
            moveToAction.setDuration(0.2f);
            indicator0.clearActions();
            indicator0.addAction(moveToAction);
            indicator1.setPosition(indicatorX, newIndicatorY);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && !selected) {
            GameManager.getInstance().playSound("Pickup.ogg");
            currentSelection++;
            if (currentSelection >= 3) {
                currentSelection -= 3;
            }

            float newIndicatorY = indicatorY - currentSelection * 120f;

            MoveToAction moveToAction = new MoveToAction();
            moveToAction.setPosition(indicatorX, newIndicatorY);
            moveToAction.setDuration(0.2f);
            indicator0.clearActions();
            indicator0.addAction(moveToAction);
            indicator1.setPosition(indicatorX, newIndicatorY);
        }

        if (!selected && (Gdx.input.isKeyJustPressed(Input.Keys.X) || Gdx.input.isKeyJustPressed(Input.Keys.Z))) {
            GameManager.getInstance().playSound("Teleport.ogg");

            selected = true;

            indicator0.setVisible(false);
            indicator1.setVisible(true);

            RunnableAction runnableAction = new RunnableAction();
            runnableAction.setRunnable(new Runnable() {
                @Override
                public void run() {
                    switch (currentSelection) {
                        case 2: // hard mode
                            GameManager.totalEnemies = 45;

                            GameManager.infiniteLives = false;
                            GameManager.resetPlayerAbilities = true;
                            break;
                        case 1: // normal mode
                            GameManager.totalEnemies = 30;

                            GameManager.infiniteLives = true;
                            GameManager.resetPlayerAbilities = true;
                            break;
                        case 0: // easy mode
                        default:
                            GameManager.totalEnemies = 15;
                            GameManager.infiniteLives = true;
                            GameManager.resetPlayerAbilities = false;
                            break;
                    }
                    GameManager.playerLives = 3;
                    game.setScreen(new PlayScreen(game, 1));
                }
            });

            stage.addAction(new SequenceAction(Actions.delay(0.2f), Actions.fadeOut(1f), runnableAction));
        }
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void hide() {
        GameManager.getInstance().stopMusic();
        dispose();
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        indicationsTexture.dispose();
        stage.dispose();
        font.dispose();
    }
}
