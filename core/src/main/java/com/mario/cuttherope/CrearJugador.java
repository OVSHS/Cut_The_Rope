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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private Texture[] frames;
    private int currentFrame = 0;
    private float timeElapsed = 0;
    private SpriteBatch batch;

    // Referencia a la clase de idiomas
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

    public CrearJugador(MainGame game) {
        this.game = game;
        this.loginManager = new ManejoUsuario();
        
        batch = new SpriteBatch();
        loadFrames();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Obtenemos la instancia de idiomas
        idioma = Idiomas.getInstance();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Table formTable = new Table();

        // Labels y TextFields
        formTable.add(new Label(idioma.get("lbl.apodo"), skin)).pad(5);
        final TextField tfApodo = new TextField("", skin);
        formTable.add(tfApodo).width(150).row();

        formTable.add(new Label(idioma.get("lbl.nombreCompleto"), skin)).pad(5);
        final TextField tfNombreCompleto = new TextField("", skin);
        formTable.add(tfNombreCompleto).width(150).row();

        formTable.add(new Label(idioma.get("lbl.contrasena"), skin)).pad(5);
        final TextField tfContrasena = new TextField("", skin);
        tfContrasena.setPasswordMode(true);
        tfContrasena.setPasswordCharacter('*');
        formTable.add(tfContrasena).width(150).row();

        // Botones de registrar y cancelar
        TextButton btnRegistrar = new TextButton(idioma.get("btn.registrar"), skin);
        TextButton btnCancelar = new TextButton(idioma.get("btn.cancelar"), skin);
        formTable.add(btnRegistrar).pad(10);
        formTable.add(btnCancelar).pad(10);

        // Seccion para el avatar
        Table avatarTable = new Table();
        avatarImage = new Image(new Texture(Gdx.files.internal(defaultAvatarPath)));
        avatarTable.add(avatarImage).width(150).height(150).row();

        TextButton btnCambiarFoto = new TextButton(idioma.get("btn.cambiarFoto"), skin);
        avatarTable.add(btnCambiarFoto).padTop(10);

        // Agregamos las tablas a la pantalla
        mainTable.add(formTable).pad(10);
        mainTable.add(avatarTable).pad(10);

        // Listener para cambiar la foto
        btnCambiarFoto.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                seleccionarAvatar();
            }
        });

        // Listener para registrar
        btnRegistrar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String apodo = tfApodo.getText().trim();
                String nombreCompleto = tfNombreCompleto.getText().trim();
                String contrasena = tfContrasena.getText().trim();

                if (apodo.isEmpty() || nombreCompleto.isEmpty() || contrasena.isEmpty()) {
                    mostrarMensaje(idioma.get("msg.camposObligatorios"));
                    return;
                }

                boolean registrado = loginManager.registerJugador(
                        apodo,
                        contrasena,
                        nombreCompleto,
                        (selectedAvatar != null ? selectedAvatar.path() : defaultAvatarPath)
                );

                if (registrado) {
                    mostrarMensaje(idioma.get("msg.usuarioRegistrado"));
                    game.setScreen(new MenuInicio(game));
                } else {
                    mostrarMensaje(idioma.get("msg.usuarioExiste"));
                }
            }
        });

        // Listener para cancelar
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

    private void mostrarMensaje(String texto) {
        Dialog dialog = new Dialog(idioma.get("dialog.aviso"), skin) {
            @Override
            protected void result(Object object) {
                hide();
            }
        };
        dialog.text(texto);
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
        for (Texture frame : frames) {
            frame.dispose();
        }
        batch.dispose();
    }
}
