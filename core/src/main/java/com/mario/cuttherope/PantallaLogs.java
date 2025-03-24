/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 *
 * @author Maria Gabriela
 */
public class PantallaLogs implements Screen {

    private Stage stage;
    private ManejoUsuario loginManager;
    private MainGame game;
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

    public PantallaLogs(MainGame game, ManejoUsuario loginManager) {
        this.game = game;
        this.loginManager = loginManager;
        this.stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        this.game = game;
        idioma = Idiomas.getInstance();
        Table table = new Table();
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        table.setFillParent(true);
        table.defaults().pad(10);

        batch = new SpriteBatch();
        loadFrames();
        Label.LabelStyle titleStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        // Título
        Label titleLabel = new Label(idioma.get("menu.logs"), skin);
        table.add(titleLabel).colspan(2).row();

        // Encabezados
        Label nombreHeader = new Label(idioma.get("logs.name"), skin);
        table.add(nombreHeader);
        Label estrellasHeader = new Label(idioma.get("logs.estrellas"), skin);
        table.add(estrellasHeader);
        Label FechaHeader = new Label(idioma.get("logs.date"), skin);
        table.add(FechaHeader).row();

        // Filas de logs
        for (LogPartida log : loginManager.getLogsPartidas()) {
            table.add(new Label(String.valueOf(log.getNivel()), titleStyle));
            table.add(new Label(String.valueOf(log.getEstrellas()), titleStyle));
            table.add(new Label(log.getFechaHora(), titleStyle)).row();
        }
        // Botón de regreso
        TextButton backButton = new TextButton(idioma.get("btn.salir"), skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Cambiar a la pantalla anterior (por ejemplo, MenuPrincipal)
                game.setScreen(new MenuPrincipal(game, loginManager));
            }
        });

        // Añadir el botón de regreso en la parte inferior
        table.row().padTop(20); // Espacio arriba del botón
        table.add(backButton).colspan(3).bottom().width(200).height(50);
        stage.addActor(table);
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
    }
}
