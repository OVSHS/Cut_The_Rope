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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.Locale;

public class PreferenciaJuego implements Screen {

    private MainGame game;
    private SpriteBatch batch;
    private ManejoUsuario manejoUsuario;
    private Stage stage;
    private Skin skin;
    private Slider sliderVolumen;
    private Idiomas idioma;
    private Label titulo;
    private Label lblVol;
    private Label lblSeleccioneIdioma;
    private TextButton btnRegresar;
    private TextButton btnEnglish;
    private TextButton btnSpanish;
    private Texture[] frames;
    private int currentFrame = 0;
    private float timeElapsed = 0;

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

    public PreferenciaJuego(MainGame game, ManejoUsuario manejoUsuario) {
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
        table.center();
        table.defaults().pad(5);
        stage.addActor(table);

        titulo = new Label(idioma.get("titulo.preferenciasJuego"), skin);
        table.add(titulo).colspan(2).pad(10).center();
        table.row();

        lblVol = new Label(idioma.get("lbl.volumen"), skin);
        sliderVolumen = new Slider(0f, 1f, 0.1f, false, skin);

        PerfilUsuario perfil = manejoUsuario.getPerfilUsuarioActual();
        if (perfil != null) {
            sliderVolumen.setValue(perfil.getVolumen());
            AudioManager.getInstance().setVolume(perfil.getVolumen());
        }

        sliderVolumen.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float nuevoVolumen = sliderVolumen.getValue();
                AudioManager.getInstance().setVolume(nuevoVolumen);

                if (perfil != null) {
                    perfil.setVolumen(nuevoVolumen);
                    manejoUsuario.actualizarPerfil(perfil);
                }
            }
        });

        table.add(lblVol).pad(5).right();
        table.add(sliderVolumen).width(200).pad(5).left();
        table.row();

        lblSeleccioneIdioma = new Label(idioma.get("lbl.seleccioneIdioma"), skin);
        table.add(lblSeleccioneIdioma).colspan(2).center();
        table.row();

        btnEnglish = new TextButton("EN", skin);
        btnSpanish = new TextButton("ES", skin);

        Table langTable = new Table();
        langTable.add(btnEnglish).padRight(10);
        langTable.add(btnSpanish);

        table.add(langTable).colspan(2).center();
        table.row();

        btnRegresar = new TextButton(idioma.get("btn.regresar"), skin);
        btnRegresar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuPrincipal(game, manejoUsuario));
            }
        });
        table.add(btnRegresar).colspan(2).padTop(20).center();

        btnEnglish.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                idioma.setLocale(new Locale("en"));
                actualizarTextos();
            }
        });

        btnSpanish.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                idioma.setLocale(new Locale("es"));
                actualizarTextos();
            }
        });
    }

    private void actualizarTextos() {
        titulo.setText(idioma.get("titulo.preferenciasJuego"));
        lblVol.setText(idioma.get("lbl.volumen"));
        lblSeleccioneIdioma.setText(idioma.get("lbl.seleccioneIdioma"));
        btnRegresar.setText(idioma.get("btn.regresar"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        float time = Gdx.graphics.getFrameId() % 300 / 300f;
        float r = 0.76f + 0.04f * (float) Math.sin(time * 6.28f);
        float g = 0.67f + 0.04f * (float) Math.cos(time * 6.28f);
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
        for (Texture frame : frames) {
            frame.dispose();
        }
        batch.dispose();
        stage.dispose();
        stage.dispose();
        skin.dispose();
    }
}
