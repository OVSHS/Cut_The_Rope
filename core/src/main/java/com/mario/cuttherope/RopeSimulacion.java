package com.mario.cuttherope;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.ArrayList;
import java.util.List;

public class RopeSimulacion extends Juego {

    protected World mundo;
    protected Box2DDebugRenderer depurador;
    protected OrthographicCamera camara;
    protected ShapeRenderer renderizadorFiguras;
    protected SpriteBatch loteSprites;
    protected Stage escenario;
    protected Skin piel;
    protected Texture omNomCerrado;
    protected Texture omNomAbierto;
    protected Texture texturaDulce;

    // Constantes para la cuerda
    protected final float RADIO_DULCE = 0.1f;
    protected final float LONGITUD_SEGMENTO = 0.2f;
    protected final float ANCHO_SEGMENTO = 0.05f;
    protected final int NUMERO_SEGMENTOS = 10;

    // Lista de cuerdas que se usarán en el nivel
    protected List<Cuerda> cuerdas;
    protected Vector3 ultimaPosRaton = new Vector3();
    protected boolean cortando;
    protected boolean juegoCompleto;
    protected boolean dialogoMostrado;

    /**
     * Constructor de RopeSimulacion.
     *
     * @param mainGame referencia al juego principal.
     * @param loginManager referencia al manejo de usuario.
     * @param nivel número de nivel (1 para una cuerda; 2 para múltiples
     * cuerdas, por ejemplo).
     */
    public RopeSimulacion(MainGame mainGame, ManejoUsuario loginManager, int nivel) {
        super(mainGame, loginManager);
        // Inicialización del mundo y objetos de Box2D
        mundo = new World(new Vector2(0, -9.8f), true);
        depurador = new Box2DDebugRenderer();
        camara = new OrthographicCamera(10, 10);
        camara.position.set(5, 5, 0);
        camara.update();
        renderizadorFiguras = new ShapeRenderer();
        loteSprites = new SpriteBatch();
        escenario = new Stage(new ScreenViewport());
        piel = new Skin(Gdx.files.internal("uiskin.json"));
        omNomCerrado = new Texture(Gdx.files.internal("omnom.png"));
        omNomAbierto = new Texture(Gdx.files.internal("omnomhambre.png"));
        texturaDulce = new Texture(Gdx.files.internal("calabaza.png"));

        cuerdas = new ArrayList<Cuerda>();

        // Solo para nivel 1 se crea la cuerda automáticamente.
        if (nivel == 1) {
            cuerdas.add(new Cuerda(mundo, 5, 9, NUMERO_SEGMENTOS, LONGITUD_SEGMENTO, ANCHO_SEGMENTO, RADIO_DULCE));
        }

        // Configuración de entrada para el corte de cuerda.
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                cortando = true;
                ultimaPosRaton.set(screenX, screenY, 0);
                camara.unproject(ultimaPosRaton);
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (!cortando) {
                    return false;
                }
                Vector3 nuevaPos = new Vector3(screenX, screenY, 0);
                camara.unproject(nuevaPos);
                // Se recorre cada cuerda para evaluar el corte.
                for (Cuerda cuerda : cuerdas) {
                    cuerda.cortarCuerda(ultimaPosRaton, nuevaPos);
                }
                ultimaPosRaton.set(nuevaPos);
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                cortando = false;
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
        mundo.step(delta, 8, 3);
        // Se puede dibujar el depurador para visualizar las físicas
        depurador.render(mundo, camara.combined);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (renderizadorFiguras != null) {
            renderizadorFiguras.dispose();
        }
        if (loteSprites != null) {
            loteSprites.dispose();
        }
        if (omNomCerrado != null) {
            omNomCerrado.dispose();
        }
        if (omNomAbierto != null) {
            omNomAbierto.dispose();
        }
        if (texturaDulce != null) {
            texturaDulce.dispose();
        }
        if (escenario != null) {
            escenario.dispose();
        }

        // Liberar los recursos de cada cuerda creada
        for (Cuerda cuerda : cuerdas) {
            cuerda.dispose();
        }

        if (mundo != null) {
            mundo.dispose();
        }
        if (depurador != null) {
            depurador.dispose();
        }
    }
}
