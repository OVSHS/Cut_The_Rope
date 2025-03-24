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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Maria Gabriela
 */
public class Ranking implements Screen {

    private Stage stage;
    private ManejoUsuario loginManager;
    private List<PerfilUsuario> ranking;
    private MainGame game;

    private Texture[] frames;
    private int currentFrame = 0;
    private float timeElapsed = 0;
    private SpriteBatch batch;
    private Skin skin;
    private Idiomas idioma;

    public Ranking(MainGame game, ManejoUsuario loginManager) {
        if (game == null) {
            throw new IllegalArgumentException("MainGame no puede ser null");
        }
        this.game = game;
        this.loginManager = loginManager;
        this.ranking = loginManager.obtenerRanking();
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        batch = new SpriteBatch();

    }

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

    @Override
    public void show() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        idioma = Idiomas.getInstance();
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(10);

        loadFrames();

        // Título del ranking
        Label titleLabel = new Label(idioma.get("menu.ranking"), skin);
        table.add(titleLabel).colspan(2).row();

        // Encabezados de la tabla
        Label.LabelStyle headerStyle = new Label.LabelStyle(new BitmapFont(), Color.YELLOW);
        Label nombreHeader = new Label(idioma.get("ranking.name"), skin);
        Label estrellasHeader = new Label(idioma.get("ranking.estrellas"), skin);
        table.add(nombreHeader).padRight(50);
        table.add(estrellasHeader).row();

        // Mostrar los jugadores en el ranking
        Label.LabelStyle rowStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        for (PerfilUsuario perfil : ranking) {
            Label nombreLabel = new Label(perfil.getApodo(), skin);
            Label estrellasLabel = new Label(String.valueOf(perfil.getCantEstrellas()), skin);
            table.add(nombreLabel).padRight(50);
            table.add(estrellasLabel).row();
        }

        // Botón para regresar
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;

        TextButton backButton = new TextButton(idioma.get("btn.regresar"), skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuPrincipal(game, loginManager));
            }
        });
        table.add(backButton).colspan(2).padTop(20);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the animated background
        timeElapsed += delta;
        if (timeElapsed > 0.225f) { // Change frame every 0.225 seconds
            currentFrame = (currentFrame + 1) % frames.length;
            timeElapsed = 0;
        }
        batch.begin();
        batch.draw(frames[currentFrame], 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Draw the stage (UI elements)
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
        stage.dispose();
        for (Texture frame : frames) {
            frame.dispose();
        }
        batch.dispose();
    }
}
