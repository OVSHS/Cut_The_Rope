/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class MenuInicio implements Screen {

    private Stage stage;
    private Skin skin;
    private MainGame game;

    private Label titleLabel;
    private TextButton btnIniciarSesion;
    private TextButton btnCrearJugador;
    private TextButton btnSalir;
    
    private Texture[] frames;
    private int currentFrame = 0;
    private float timeElapsed = 0;
    private SpriteBatch batch;


    private Idiomas idioma;
    
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


    public MenuInicio(MainGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        loadFrames();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        idioma = Idiomas.getInstance();

        Table table = new Table();
        table.setFillParent(true);
        table.center();                
        table.defaults().pad(5);       
        stage.addActor(table);

        titleLabel = new Label(idioma.get("menu.inicio"), skin);
        btnIniciarSesion = new TextButton(idioma.get("btn.iniciarSesion"), skin);
        btnCrearJugador = new TextButton(idioma.get("btn.crearJugador"), skin);
        btnSalir = new TextButton(idioma.get("btn.salir"), skin);

        

        Table topRow = new Table();
        topRow.add(titleLabel).padRight(30);   

        table.add(topRow).colspan(3).padLeft(40).padBottom(20).row();

       
        table.add(btnIniciarSesion).colspan(3).center().pad(10).row();

        
        table.add(btnCrearJugador).colspan(3).center().pad(10).row();

    
        table.add(btnSalir).colspan(3).center().pad(10).row();

  
        btnIniciarSesion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new IniciarSesion(game));
            }
        });

        btnCrearJugador.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CrearJugador(game));
            }
        });

        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

    }

    private void actualizarTextos() {
        titleLabel.setText(idioma.get("menu.inicio"));
        btnIniciarSesion.setText(idioma.get("btn.iniciarSesion"));
        btnCrearJugador.setText(idioma.get("btn.crearJugador"));
        btnSalir.setText(idioma.get("btn.salir"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
    public void hide() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        for (Texture frame : frames) {
            frame.dispose();
        }
        batch.dispose();
    }
}
