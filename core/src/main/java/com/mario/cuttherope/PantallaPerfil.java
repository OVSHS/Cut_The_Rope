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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public class PantallaPerfil implements Screen {

    private MainGame game;
    private ManejoUsuario manejoUsuario;
    private Stage stage;
    private Skin skin;
    private Image avatarImage;
    private FileHandle selectedAvatar;
    private Idiomas idioma;
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

    public PantallaPerfil(MainGame game, ManejoUsuario manejoUsuario) {
        this.game = game;
        this.manejoUsuario = manejoUsuario;
        batch = new SpriteBatch();
        loadFrames();
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        idioma = Idiomas.getInstance();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        PerfilUsuario perfil = manejoUsuario.getPerfilUsuarioActual();
        if (perfil == null) {
            game.setScreen(new MenuPrincipal(game, manejoUsuario));
            return;
        }

        Label lblTitulo = new Label(idioma.get("lbl.tituloPerfil"), skin);
        table.add(lblTitulo).colspan(2).pad(10).center();
        table.row();

        table.add(new Label(idioma.get("lbl.usuario"), skin)).pad(5).right();
        table.add(new Label(perfil.getApodo(), skin)).pad(5).left();
        table.row();

        table.add(new Label(idioma.get("lbl.nombreCompleto"), skin)).pad(5).right();
        table.add(new Label(perfil.getNombreCompleto(), skin)).pad(5).left();
        table.row();

        table.add(new Label(idioma.get("lbl.fechaRegistro"), skin)).pad(5).right();
        table.add(new Label(formatoFecha(perfil.getFechaRegistro()), skin)).pad(5).left();
        table.row();

        table.add(new Label(idioma.get("lbl.ultimaSesion"), skin)).pad(5).right();
        table.add(new Label(formatoFecha(perfil.getUltimaSesion()), skin)).pad(5).left();
        table.row();
        try {
            table.add(new Label(idioma.get("lbl.tiempo"), skin)).pad(5).right();
            table.add(new Label(perfil.getTiempoFormateado(), skin)).pad(5).left();
            table.row();

            // Fixed code for stars display - removed extra parenthesis
            table.add(new Label(idioma.get("lbl.estrellas"), skin)).pad(5).right();
            table.add(new Label(String.valueOf(perfil.getCantEstrellas()), skin)).pad(5).left();
            table.row();

            // Fixed code for level info - removed extra parenthesis
            table.add(new Label(idioma.get("lbl.nivel"), skin)).pad(5).right();
            table.add(new Label(String.valueOf(perfil.getNivelDesbloqueado()), skin)).pad(5).left();
            table.row();
        } catch (Exception e) {
            System.out.println("Error al mostrar estadísticas: " + e.getMessage());
        }

        avatarImage = new Image();
        mostrarAvatar(perfil.getRutaAvatar());
        table.add(new Label(idioma.get("lbl.avatar"), skin)).pad(5).right();
        table.add(avatarImage).pad(5).width(100).height(100).left();
        table.row();

        // Botón para Cambiar Foto 
        TextButton btnCambiarFoto = new TextButton(idioma.get("btn.cambiarFoto"), skin);
        btnCambiarFoto.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                seleccionarAvatar(perfil);
            }
        });
        table.add(btnCambiarFoto).colspan(2).pad(5).center();
        table.row();

        // Botón para Eliminar Cuenta 
        TextButton btnEliminarCuenta = new TextButton(idioma.get("btn.eliminarCuenta"), skin);
        btnEliminarCuenta.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Mostrar dialogo de confirmacion
                Dialog confirmDialog = new Dialog(idioma.get("dialog.confirmarEliminarTitulo"), skin) {
                    @Override
                    protected void result(Object object) {
                        boolean confirmed = (Boolean) object;
                        if (confirmed) {
                            eliminarCuenta(perfil);
                            game.setScreen(new MenuInicio(game));
                        }
                    }
                };
                confirmDialog.text(idioma.get("dialog.confirmarEliminarTexto"));
                confirmDialog.button(idioma.get("dialog.si"), true);
                confirmDialog.button(idioma.get("dialog.no"), false);
                confirmDialog.show(stage);
            }
        });
        table.add(btnEliminarCuenta).colspan(2).pad(5).center();
        table.row();

        // Botón para Regresar
        TextButton btnRegresar = new TextButton(idioma.get("btn.regresar"), skin);
        btnRegresar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float xx, float yy) {
                game.setScreen(new MenuPrincipal(game, manejoUsuario));
            }
        });
        table.add(btnRegresar).colspan(2).padTop(20).center();
    }

    private void eliminarCuenta(PerfilUsuario perfil) {
        // Obtener la carpeta del usuario y eliminarla recursivamente
        FileHandle folder = Gdx.files.local("usuario/" + perfil.getApodo());
        if (folder.exists()) {
            folder.deleteDirectory();
        }

    }

    private void seleccionarAvatar(PerfilUsuario perfil) {
        SwingUtilities.invokeLater(() -> {
            File assetsDir = new File(Gdx.files.internal("assets").file().getAbsolutePath());
            JFileChooser fileChooser = new JFileChooser(assetsDir);
            fileChooser.setCurrentDirectory(assetsDir);
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                selectedAvatar = Gdx.files.absolute(file.getAbsolutePath());
                Gdx.app.postRunnable(() -> {
                    // Actualizar el avatar en pantalla
                    avatarImage.setDrawable(new TextureRegionDrawable(new Texture(selectedAvatar)));
                    // Actualizar la ruta en el perfil
                    perfil.setRutaAvatar(selectedAvatar.path());
                    // Guardar el cambio en el perfil
                    guardarCambioAvatar(perfil);
                    mostrarMensaje(idioma.get("msg.avatarActualizado"));
                });
            }
        });
    }

    private void guardarCambioAvatar(PerfilUsuario perfil) {
        String apodo = perfil.getApodo();
        FileHandle folder = Gdx.files.local("usuario/" + apodo);
        folder.mkdirs();
        FileHandle datosBin = folder.child("datos.bin");
        manejoUsuario.saveUserData(datosBin, perfil);
    }

    private void mostrarAvatar(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            avatarImage.setDrawable(new TextureRegionDrawable(new Texture(Gdx.files.internal("FotoPrede.png"))));
            return;
        }
        try {
            Texture tex = new Texture(Gdx.files.absolute(ruta));
            avatarImage.setDrawable(new TextureRegionDrawable(tex));
        } catch (Exception e) {
            avatarImage.setDrawable(new TextureRegionDrawable(new Texture(Gdx.files.internal("FotoPrede.png"))));
        }
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

    private String formatoFecha(long fechaMillis) {
        if (fechaMillis <= 0) {
            return "-";
        }
        Date date = new Date(fechaMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(date);
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
        if (timeElapsed > 0.225f) { // Cambia de frame cada 0.225 seg
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
