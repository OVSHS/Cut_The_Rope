//Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
//  Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
package com.mario.cuttherope;
// 
//  @author Mario

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
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.ArrayList;

public class Nivel2 extends Juego implements InputProcessor {

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
    private RopeSimulacion ropeSimulacion;
    private Idiomas idioma;
    private Box2DDebugRenderer debugRenderer;
    private final float ANCHO_MUNDO = 20f;
    private final float ALTO_MUNDO = 30f;
    private long tiempoInicioNivel;
    private long tiempoJugadoNivel;
    private boolean nivelCompletado = false;

    public Nivel2(MainGame mainGame, ManejoUsuario loginManager, int nivel) {
        super(mainGame, loginManager);
        this.game = mainGame;
        this.loginManager = loginManager;
        this.numeroNivel = nivel;
    }

    @Override
    public void show() {
        // Llama al show() de la clase abstracta Juego para iniciar el hilo de eventos, etc.
        super.show();
        idioma = Idiomas.getInstance();
        mundo = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();
        stage = new Stage(new ScreenViewport());
        camara = new OrthographicCamera(ANCHO_MUNDO, ALTO_MUNDO);
        camara.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);
        camara.update();
        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batchJuego = new SpriteBatch();
        
        tiempoInicioNivel = System.currentTimeMillis() / 1000;
        tiempoJugadoNivel = 0;
        nivelCompletado = false;


        cuerpoOmNom = crearCuerpoOmNom(new Vector2(ANCHO_MUNDO / 2f, 3f));
        cuerpoDulce = crearCuerpoDulce(new Vector2(ANCHO_MUNDO / 2f, ALTO_MUNDO - 5f));

        // Crear 3 anclajes para sujetar las 3 cuerdas
        Body anclaje1 = crearAnclaje(new Vector2(ANCHO_MUNDO / 2f - 7f, ALTO_MUNDO - 2f)); // Izquierda
        Body anclaje2 = crearAnclaje(new Vector2(ANCHO_MUNDO / 3f, ALTO_MUNDO - 2f));      // Centro
        Body anclaje3 = crearAnclaje(new Vector2(ANCHO_MUNDO / 2f + 2f, ALTO_MUNDO - 2f)); // Derecha

        Cuerda cuerda1 = new Cuerda(anclaje1, cuerpoDulce, mundo);
        Cuerda cuerda2 = new Cuerda(anclaje2, cuerpoDulce, mundo);
        Cuerda cuerda3 = new Cuerda(anclaje3, cuerpoDulce, mundo);
        cuerda1.setLongitud(5f);
        cuerda2.setLongitud(8f);
        listaCuerdas.add(cuerda1);
        listaCuerdas.add(cuerda2);
        listaCuerdas.add(cuerda3);

        listaEstrellas.add(crearEstrella(new Vector2(ANCHO_MUNDO / 3.5f, 21f)));
        listaEstrellas.add(crearEstrella(new Vector2(ANCHO_MUNDO / 2.5f, 17f)));
        listaEstrellas.add(crearEstrella(new Vector2(ANCHO_MUNDO / 2f, 11f)));

        ropeSimulacion = new RopeSimulacion(game, loginManager, numeroNivel, mundo);
        ropeSimulacion.inicializarElementos(cuerpoOmNom, cuerpoDulce, listaEstrellas, listaCuerdas, mundo);

        juegoTerminado = false;
        mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

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
                            mostrarDialogoFelicidades(ropeSimulacion.getEstrellasRecogidas());
                        });
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });

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

        InputMultiplexer multiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        mundo.step(delta, 6, 2);
        Gdx.gl.glClearColor(0.76f, 0.67f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.setDrawBodies(false);
        debugRenderer.render(mundo, camara.combined);
        debugRenderer.setDrawBodies(true);
        stage.act(delta);
        stage.draw();
        ropeSimulacion.actualizar(delta);
        batchJuego.setProjectionMatrix(camara.combined);
        batchJuego.begin();
        ropeSimulacion.render(batchJuego);
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
        if (!juegoTerminado && cuerpoDulce.getPosition().y < -5) {
            juegoTerminado = true;
            mostrarDialogoFallo();
        }
    }

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
        shape.setRadius(0.1f);
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.isSensor = true;
        body.createFixture(fd);
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

    private void mostrarDialogoFelicidades(int estrellasRecolectadas) {
    nivelCompletado = true;

    // Guardar el tiempo jugado y sumar las estrellas recolectadas
    PerfilUsuario perfil = loginManager.getPerfilUsuarioActual();
    if (perfil != null) {
        perfil.addTiempoJugado(tiempoJugadoNivel);
        perfil.addCantEstrellas(estrellasRecolectadas); // Sumar estrellas al total
        loginManager.actualizarPerfil(perfil); // Guardar el perfil actualizado
    }

    // Mostrar diálogo de felicitaciones
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
        Dialog dialog = new Dialog(idioma.get("dialog.nivelTerminadoTitulo"), new Skin(Gdx.files.internal("uiskin.json"))) {
            @Override
            protected void result(Object object) {
                boolean volverAlMenu = (boolean) object;
                if (volverAlMenu) {
                    game.setScreen(new MenuNiveles(game, loginManager));
                } else {
                    game.setScreen(new Nivel2(game, loginManager, numeroNivel));
                }
            }
        };
        dialog.text(idioma.get("dialog.nivelTerminadoTexto"));
        dialog.button(idioma.get("btn.volverAlMapa"), true);
        dialog.button(idioma.get("btn.repetirNivel"), false);
        dialog.show(stage);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        stage.dispose();
        ropeSimulacion.dispose();
        mundo.dispose();
        batchJuego.dispose();
        debugRenderer.dispose();
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 tmp = new Vector3(screenX, screenY, 0);
        camara.unproject(tmp);
        ropeSimulacion.intentarCortarCuerda(new Vector2(tmp.x, tmp.y));
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
