/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import java.io.File;

public class CrearJugador implements Screen {

    private MainGame game;
    private Stage stage;
    private Skin skin;
    private ManejoUsuario loginManager;
    private Image avatarImage;
    private FileHandle selectedAvatar;
    private final String defaultAvatarPath = "FotoPrede.png";

    public CrearJugador(MainGame game) {
        this.game = game;
        this.loginManager = new ManejoUsuario();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Table formTable = new Table();
        formTable.add(new Label("Apodo:", skin)).pad(5);
        final TextField tfApodo = new TextField("", skin);
        formTable.add(tfApodo).width(150).row();

        formTable.add(new Label("NombreCompleto:", skin)).pad(5);
        final TextField tfNombreCompleto = new TextField("", skin);
        formTable.add(tfNombreCompleto).width(150).row();

        formTable.add(new Label("Contrasena:", skin)).pad(5);
        final TextField tfContrasena = new TextField("", skin);
        tfContrasena.setPasswordMode(true);
        tfContrasena.setPasswordCharacter('*');
        formTable.add(tfContrasena).width(150).row();

        TextButton btnRegistrar = new TextButton("Registrar", skin);
        TextButton btnCancelar = new TextButton("Cancelar", skin);
        formTable.add(btnRegistrar).pad(10);
        formTable.add(btnCancelar).pad(10);

        Table avatarTable = new Table();
        avatarImage = new Image(new Texture(Gdx.files.internal(defaultAvatarPath)));
        avatarTable.add(avatarImage).width(150).height(150).row();
        TextButton btnCambiarFoto = new TextButton("Cambiar Foto", skin);
        avatarTable.add(btnCambiarFoto).padTop(10);

        mainTable.add(formTable).pad(10);
        mainTable.add(avatarTable).pad(10);

        btnCambiarFoto.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                seleccionarAvatar();
            }
        });

        btnRegistrar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String apodo = tfApodo.getText().trim();
                String nombreCompleto = tfNombreCompleto.getText().trim();
                String contrasena = tfContrasena.getText().trim();

                if (apodo.isEmpty() || nombreCompleto.isEmpty() || contrasena.isEmpty()) {
                    mostrarMensaje("Todos los campos son obligatorios.");
                    return;
                }

                boolean registrado = loginManager.registerJugador(
                        apodo,
                        contrasena,
                        nombreCompleto,
                        (selectedAvatar != null ? selectedAvatar.path() : defaultAvatarPath)
                );

                if (registrado) {
                    mostrarMensaje("Usuario registrado exitosamente.");
                    game.setScreen(new MenuInicio(game));
                } else {
                    mostrarMensaje("El usuario ya existe.");
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

    private void seleccionarAvatar() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                File assetsDir = new File(Gdx.files.internal("assets").file().getAbsolutePath());
                JFileChooser fileChooser = new JFileChooser(assetsDir);
                fileChooser.setCurrentDirectory(assetsDir);
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    final FileHandle selected = Gdx.files.absolute(file.getAbsolutePath());
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            selectedAvatar = selected;
                            avatarImage.setDrawable(new TextureRegionDrawable(new Texture(selectedAvatar)));
                        }
                    });
                }
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
    public void hide() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
