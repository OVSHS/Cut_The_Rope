/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.ArrayList;

/**
 *
 * @author Maria Gabriela
 */
public class Nivel5 implements Screen, InputProcessor {

    private Stage stage;
    private ManejoUsuario loginManager;
    private MainGame game;
    private int numeroNivel;
    private boolean juegoTerminado = false;

    private World mundo;
    private OrthographicCamera camara;
    private SpriteBatch batchJuego;
    private Body cuerpoOmNom;
    private Body cuerpoDulce;
    private ArrayList<AreaAnclaje> listaAreasAnclaje = new ArrayList<>();
    private ArrayList<Cuerda> listaCuerdas = new ArrayList<>();
    private ArrayList<AreaAnclaje> pendingRopeCreations = new ArrayList<>();
    private OrthographicCamera hudCamera;
    private ShapeRenderer shapeRenderer;
    private ArrayList<StarObject> listaEstrellas = new ArrayList<>();
    private RopeSimulacion ropeSimulacion;

    private final float ANCHO_MUNDO = 20f;
    private final float ALTO_MUNDO = 30f;

    private Box2DDebugRenderer debugRenderer;

    public Nivel5(MainGame game, ManejoUsuario loginManager, int nivel) {
        this.game = game;
        this.loginManager = loginManager;
        this.numeroNivel = nivel;
    }

    private class AreaAnclaje {

        Body areaSensor;
        Body anclaje;
        float radio;
        boolean creadaCuerda;

        public AreaAnclaje(Body areaSensor, Body anclaje, float radio) {
            this.areaSensor = areaSensor;
            this.anclaje = anclaje;
            this.radio = radio;
            this.creadaCuerda = false;
        }
    }

    @Override
    public void show() {
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

        // Crear Om Nom en la esquina inferior derecha
        cuerpoOmNom = crearCuerpoOmNom(new Vector2(ANCHO_MUNDO - 4f, 3f));

        // Crear el dulce en la parte superior central
        cuerpoDulce = crearCuerpoDulce(new Vector2(ANCHO_MUNDO / 2f, ALTO_MUNDO * 0.75f));

        // Crear un anclaje en la parte superior
        Body anclajeArriba = crearAnclaje(new Vector2(ANCHO_MUNDO / 2f, ALTO_MUNDO * 0.85f));

        // Crear la cuerda inicial que une el anclaje superior al dulce
        Cuerda cuerdaSuperior = new Cuerda(anclajeArriba, cuerpoDulce, mundo);
        cuerdaSuperior.setLongitud(2f);
        listaCuerdas.add(cuerdaSuperior);

        // Crear las áreas de anclaje (3 círculos en diagonal)
        float radioArea = 2f;

        // Posiciones para los círculos de anclaje en diagonal
        Vector2 posArea1 = new Vector2(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f);
        Vector2 posArea2 = new Vector2(ANCHO_MUNDO / 2f + 3f, ALTO_MUNDO / 2f - 3f);
        Vector2 posArea3 = new Vector2(ANCHO_MUNDO / 2f + 6f, ALTO_MUNDO / 2f - 6f);

        // Crear las áreas de anclaje con sus respectivos anclajes
        AreaAnclaje area1 = crearAreaAnclaje(posArea1, radioArea);
        AreaAnclaje area2 = crearAreaAnclaje(posArea2, radioArea);
        AreaAnclaje area3 = crearAreaAnclaje(posArea3, radioArea);

        listaAreasAnclaje.add(area1);
        listaAreasAnclaje.add(area2);
        listaAreasAnclaje.add(area3);

        // Crear las estrellas (3 estrellas cerca de las áreas de anclaje)
        listaEstrellas.add(crearEstrella(new Vector2(posArea1.x - 2f, posArea1.y)));
        listaEstrellas.add(crearEstrella(new Vector2(posArea2.x, posArea2.y - 1f)));
        listaEstrellas.add(crearEstrella(new Vector2(posArea3.x, posArea3.y - 1f)));

        ropeSimulacion = new RopeSimulacion(game, loginManager, numeroNivel, mundo);
        ropeSimulacion.inicializarElementos(cuerpoOmNom, cuerpoDulce, listaEstrellas, listaCuerdas, mundo);

        juegoTerminado = false;
        mundo.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                // Log debug info
                if (bodyA != null && bodyB != null) {
                    Object dataA = bodyA.getUserData();
                    Object dataB = bodyB.getUserData();
                    Gdx.app.log("Contact", "Contacto entre: "
                            + (dataA != null ? dataA.toString() : "null") + " y "
                            + (dataB != null ? dataB.toString() : "null"));
                }

                // Check collision between candy and Om Nom
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
                            mostrarDialogoFelicidades();
                        });
                    }
                }

                Body dulceBody = null;
                Body areaBody = null;

                if (bodyA.getUserData() != null && bodyA.getUserData().equals("dulce")) {
                    dulceBody = bodyA;
                    if (bodyB.getUserData() != null && bodyB.getUserData().equals("areaAnclaje")) {
                        areaBody = bodyB;
                    }
                } else if (bodyB.getUserData() != null && bodyB.getUserData().equals("dulce")) {
                    dulceBody = bodyB;
                    if (bodyA.getUserData() != null && bodyA.getUserData().equals("areaAnclaje")) {
                        areaBody = bodyA;
                    }
                }

                if (dulceBody != null && areaBody != null) {

                    // Find the corresponding anchor area
                    for (AreaAnclaje area : listaAreasAnclaje) {
                        if (area.areaSensor == areaBody && !area.creadaCuerda) {
                            // Instead of creating the rope here, add the area to pending list
                            pendingRopeCreations.add(area);
                            break;
                        }
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

        InputMultiplexer multiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        for (AreaAnclaje area : pendingRopeCreations) {
            if (!area.creadaCuerda) {
                Cuerda nuevaCuerda = new Cuerda(area.anclaje, cuerpoDulce, mundo);
                nuevaCuerda.setLongitud(3f);
                // Apply an initial small impulse to help settle the physics
                if (cuerpoDulce != null) {
                    cuerpoDulce.applyLinearImpulse(new Vector2(0, -0.1f),
                            cuerpoDulce.getWorldCenter(), true);
                }
                listaCuerdas.add(nuevaCuerda);
                area.creadaCuerda = true;
            }
        }
        pendingRopeCreations.clear();

        mundo.step(delta, 6, 2);

        Gdx.gl.glClearColor(0.76f, 0.67f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(mundo, camara.combined);

        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.3f, 0.1f, 1);

        for (AreaAnclaje area : listaAreasAnclaje) {
            Vector2 pos = area.areaSensor.getPosition();
            float radio = area.radio;

            int segmentos = 20;
            float anguloIncremento = 360f / segmentos;

            for (int i = 0; i < segmentos; i++) {
                float angulo1 = (float) Math.toRadians(i * anguloIncremento);
                float angulo2 = (float) Math.toRadians((i + 0.5f) * anguloIncremento);

                float x1 = pos.x + radio * (float) Math.cos(angulo1);
                float y1 = pos.y + radio * (float) Math.sin(angulo1);
                float x2 = pos.x + radio * (float) Math.cos(angulo2);
                float y2 = pos.y + radio * (float) Math.sin(angulo2);

                shapeRenderer.line(x1, y1, x2, y2);
            }
        }
        shapeRenderer.end();

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

        if (!juegoTerminado && cuerpoDulce != null && cuerpoDulce.getPosition().y < -5) {
            juegoTerminado = true;
            mostrarDialogoFallo();
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

    }

    private AreaAnclaje crearAreaAnclaje(Vector2 pos, float radio) {
        Body anclaje = crearAnclaje(pos);

        BodyDef areaDef = new BodyDef();
        areaDef.type = BodyDef.BodyType.StaticBody;
        areaDef.position.set(pos);
        Body areaBody = mundo.createBody(areaDef);

        CircleShape areaShape = new CircleShape();
        areaShape.setRadius(radio);

        FixtureDef areaFixture = new FixtureDef();
        areaFixture.shape = areaShape;
        areaFixture.isSensor = true;

        areaBody.createFixture(areaFixture);
        areaBody.setUserData("areaAnclaje");

        areaShape.dispose();

        return new AreaAnclaje(areaBody, anclaje, radio);
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
        bd.linearDamping = 0.1f;
        Body body = mundo.createBody(bd);
        FixtureDef fd = new FixtureDef();
        fd.shape = p.forma;
        fd.density = 1f;
        fd.friction = 0.2f;
        fd.restitution = 0.1f;
        fd.filter.groupIndex = -1;
        body.createFixture(fd);
        body.setUserData("dulce");
        p.forma.dispose();
        return body;
    }

    private Body crearObstaculo(Vector2 pos, float radio) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.position.set(pos);
        Body body = mundo.createBody(bd);

        CircleShape shape = new CircleShape();
        shape.setRadius(radio);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.friction = 0.3f;
        fd.restitution = 0.1f;

        body.createFixture(fd);
        body.setUserData("obstaculo");

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

    private void mostrarDialogoFelicidades() {
        Dialog dialog = new Dialog("Felicidades", new Skin(Gdx.files.internal("uiskin.json"))) {
            @Override
            protected void result(Object object) {
                game.setScreen(new MenuNiveles(game, loginManager));
            }
        };
        dialog.text("El dulce llego a la boca de OmNom.");
        dialog.button("Aceptar", true);
        dialog.show(stage);
    }

    private void mostrarDialogoFallo() {

        Dialog dialog = new Dialog("Nivel Terminado", new Skin(Gdx.files.internal("uiskin.json"))) {
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
        dialog.text("El dulce se cayo. ¿Deseas repetir el nivel o volver al menu?");
        dialog.button("Volver al menu", true);
        dialog.button("Repetir nivel", false);
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
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
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
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float f, float f1) {
        return false;
    }

}
