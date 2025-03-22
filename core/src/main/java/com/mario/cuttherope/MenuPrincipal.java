/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class MenuPrincipal implements Screen {

      private Stage stage;
    private Skin skin;
    private MainGame game;
    private ManejoUsuario loginManager;

    public MenuPrincipal(MainGame game, ManejoUsuario loginManager) {
        this.game = game;
        this.loginManager = loginManager;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        Label titleLabel = new Label("Menu Principal", skin);
        TextButton btnJugar = new TextButton("Jugar", skin);
        TextButton btnCerrarSesion = new TextButton("Cerrar Sesion", skin);
        table.add(titleLabel).pad(20).row();
        table.add(btnJugar).pad(10).row();
        table.add(btnCerrarSesion).pad(10).row();
        
        btnJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (loginManager.hayJugadorLogueado()) {
                    game.setScreen(new Nivel4(game, loginManager, 1));
                } else {
                    mostrarMensaje("Debes iniciar sesion para jugar.");
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

    private void mostrarMensaje(String mensaje) {
        Dialog dialog = new Dialog("Aviso", skin) {
            protected void result(Object object) {
                hide();
            }
        };
        dialog.text(mensaje);
        dialog.button("OK", true);
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
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
