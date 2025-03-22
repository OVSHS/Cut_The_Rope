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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuPrincipal implements Screen {
     private Stage stage;
    private Skin skin;
    private MainGame game;
    private ManejoUsuario loginManager;
    private Idiomas idioma;
    
    private Label titleLabel;
    private TextButton btnJugar;
    private TextButton btnMiPerfil;
    private TextButton btnPreferenciasJuego;
    private TextButton btnCerrarSesion;
    
    private Texture[] frames;
    private int currentFrame = 0;
    private float timeElapsed = 0;
    private SpriteBatch batch;
    
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
    
    public MenuPrincipal(MainGame game, ManejoUsuario loginManager) {
        this.game = game;
        batch = new SpriteBatch();
        loadFrames();
        this.loginManager = loginManager;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        idioma = Idiomas.getInstance();
        
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.defaults().pad(5);
        stage.addActor(table);
        
        titleLabel = new Label(idioma.get("menu.principal"), skin);
        btnJugar = new TextButton(idioma.get("btn.jugar"), skin);
        btnMiPerfil = new TextButton(idioma.get("btn.miPerfil"), skin);
        btnPreferenciasJuego = new TextButton(idioma.get("btn.preferenciasJuego"), skin);
        btnCerrarSesion = new TextButton(idioma.get("btn.cerrarSesion"), skin);
        
        Table topRow = new Table();
        topRow.add(titleLabel).padRight(30);
        table.add(topRow).colspan(3).padBottom(20).row();
        table.add(btnJugar).colspan(3).center().pad(10).row();
        table.add(btnMiPerfil).colspan(3).center().pad(10).row();
        table.add(btnPreferenciasJuego).colspan(3).center().pad(10).row();
        table.add(btnCerrarSesion).colspan(3).center().pad(10).row();
        
        btnJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (loginManager.hayJugadorLogueado()) {
                    game.setScreen(new Nivel4(game, loginManager, 1));
                } else {
                    mostrarMensaje(idioma.get("msg.debesIniciarSesionParaJugar"));
                }
            }
        });
        
        btnMiPerfil.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (loginManager.hayJugadorLogueado()) {
                    game.setScreen(new PantallaPerfil(game, loginManager));
                } else {
                    mostrarMensaje(idioma.get("msg.debesIniciarSesionParaVerPerfil"));
                }
            }
        });
        
        btnPreferenciasJuego.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (loginManager.hayJugadorLogueado()) {
                    game.setScreen(new PreferenciaJuego(game, loginManager));
                } else {
                    mostrarMensaje(idioma.get("msg.debesIniciarSesionParaVerPerfil"));
                }
            }
        });
        
        btnCerrarSesion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loginManager.logout();
                game.setScreen(new MenuInicio(game));
            }
        });
    }
    
    private void actualizarTextos() {
        titleLabel.setText(idioma.get("menu.principal"));
        btnJugar.setText(idioma.get("btn.jugar"));
        btnMiPerfil.setText(idioma.get("btn.miPerfil"));
        btnPreferenciasJuego.setText(idioma.get("btn.preferenciasJuego"));
        btnCerrarSesion.setText(idioma.get("btn.cerrarSesion"));
    }
    
    private void mostrarMensaje(String mensaje) {
        Dialog dialog = new Dialog(idioma.get("dialog.aviso"), skin) {
            protected void result(Object object) {
                hide();
            }
        };
        dialog.text(mensaje);
        dialog.button(idioma.get("dialog.ok"), true);
        dialog.show(stage);
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
    
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    
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
