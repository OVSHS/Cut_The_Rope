package com.mario.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.ArrayList;

/**
 *
 * @author Maria Gabriela
 */
public class Nivel5 extends Juego implements InputProcessor {

    private Stage stage;
    private ManejoUsuario loginManager;
    private MainGame game;
    private int numeroNivel;
    private boolean juegoTerminado = false;
    private World mundo;
    private OrthographicCamera camara;
    private OrthographicCamera hudCamera;
    private SpriteBatch batchJuego;
    private Body cuerpoOmNom;
    private Body cuerpoDulce;
    private ShapeRenderer shapeRenderer;
    private ArrayList<Cuerda> listaCuerdas = new ArrayList<>();
    private ArrayList<StarObject> listaEstrellas = new ArrayList<>();
    private ArrayList<Body> listaSpikes = new ArrayList<>(); // Lista para almacenar los cuerpos de los spikes
    private RopeSimulacion ropeSimulacion;
    private Idiomas idioma;
    private Box2DDebugRenderer debugRenderer;
    private long tiempoInicioNivel;
    private long tiempoJugadoNivel;
    private boolean nivelCompletado = false;
    private Texture spikeTexture;

    private final float ANCHO_MUNDO = 20f;
    private final float ALTO_MUNDO = 30f;

    public Nivel5(MainGame mainGame, ManejoUsuario loginManager, int nivel) {
        super(mainGame, loginManager);
        this.game = mainGame;
        this.loginManager = loginManager;
        this.numeroNivel = nivel;
    }

    @Override
    public void show() {
        super.show();

        tiempoInicioNivel = System.currentTimeMillis() / 1000;
        tiempoJugadoNivel = 0;
        nivelCompletado = false;

        idioma = Idiomas.getInstance();
        mundo = new World(new Vector2(0, -9.8f), true);
        stage = new Stage(new ScreenViewport());
        camara = new OrthographicCamera(ANCHO_MUNDO, ALTO_MUNDO);
        camara.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);
        camara.update();
        batchJuego = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();

        // Creación de cuerpos
        cuerpoOmNom = crearCuerpoOmNom(new Vector2(ANCHO_MUNDO / 2f, 4f)); // Om Nom en la parte inferior
        cuerpoDulce = crearCuerpoDulce(new Vector2(ANCHO_MUNDO / 2f, ALTO_MUNDO - 10f));

        Body anclajemedio = crearAnclaje(new Vector2(ANCHO_MUNDO / 2f - 2f, 19f));
        Body anclajeabajo = crearAnclaje(new Vector2(ANCHO_MUNDO / 2f - 2f, 12f));
        Body anclajearriba = crearAnclaje(new Vector2(ANCHO_MUNDO / 2f - 2f, 24f));
        Body anclajederecha = crearAnclaje(new Vector2(ANCHO_MUNDO / 2f + 4f, 24f));

        Cuerda cuerdamedio = new Cuerda(anclajemedio, cuerpoDulce, mundo);
        Cuerda cuerdaabajo = new Cuerda(anclajeabajo, cuerpoDulce, mundo);
        Cuerda cuerdaarriba = new Cuerda(anclajearriba, cuerpoDulce, mundo);
        Cuerda cuerdaderecha = new Cuerda(anclajederecha, cuerpoDulce, mundo);

        cuerdamedio.setLongitud(6f);
        cuerdaabajo.setLongitud(5.5f);
        cuerdaarriba.setLongitud(5f);
        cuerdaderecha.setLongitud(1.8f);

        listaCuerdas.add(cuerdamedio);
        listaCuerdas.add(cuerdaabajo);
        listaCuerdas.add(cuerdaarriba);
        listaCuerdas.add(cuerdaderecha);

        // Crear estrellas (posicionadas como en la imagen)
        listaEstrellas.add(crearEstrella(new Vector2(12f, 12f))); // Estrella izquierda
        listaEstrellas.add(crearEstrella(new Vector2(5f, 12f))); // Estrella derecha
        listaEstrellas.add(crearEstrella(new Vector2(5f, 18f))); // Estrella derecha

        // Crear spikes (como se ve en la imagen)
        // Primera fila de spikes
        for (int i = 0; i < 2; i++) {
            Body spike = crearSpike(new Vector2(8f + i * 2f, 15f));
            listaSpikes.add(spike);
        }

        // Segunda fila de spikes
        for (int i = 0; i < 2; i++) {
            Body spike = crearSpike(new Vector2(8f + i * 2f, 8f));
            listaSpikes.add(spike);
        }

        // Inicializar la simulación de cuerdas
        ropeSimulacion = new RopeSimulacion(game, loginManager, numeroNivel, mundo);
        ropeSimulacion.inicializarElementos(cuerpoOmNom, cuerpoDulce, listaEstrellas, listaCuerdas, mundo);
        spikeTexture = new Texture(Gdx.files.internal("spikes.png"));

        juegoTerminado = false;
        mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                // Detectar colisión entre el dulce y Om Nom
                if ((bodyA.getUserData() != null && bodyA.getUserData().equals("dulce")
                        && bodyB.getUserData() != null && bodyB.getUserData().equals("omnom"))
                        || (bodyB.getUserData() != null && bodyB.getUserData().equals("dulce")
                        && bodyA.getUserData() != null && bodyA.getUserData().equals("omnom"))) {
                    if (!juegoTerminado) {
                        juegoTerminado = true;
                        Gdx.app.postRunnable(() -> {
                            if (cuerpoDulce != null) {
                                mundo.destroyBody(cuerpoDulce);
                                cuerpoDulce = null;
                            }
                            ropeSimulacion.setDulceComido(true);

                            // Verificar si no se recolectó ninguna estrella
                            if (ropeSimulacion.getEstrellasRecogidas() == 0) {
                                mostrarDialogoFallo(); // Mostrar diálogo de fallo
                            } else {
                                mostrarDialogoFelicidades(ropeSimulacion.getEstrellasRecogidas()); // Mostrar diálogo de éxito
                            }
                        });
                    }
                }

                // Detectar colisión entre el dulce y los spikes
                if ((bodyA.getUserData() != null && bodyA.getUserData().equals("dulce")
                        && bodyB.getUserData() != null && bodyB.getUserData().equals("spike"))
                        || (bodyB.getUserData() != null && bodyB.getUserData().equals("dulce")
                        && bodyA.getUserData() != null && bodyA.getUserData().equals("spike"))) {
                    if (!juegoTerminado) {
                        juegoTerminado = true;
                        Gdx.app.postRunnable(() -> {
                            if (cuerpoDulce != null) {
                                mundo.destroyBody(cuerpoDulce);
                                cuerpoDulce = null;
                            }
                            mostrarDialogoFallo();
                        });
                    }
                }
            }

            @Override
            public void endContact(Contact cntct) {
            }

            @Override
            public void preSolve(Contact cntct, Manifold mnfld) {
            }

            @Override
            public void postSolve(Contact cntct, ContactImpulse ci) {
            }
        });

        // Botón para volver al mapa
        Texture backButtonTexture = new Texture("back_button.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(backButtonTexture));
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = drawable;
        textButtonStyle.down = drawable;
        textButtonStyle.font = new BitmapFont();

        TextButton botonMapa = new TextButton(idioma.get("btn.mapa"), textButtonStyle);
        botonMapa.setSize(100, 50);
        botonMapa.setPosition(10, 10);
        botonMapa.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuNiveles(game, loginManager));
            }
        });
        stage.addActor(botonMapa);

        // Configurar el procesador de entrada
        InputMultiplexer multiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void renderizarSpikes(SpriteBatch batch) {
        if (listaSpikes != null && spikeTexture != null) {
            for (Body spike : listaSpikes) {
                Vector2 pos = spike.getPosition();
                float size = 0.5f; // Match the size used in crearSpike

                // Draw the spike texture
                batch.draw(spikeTexture,
                        pos.x - size,
                        pos.y - size / 2,
                        size * 2, // width
                        size * 1.5f); // height
            }
        }
    }

    @Override
    public void render(float delta) {
        if (!nivelCompletado) {
            tiempoJugadoNivel = (System.currentTimeMillis() / 1000) - tiempoInicioNivel;
        }

        // Actualizar la física
        mundo.step(delta, 6, 2);

        // Limpiar la pantalla
        Gdx.gl.glClearColor(0.76f, 0.67f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar debug de física si es necesario
        debugRenderer.setDrawBodies(false);
        debugRenderer.render(mundo, camara.combined);
        debugRenderer.setDrawBodies(true);

        // Actualizar y dibujar la stage (UI)
        stage.act(delta);
        stage.draw();

        // Actualizar la simulación de la cuerda
        ropeSimulacion.actualizar(delta);

        // Dibujar los elementos del juego
        batchJuego.setProjectionMatrix(camara.combined);
        batchJuego.begin();
        // Dibujar elementos del juego
        ropeSimulacion.render(batchJuego);
        renderizarSpikes(batchJuego);

        // Dibujar spikes aquí si no están en la simulación
        // renderSpikes(batchJuego);
        batchJuego.end();

        try {
            if (hudCamera == null) {
                hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
            hudCamera.update();
            batchJuego.setProjectionMatrix(hudCamera.combined);
            batchJuego.begin();
            ropeSimulacion.renderHUD(batchJuego, (int) hudCamera.viewportWidth, (int) hudCamera.viewportHeight);
            batchJuego.end();
        } catch (Exception e) {
            Gdx.app.error("error", "Error al renderizar HUD", e);
        }

        // Verificar si el dulce cayó fuera de la pantalla
        if (!juegoTerminado && cuerpoDulce != null && cuerpoDulce.getPosition().y < -5) {
            juegoTerminado = true;
            mostrarDialogoFallo();
        }
    }

    private void renderizarSpikes(ShapeRenderer shapeRenderer) {
        // Dibujar los spikes como triángulos
        shapeRenderer.setColor(0.7f, 0.7f, 0.7f, 1f); // Color gris para los spikes

        for (Body spikeBody : listaSpikes) {
            Vector2 pos = spikeBody.getPosition();
            float size = 0.5f; // Tamaño de base del spike

            // Dibujar el triángulo del spike
            shapeRenderer.triangle(
                    pos.x - size, pos.y - size / 2, // Punto inferior izquierdo
                    pos.x + size, pos.y - size / 2, // Punto inferior derecho
                    pos.x, pos.y + size // Punto superior
            );
        }
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
        dispose();
    }

    @Override
    public void dispose() {
        if (spikeTexture != null) {
            spikeTexture.dispose();
        }

        stage.dispose();
        ropeSimulacion.dispose();
        mundo.dispose();
        batchJuego.dispose();
        debugRenderer.dispose();
    }

    // Métodos de creación de objetos
    private Body crearCuerpoOmNom(Vector2 pos) {
        Pieza p = new Pieza(1f, BodyDef.BodyType.StaticBody);
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.position.set(pos);
        Body body = mundo.createBody(bd);
        FixtureDef fd = new FixtureDef();
        fd.shape = p.forma;
        fd.isSensor = true;
        body.createFixture(fd);
        body.setUserData("omnom");
        p.forma.dispose();
        return body;
    }

    private Body crearCuerpoDulce(Vector2 pos) {
        Pieza p = new Pieza(0.6f, BodyDef.BodyType.DynamicBody);
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(pos);
        bd.linearDamping = 0.5f;
        Body body = mundo.createBody(bd);
        FixtureDef fd = new FixtureDef();
        fd.shape = p.forma;
        fd.density = 2f;
        fd.friction = 0.2f;
        fd.restitution = 0.1f;
        fd.filter.groupIndex = -1;
        body.createFixture(fd);
        body.setUserData("dulce");
        p.forma.dispose();
        return body;
    }

    private Body crearAnclaje(Vector2 pos) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.position.set(pos);
        Body body = mundo.createBody(bd);
        CircleShape shape = new CircleShape();
        shape.setRadius(0.2f);
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.isSensor = true;
        body.createFixture(fd);
        shape.dispose();
        body.setUserData("anclaje");
        return body;
    }

    private Body crearSpike(Vector2 pos) {
        // Crear un cuerpo de spike triangular
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.position.set(pos);
        Body body = mundo.createBody(bd);

        // Definir la forma triangular del spike
        com.badlogic.gdx.physics.box2d.PolygonShape shape = new com.badlogic.gdx.physics.box2d.PolygonShape();
        Vector2[] vertices = new Vector2[3];
        float size = 0.5f; // Tamaño de base del spike

        vertices[0] = new Vector2(-size, -size / 2);  // Inferior izquierda
        vertices[1] = new Vector2(size, -size / 2);   // Inferior derecha
        vertices[2] = new Vector2(0, size);         // Superior centro

        shape.set(vertices);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.isSensor = true; // Lo hacemos sensor para detectar la colisión pero sin afectar físicamente
        body.createFixture(fd);
        body.setUserData("spike");
        shape.dispose();

        return body;
    }

    private StarObject crearEstrella(Vector2 pos) {
        Pieza p = new Pieza(0.4f, BodyDef.BodyType.KinematicBody);
        p.esSensor = true;
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.KinematicBody;
        bd.position.set(pos);
        Body body = mundo.createBody(bd);
        FixtureDef fd = new FixtureDef();
        fd.shape = p.forma;
        fd.isSensor = true;
        body.createFixture(fd);
        p.forma.dispose();
        return new StarObject(p, body);
    }

    // Métodos para mostrar diálogos
    private void mostrarDialogoFelicidades(int estrellasRecolectadas) {
        if (nivelCompletado) {
            return;
        }
        nivelCompletado = true;

        PerfilUsuario perfil = loginManager.getPerfilUsuarioActual();
        if (perfil != null) {
            perfil.addTiempoJugado(tiempoJugadoNivel);
            perfil.addCantEstrellas(estrellasRecolectadas);

            // Solo desbloquear si es el nivel actual (no uno ya superado)
            loginManager.completarNivel(numeroNivel);

            loginManager.actualizarPerfil(perfil);
            nivelCompletado(numeroNivel, estrellasRecolectadas);
        }

        Dialog dialog = new Dialog(idioma.get("dialog.felicidadesTitulo"),
                new Skin(Gdx.files.internal("uiskin.json"))) {
            @Override
            protected void result(Object object) {
                game.setScreen(new MenuNiveles(game, loginManager));
            }
        };
        dialog.text(idioma.get("dialog.felicidadesTexto") + "\nEstrellas recolectadas: " + estrellasRecolectadas);
        dialog.button(idioma.get("btn.aceptar"), true);
        dialog.show(stage);
    }

    private void mostrarDialogoFallo() {
        Dialog dialog = new Dialog(idioma.get("dialog.nivelTerminadoTitulo"),
                new Skin(Gdx.files.internal("uiskin.json"))) {
            @Override
            protected void result(Object object) {
                boolean volverAlMenu = (boolean) object;
                if (volverAlMenu) {
                    game.setScreen(new MenuNiveles(game, loginManager));
                } else {
                    game.setScreen(new Nivel5(game, loginManager, numeroNivel));
                }
            }
        };
        dialog.text(idioma.get("dialog.nivelTerminadoTexto"));
        dialog.button(idioma.get("btn.volverAlMapa"), true);
        dialog.button(idioma.get("btn.repetirNivel"), false);
        dialog.show(stage);
    }

    // Métodos del InputProcessor para manejar eventos de entrada
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Convertir coordenadas de pantalla a coordenadas de mundo
        Vector3 tmp = new Vector3(screenX, screenY, 0);
        camara.unproject(tmp);
        // Intentar cortar la cuerda donde el usuario arrastró el dedo
        ropeSimulacion.intentarCortarCuerda(new Vector2(tmp.x, tmp.y));
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public void nivelCompletado(int nivel, int estrellas) {
        // Registrar la partida en el historial de logs
        loginManager.registrarPartida(nivel, estrellas);

    }
}
