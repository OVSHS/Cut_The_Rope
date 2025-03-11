/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

/**
 *
 * @author Mario
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class IniciarSesion implements Screen {

    private MainGame game;
    private Stage stage;
    private Skin skin;
    private ManejoUsuario loginManager;

    public IniciarSesion(MainGame game) {
        this.game = game;
        loginManager = new ManejoUsuario();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label lblTitulo = new Label("Iniciar Sesion", skin);
        Label lblUsuario = new Label("Usuario:", skin);
        final TextField tfUsuario = new TextField("", skin);
        Label lblContrasena = new Label("Contrasena:", skin);
        final TextField tfContrasena = new TextField("", skin);
        tfContrasena.setPasswordMode(true);
        tfContrasena.setPasswordCharacter('*');

        TextButton btnAceptar = new TextButton("Aceptar", skin);
        TextButton btnCancelar = new TextButton("Cancelar", skin);

        table.add(lblTitulo).colspan(2).pad(10).row();
        table.add(lblUsuario).pad(5);
        table.add(tfUsuario).width(150).pad(5).row();
        table.add(lblContrasena).pad(5);
        table.add(tfContrasena).width(150).pad(5).row();
        table.add(btnAceptar).pad(10);
        table.add(btnCancelar).pad(10);

        btnAceptar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String usuario = tfUsuario.getText();
                String contrasena = tfContrasena.getText();
                if (usuario.isEmpty() || contrasena.isEmpty()) {
                    mostrarMensaje("Campos vacios");
                    return;
                }
                if (loginManager.login(usuario, contrasena)) {
                    mostrarMensaje("Inicio de sesion exitoso");
                    game.setScreen(new MenuPrincipal(game, loginManager));
                } else {
                    mostrarMensaje("Usuario o contrasena incorrectos");
                }
            }
        });

        btnCancelar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuInicio(game));
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        Dialog dialog = new Dialog("Aviso", skin) {
            @Override
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
        skin.dispose();
    }

}
