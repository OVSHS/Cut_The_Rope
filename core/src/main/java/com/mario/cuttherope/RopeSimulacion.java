package com.mario.cuttherope;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
  Clase que maneja la lógica de las cuerdas, el dulce y las estrellas, asi como
 el cambio de imagen de OmNom (abierto/cerrado).
 
 Tambien incluye la logica para "cortar" las cuerdas cuando el mouse se
 arrastra sobre ellas.
 */
public class RopeSimulacion {

    private Body cuerpoOmNom;
    private Body cuerpoDulce;
    private List<StarObject> listaEstrellas;
    private List<Cuerda> listaCuerdas;
    private Texture texturaOmNomCerrado;
    private Texture texturaOmNomAbierto;
    private Texture texturaDulce;
    private Texture texturaEstrella;
    private Texture texturaEstrellaScene;
    private Sprite spriteOmNom;
    private Sprite spriteDulce;
    private Sprite spriteEstrella;
    private float distanciaApertura = 2.5f;
    private float distanciaEstrella = 1.2f;
    private boolean dulceComido = false;
    private MainGame game;
    private ManejoUsuario loginManager;
    private int numeroNivel;
    private World mundo;
    private Texture texturaEstrellaEmpty;
    private Texture texturaEstrellaFull;
    private int estrellasRecogidas = 0;
    private int totalStars = 0;


    public RopeSimulacion(MainGame game, ManejoUsuario loginManager, int nivel, World mundo) {
        this.game = game;
        this.loginManager = loginManager;
        this.numeroNivel = nivel;
        this.mundo = mundo;
    }

    public RopeSimulacion(Body omNom, Body dulce, List<StarObject> estrellas, List<Cuerda> cuerdas, World mundo) {
        inicializarElementos(omNom, dulce, estrellas, cuerdas, mundo);
    }

    public void inicializarElementos(Body omNom, Body dulce, List<StarObject> estrellas, List<Cuerda> cuerdas, World mundo) {
        this.cuerpoOmNom = omNom;
        this.cuerpoDulce = dulce;
        this.listaEstrellas = estrellas;
        this.listaCuerdas = cuerdas;
        this.mundo = mundo;
        if (texturaOmNomCerrado == null) {
            texturaOmNomCerrado = new Texture("omnom.png");
            texturaOmNomAbierto = new Texture("omnomhambre.png");
            texturaDulce = new Texture("calabaza.png");
            texturaEstrella = new Texture("estrella.png");
            texturaEstrellaEmpty = new Texture("estrellablanca.png");
            texturaEstrellaFull = new Texture("estrella.png");

        }
        spriteOmNom = new Sprite(texturaOmNomCerrado);
        spriteDulce = new Sprite(texturaDulce);
        spriteEstrella = new Sprite(texturaEstrella);
        spriteOmNom.setSize(2f, 2f);
        spriteDulce.setSize(1.5f, 1.5f);
        spriteEstrella.setSize(1f, 1f);
        totalStars = (estrellas != null) ? estrellas.size() : 0;

    }

    public void actualizarCuerdas(List<Cuerda> cuerdas) {
        this.listaCuerdas = cuerdas;
    }

    public void actualizar(final float delta) {
        if (cuerpoOmNom == null) {
            return;
        }
        if (dulceComido) {
            spriteOmNom.setTexture(texturaOmNomCerrado);
            return;
        }
        if (cuerpoDulce != null) {
            float distancia = cuerpoOmNom.getPosition().dst(cuerpoDulce.getPosition());
            if (distancia <= distanciaApertura) {
                spriteOmNom.setTexture(texturaOmNomAbierto);
            } else {
                spriteOmNom.setTexture(texturaOmNomCerrado);
            }
        }

        // Verifica la recoleccion de estrellas en un hilo separado
        if (cuerpoDulce != null && listaEstrellas != null && !listaEstrellas.isEmpty()) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<StarObject> estrellasAEliminar = new ArrayList<>();
                    for (StarObject estrella : listaEstrellas) {
                        float distEstrella = cuerpoDulce.getPosition().dst(estrella.body.getPosition());
                        if (distEstrella < distanciaEstrella) {
                            estrellasAEliminar.add(estrella);
                        }
                    }
                    if (!estrellasAEliminar.isEmpty()) {
                        // Publicar la eliminacion de estrellas en el hilo principal
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                for (StarObject estrella : estrellasAEliminar) {
                                    mundo.destroyBody(estrella.body);
                                    listaEstrellas.remove(estrella);
                                    estrellasRecogidas++;
                                }
                            }
                        });
                    }
                }
            });
            executor.shutdown();
        }
    }

    public void renderHUD(Batch batch, int viewportWidth, int viewportHeight) {
        int hudMargin = 10;
        int starSize = 40;
        for (int i = 0; i < totalStars; i++) {
            int x = hudMargin + i * (starSize + 5);
            int y = viewportHeight - hudMargin - starSize;  // Usamos el alto del viewport de hudCamera
            if (i < estrellasRecogidas) {
                batch.draw(texturaEstrellaFull, x, y, starSize, starSize);
            } else {
                batch.draw(texturaEstrellaEmpty, x, y, starSize, starSize);
            }
        }
    }

    public void render(Batch batch) {
        if (cuerpoOmNom != null) {
            Vector2 posOmNom = cuerpoOmNom.getPosition();
            spriteOmNom.setPosition(posOmNom.x - spriteOmNom.getWidth() / 2,
                    posOmNom.y - spriteOmNom.getHeight() / 2);
            spriteOmNom.draw(batch);
        }

        // Dibuja el dulce sólo si no ha sido comido
        if (!dulceComido && cuerpoDulce != null) {
            Vector2 posDulce = cuerpoDulce.getPosition();
            spriteDulce.setPosition(posDulce.x - spriteDulce.getWidth() / 2,
                    posDulce.y - spriteDulce.getHeight() / 2);
            spriteDulce.draw(batch);
        }

        // Dibuja las estrellas
        if (listaEstrellas != null && !listaEstrellas.isEmpty()) {
            for (StarObject estrella : listaEstrellas) {
                Vector2 posEst = estrella.body.getPosition();
                spriteEstrella.setPosition(posEst.x - spriteEstrella.getWidth() / 2,
                        posEst.y - spriteEstrella.getHeight() / 2);
                spriteEstrella.draw(batch);
            }
        }
    }

    public void intentarCortarCuerda(Vector2 posicionMouse) {
        if (listaCuerdas == null || listaCuerdas.isEmpty()) {
            return;
        }
        Cuerda cuerdaCortada = null;
        for (Cuerda cuerda : listaCuerdas) {
            if (cuerda.verificarPunto(posicionMouse)) {
                cuerdaCortada = cuerda;
                break;
            }
        }
        if (cuerdaCortada != null) {
            listaCuerdas.remove(cuerdaCortada);
            cuerdaCortada.eliminar();
        }
    }

    public void setDulceComido(boolean valor) {
        dulceComido = valor;
    }

    public void dispose() {
        if (texturaOmNomCerrado != null) {
            texturaOmNomCerrado.dispose();
        }
        if (texturaOmNomAbierto != null) {
            texturaOmNomAbierto.dispose();
        }
        if (texturaDulce != null) {
            texturaDulce.dispose();
        }
        if (texturaEstrellaScene != null) {
            texturaEstrellaScene.dispose();
        }

        if (texturaEstrellaEmpty != null) {
            texturaEstrellaEmpty.dispose();
        }
        if (texturaEstrellaFull != null) {
            texturaEstrellaFull.dispose();
        }
    }

    public void setDistanciaApertura(float dist) {
        distanciaApertura = dist;
    }

    public float getDistanciaApertura() {
        return distanciaApertura;
    }

    public void setDistanciaEstrella(float dist) {
        distanciaEstrella = dist;
    }

    public float getDistanciaEstrella() {
        return distanciaEstrella;
    }

    public MainGame getGame() {
        return game;
    }

    public ManejoUsuario getLoginManager() {
        return loginManager;
    }

    public int getNumeroNivel() {
        return numeroNivel;
    }
}
