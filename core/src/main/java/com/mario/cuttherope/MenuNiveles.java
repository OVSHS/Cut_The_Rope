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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Menu de selección de niveles para el juego Cut The Rope
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
    
    // Texturas
    private Texture levelButtonTexture;
    private Texture lockedLevelButtonTexture;
    private Texture backButtonTexture;
    
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
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        
        // Create fonts
        createFonts();
        
        // Load textures
        levelButtonTexture = new Texture("level_button.png");
        lockedLevelButtonTexture = new Texture("level_button_locked.png");
        backButtonTexture = new Texture("back_button.png");

        // Create main table to organize the layout
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        
        // Add title
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label titleLabel = new Label("SELECT LEVEL", titleStyle);
        
        // Create a table for the level buttons grid
        Table levelGrid = new Table();
        levelGrid.defaults().pad(20); // Increased padding for better spacing

        // Add level buttons to the grid
        for (int i = 1; i <= maxLevels; i++) {
            final int levelNum = i;
            boolean isUnlocked = levelNum <= unlockedLevels;

            if (isUnlocked) {
                // Create a stack to hold button and number
                Stack buttonStack = new Stack();
                
                // Create an unlocked level button with better styling
                Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
                buttonStyle.up = new TextureRegionDrawable(levelButtonTexture);
                buttonStyle.down = new TextureRegionDrawable(levelButtonTexture);
                
                Button levelButton = new Button(buttonStyle);
                
                // Create label for level number
                Label.LabelStyle numberStyle = new Label.LabelStyle(levelFont, Color.WHITE);
                Label numberLabel = new Label(String.valueOf(levelNum), numberStyle);
                numberLabel.setAlignment(Align.center);
                
                // Add components to stack
                buttonStack.add(levelButton);
                buttonStack.add(numberLabel);
                
                // Add hover effect and click handler to the stack
                buttonStack.addListener(new ClickListener() {
                    public void enter(InputEvent event, float x, float y, int pointer, int button) {
                        buttonStack.setScale(1.1f); // Grow on hover
                    }
                    
                    public void exit(InputEvent event, float x, float y, int pointer, int button) {
                        buttonStack.setScale(1.0f); // Return to normal size
                    }
                    
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        // Launch the appropriate level screen
                        launchLevel(levelNum);
                    }
                });
                
                // Add to the grid with larger dimensions
                levelGrid.add(buttonStack).pad(15).width(120).height(120);
            } else {
                // Create a stack for locked level
                Stack buttonStack = new Stack();
                
                // Create a locked level button
                Button.ButtonStyle style = new Button.ButtonStyle();
                style.up = new TextureRegionDrawable(lockedLevelButtonTexture);
                style.down = new TextureRegionDrawable(lockedLevelButtonTexture);
                Button levelButton = new Button(style);
                
                // Create label for level number
                Label.LabelStyle numberStyle = new Label.LabelStyle(levelFont, Color.GRAY);
                Label numberLabel = new Label(String.valueOf(levelNum), numberStyle);
                numberLabel.setAlignment(Align.center);
                
                // Add components to stack
                buttonStack.add(levelButton);
                buttonStack.add(numberLabel);
                
                // Add to the grid
                levelGrid.add(buttonStack).pad(15).width(120).height(120);
            }

            // Start a new row after columnsPerRow buttons
            if (i % columnsPerRow == 0) {
                levelGrid.row();
            }
        }

        // Create back button with animation effect
        Button backButton = new Button(new TextureRegionDrawable(backButtonTexture));
        backButton.addListener(new ClickListener() {
            public void enter(InputEvent event, float x, float y, int pointer, int button) {
                backButton.setScale(1.1f); // Grow on hover
            }
            
            public void exit(InputEvent event, float x, float y, int pointer, int button) {
                backButton.setScale(1.0f); // Return to normal size
            }
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuPrincipal(game, loginManager));
            }
        });

        // Add elements to the main table with better spacing
        mainTable.pad(40);
        mainTable.top();
        mainTable.add(titleLabel).padBottom(40).row();
        mainTable.add(levelGrid).expand().row();
        mainTable.add(backButton).left().bottom().pad(20).width(80).height(80);

        // Add the table to the stage
        stage.addActor(mainTable);
        Gdx.input.setInputProcessor(stage);
    }
    
    private void createFonts() {
        // Simple font setup without FreeType dependency
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        titleFont.setColor(Color.YELLOW);
        
        levelFont = new BitmapFont();
        levelFont.getData().setScale(2.0f); // Larger number font
        levelFont.setColor(Color.WHITE);
    }

    private void launchLevel(int levelNum) {
        // Launch the appropriate level based on the level number
        switch (levelNum) {
            case 1:
                game.setScreen(new Nivel5(game, loginManager, 1));
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
        float r = 0.76f + 0.04f * (float)Math.sin(time * 6.28f);
        float g = 0.67f + 0.04f * (float)Math.cos(time * 6.28f);
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
        titleFont.dispose();
        levelFont.dispose();
    }
}