package com.atoledano.scenes;

import com.artemis.BaseSystem;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.atoledano.ProduceGame;
import com.atoledano.builders.WorldBuilder;
import com.atoledano.gamesys.GameManager;
import com.atoledano.gui.Hud;
import com.atoledano.listeners.B2DWorldContactListener;
import com.atoledano.systems.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PlayScreen extends ScreenAdapter {

    private final ProduceGame game;
    private final SpriteBatch batch;

    private OrthographicCamera camera;
    private FitViewport viewport;

    private World b2dWorld;
    private com.artemis.World world;

    private Box2DDebugRenderer b2dRenderer;
    private boolean showB2DDebugRenderer;

    private Sprite groundSprite;
    private Sprite groundSprite2;

    private int mapWidth;
    private int mapHeight;

    private Hud hud;

    private float b2dTimer;

    private Stage stage;
    private Texture fadeOutTexture;

    private final int level;

    private boolean paused;

    private Stage stage2;
    private Window pauseWindow;

    public PlayScreen(ProduceGame game, int level) {
        this.game = game;
        this.batch = game.getSpriteBatch();

        this.level = level;

        showB2DDebugRenderer = false;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        float WIDTH = 30;
        float HEIGHT = 25;
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        //todo - check the position to transition!!
        camera.position.set(WIDTH / 2, HEIGHT / 2, 0);

        b2dWorld = new World(new Vector2(), true);
        b2dWorld.setContactListener(new B2DWorldContactListener());
        b2dRenderer = new Box2DDebugRenderer();

        WorldConfiguration worldConfiguration = new WorldConfigurationBuilder()
                .with(
                        new PlayerSystem(),
                        new ProduceSystem(),
                        new CustomerSystem(),
                        new PhysicsSystem(),
                        new StateSystem(),
                        new AnimationSystem(),
                        new RenderSystem(batch)
                )
                .build();

        world = new com.artemis.World(worldConfiguration);

        WorldBuilder worldBuilder = new WorldBuilder(b2dWorld, world);
        worldBuilder.build(level);
        groundSprite = worldBuilder.getGroundSprite();
        groundSprite2 = worldBuilder.getGroundSprite2();

        mapWidth = worldBuilder.getMapWidth();
        mapHeight = worldBuilder.getMapHeight();

        hud = new Hud(batch, WIDTH, HEIGHT);

        b2dTimer = 0;

        GameManager.getInstance().playMusic("EduardKhil.ogg", true);

        stage = new Stage(viewport);
        Pixmap pixmap = new Pixmap((int) WIDTH, (int) HEIGHT, Pixmap.Format.RGB888);
        pixmap.setColor(0.2f, 0.2f, 0.2f, 1f);
        pixmap.fill();
        fadeOutTexture = new Texture(pixmap);
        pixmap.dispose();
        Image image = new Image(fadeOutTexture);
        stage.addActor(image);
        stage.addAction(Actions.fadeOut(0.5f));

        paused = false;

        Skin skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));

        stage2 = new Stage(new FitViewport(1280, 720), batch);
        pauseWindow = new Window("Pause", skin);
        pauseWindow.setPosition((1280 - pauseWindow.getWidth()) / 2, (720 - pauseWindow.getHeight()) / 2);
        pauseWindow.setVisible(paused);

        TextButton continueButton = new TextButton("Continue", skin);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = false;
                GameManager.getInstance().playMusic();
            }
        });

        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }

        });
        pauseWindow.add(continueButton).padBottom(16f);
        pauseWindow.row();
        pauseWindow.add(exitButton);

        stage2.addActor(pauseWindow);
        Gdx.input.setInputProcessor(stage2);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showB2DDebugRenderer = !showB2DDebugRenderer;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;

            if (paused) {
                GameManager.getInstance().playSound("Pause.ogg");
                GameManager.getInstance().pauseMusic();
            } else {
                GameManager.getInstance().playMusic();
            }
        }
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        // draw ground
        batch.begin();
        for (int y = 0; y < 19; y++) {
            for (int x = 0; x < mapWidth; x++) {
                groundSprite.setPosition(x, y);
                groundSprite.draw(batch);
            }
        }

        for (int y = 19; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                groundSprite2.setPosition(x, y);
                groundSprite2.draw(batch);
            }
        }

        batch.end();

        if (!paused) {
            b2dTimer += delta;
            if (b2dTimer > 1 / 60.0f) {
                b2dWorld.step(1 / 60.0f, 8, 3);
                b2dTimer -= 1 / 60.0f;
            }

            for (BaseSystem system : world.getSystems()) {
                system.setEnabled(true);
            }

        } else {
            for (BaseSystem system : world.getSystems()) {
                if (!(system instanceof RenderSystem)) {
                    system.setEnabled(false);
                }
            }
        }

        world.setDelta(delta);
        world.process();

        hud.draw(delta);

        stage.draw();
        stage.act(delta);

        pauseWindow.setVisible(paused);
        stage2.draw();

        if (showB2DDebugRenderer) {
            b2dRenderer.render(b2dWorld, camera.combined);
        }
    }

    @Override
    public void hide() {
        GameManager.getInstance().stopMusic();
        dispose();
    }

    @Override
    public void dispose() {

        b2dWorld.dispose();
        world.dispose();
        b2dRenderer.dispose();
        stage.dispose();
        fadeOutTexture.dispose();
        hud.dispose();
    }

}
