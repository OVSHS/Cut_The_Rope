package com.mario.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 *
 * @author Maria Gabriela
 */
public class MenuNiveles implements Screen {

    private SpriteBatch batch;
    private MainGame game;
    private ManejoUsuario loginManager;
    private Stage stage;
    private Texture[] frames;
    private int currentFrame = 0;
    private float timeElapsed = 0;
    private Idiomas idioma;

    // Texturas
    private Texture levelButtonTexture;
    private Texture lockedLevelButtonTexture;
    private Texture backButtonTexture;
    private Texture logoTexture;

    // Configuración de niveles
    private int maxLevels = 5; // Total number of levels
    private int unlockedLevels; // Levels unlocked by the player
    private int columnsPerRow = 3; // Reduced columns for better spacing

    // Fuentes
    private BitmapFont titleFont;
    private BitmapFont levelFont;

    public void loadFrames() {
        frames = new Texture[]{
            new Texture("fotomenu1.png"),
            new Texture("fotomenu2.png"),
            new Texture("fotomenu3.png"),
            new Texture("fotomenu4.png"),
            new Texture("fotomenu5.png"),
            new Texture("fotomenu4.png"),
            new Texture("fotomenu3.png"),
            new Texture("fotomenu2.png"),
            new Texture("fotomenu1.png"),};

    }

    public MenuNiveles(MainGame game, ManejoUsuario loginManager) {
        this.batch = new SpriteBatch();
        this.game = game;
        this.loginManager = loginManager;
        this.unlockedLevels = loginManager.getNivelDesbloqueado();// Set this based on player progress
        loadFrames();
    }

    @Override
    public void show() {
        idioma = Idiomas.getInstance();
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());

        // Crear fuentes
        createFonts();

        // Cargar texturas
        levelButtonTexture = new Texture("level_button.png");
        lockedLevelButtonTexture = new Texture("level_button_locked.png");
        backButtonTexture = new Texture("back_button.png");
        logoTexture = new Texture("title_banner.png");

        // Crear tabla principal para organizar el diseño
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Añadir espacio en la parte superior
        mainTable.add().height(200).row(); // Espacio adicional arriba

        // Logo en lugar de título
        Image logoImage = new Image(logoTexture);
        mainTable.add(logoImage).padBottom(60).row(); // Mayor padding abajo del logo

        // Crear una tabla para la cuadrícula de botones de niveles
        Table levelGrid = new Table();
        levelGrid.defaults().pad(10); // Espaciado reducido para mejor distribución

        // Añadir botones de niveles a la cuadrícula
        for (int i = 1; i <= maxLevels; i++) {
            final int levelNum = i;
            boolean isUnlocked = levelNum < unlockedLevels;

            if (isUnlocked) {
                // Crear un botón de nivel desbloqueado
                Stack buttonStack = new Stack();

                Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
                buttonStyle.up = new TextureRegionDrawable(levelButtonTexture);
                buttonStyle.down = new TextureRegionDrawable(levelButtonTexture);

                Button levelButton = new Button(buttonStyle);

                // Crear etiqueta para el número del nivel
                Label.LabelStyle numberStyle = new Label.LabelStyle(levelFont, Color.WHITE);
                Label numberLabel = new Label(String.valueOf(levelNum), numberStyle);
                numberLabel.setAlignment(Align.center);

                // Añadir componentes al stack
                buttonStack.add(levelButton);
                buttonStack.add(numberLabel);

                // Añadir efecto hover y manejador de clic al stack
                buttonStack.addListener(new ClickListener() {
                    public void enter(InputEvent event, float x, float y, int pointer, int button) {
                        buttonStack.setScale(1.05f); // Efecto hover reducido
                    }

                    public void exit(InputEvent event, float x, float y, int pointer, int button) {
                        buttonStack.setScale(1.0f);
                    }

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        launchLevel(levelNum);
                    }
                });

                // Botones más pequeños
                levelGrid.add(buttonStack).pad(10).width(80).height(80);
            } else {
                // Crear un botón de nivel bloqueado
                Stack buttonStack = new Stack();

                Button.ButtonStyle style = new Button.ButtonStyle();
                style.up = new TextureRegionDrawable(lockedLevelButtonTexture);
                style.down = new TextureRegionDrawable(lockedLevelButtonTexture);
                Button levelButton = new Button(style);

                Label.LabelStyle numberStyle = new Label.LabelStyle(levelFont, Color.GRAY);
                Label numberLabel = new Label(String.valueOf(levelNum), numberStyle);
                numberLabel.setAlignment(Align.center);

                buttonStack.add(levelButton);
                buttonStack.add(numberLabel);

                // Mismo tamaño reducido
                levelGrid.add(buttonStack).pad(10).width(80).height(80);
            }

            // Iniciar una nueva fila después de `columnsPerRow` botones
            if (i % columnsPerRow == 0) {
                levelGrid.row();
            }
        }

        // Crear botón de regreso con texto
        TextButton.TextButtonStyle backButtonStyle = new TextButton.TextButtonStyle();
        backButtonStyle.up = new TextureRegionDrawable(backButtonTexture);
        backButtonStyle.down = new TextureRegionDrawable(backButtonTexture);
        backButtonStyle.font = titleFont; // Usar fuente existente
        backButtonStyle.fontColor = Color.WHITE; // Color del texto

        TextButton backButton = new TextButton(idioma.get("btn.regresar"), backButtonStyle);

        backButton.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, int button) {
                backButton.setScale(1.1f);
            }

            public void exit(InputEvent event, float x, float y, int pointer, int button) {
                backButton.setScale(1.0f);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuPrincipal(game, loginManager));
            }
        });

        // Añadir elementos a la tabla principal con mejor espaciado
        mainTable.pad(30);
        mainTable.add(levelGrid).expand().row();
        mainTable.add(backButton).left().bottom().pad(20).width(100).height(60);

        // Añadir la tabla al stage
        stage.addActor(mainTable);
        Gdx.input.setInputProcessor(stage);
    }

    private void createFonts() {
        // Simple font setup without FreeType dependency
        titleFont = new BitmapFont();
        titleFont.getData().setScale(1.0f);
        titleFont.setColor(Color.YELLOW);

        levelFont = new BitmapFont();
        levelFont.getData().setScale(2.0f); // Larger number font
        levelFont.setColor(Color.WHITE);
    }

    private void launchLevel(int levelNum) {
        // Launch the appropriate level based on the level number
        switch (levelNum) {
            case 1:
                game.setScreen(new Nivel1(game, loginManager, 1));
                break;
            case 2:
                game.setScreen(new Nivel2(game, loginManager, 2));
                break;
            case 3:
                game.setScreen(new Nivel3(game, loginManager, 3));
                break;
            case 4:
                game.setScreen(new Nivel4(game, loginManager, 4));
                break;
            case 5:
                game.setScreen(new Nivel5(game, loginManager, 5));
                break;
            default:
                break;
        }
    }

    @Override
    public void render(float delta) {
        // Subtle animated background color
        float time = Gdx.graphics.getFrameId() % 300 / 300f;
        float r = 0.76f + 0.04f * (float) Math.sin(time * 6.28f);
        float g = 0.67f + 0.04f * (float) Math.cos(time * 6.28f);
        float b = 0.5f;

        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        timeElapsed += delta;
        if (timeElapsed > 0.225f) { // Change frame every 0.1 sec
            currentFrame = (currentFrame + 1) % frames.length;
            timeElapsed = 0;
        }
        batch.begin();
        batch.draw(frames[currentFrame], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        for (Texture frame : frames) {
            frame.dispose();
        }
        batch.dispose();
        stage.dispose();
        levelButtonTexture.dispose();
        lockedLevelButtonTexture.dispose();
        backButtonTexture.dispose();
        logoTexture.dispose(); // No olvides liberar la textura del logo
        titleFont.dispose();
        levelFont.dispose();
    }
}
