/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import java.util.ArrayList;
import com.badlogic.gdx.physics.box2d.Joint;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
  Clase que representa una cuerda fisica en Box2D, uniendo dos cuerpos
 */
public class Cuerda {

    public Body cuerpoAnclarA;
    public Body cuerpoAnclarB;
    public World mundo;
    public boolean esColisionHabilitada;
    public ArrayList<Body> listaCuerpos = new ArrayList<>();
    public ArrayList<Joint> listaJoints = new ArrayList<>();

    public Cuerda(Body cuerpoAnclarA, Body cuerpoAnclarB, World mundo) {
        this(cuerpoAnclarA, cuerpoAnclarB, mundo, 0, false, false);
    }

    public Cuerda(Body cuerpoAnclarA, Body cuerpoAnclarB, World mundo, boolean esElastica) {
        this(cuerpoAnclarA, cuerpoAnclarB, mundo, 0, esElastica, false);
    }

    public Cuerda(Body cuerpoAnclarA, Body cuerpoAnclarB, World mundo, int longitudExtra) {
        this(cuerpoAnclarA, cuerpoAnclarB, mundo, longitudExtra, false, false);
    }

    public Cuerda(Body cuerpoAnclarA, Body cuerpoAnclarB, World mundo, int longitudExtra, boolean esElastica) {
        this(cuerpoAnclarA, cuerpoAnclarB, mundo, longitudExtra, esElastica, false);
    }

    public Cuerda(Body cuerpoAnclarA, Body cuerpoAnclarB, World mundo, int longitudExtra, boolean esElastica, boolean habilitarColision) {
        this.cuerpoAnclarA = cuerpoAnclarA;
        this.cuerpoAnclarB = cuerpoAnclarB;
        this.mundo = mundo;
        this.esColisionHabilitada = habilitarColision;

        float pasoGeneracion = 1.5f;  // Espaciado entre los segmentos de la cuerda

        // Definir la forma de los eslabones de la cuerda
        PolygonShape formaEslabon = new PolygonShape();
        formaEslabon.setAsBox(0.5f, 0.125f);

        FixtureDef defFixture = new FixtureDef();
        defFixture.shape = formaEslabon;
        defFixture.density = 5f;
        defFixture.friction = 0.4f;
        defFixture.restitution = 0.0f;
        defFixture.filter.groupIndex = -1;

        cuerpoAnclarA.setAngularDamping(1.0f);
        cuerpoAnclarB.setAngularDamping(1.0f);

        Vector2 vectorDistancia = new Vector2(cuerpoAnclarB.getPosition());
        vectorDistancia.sub(cuerpoAnclarA.getPosition());
        vectorDistancia.nor().scl(0.5f);

        Body cuerpoPrevio = cuerpoAnclarA;
        Body cuerpoTemporal;
        BodyDef defCuerpo = new BodyDef();
        defCuerpo.type = BodyDef.BodyType.DynamicBody;
        defCuerpo.angularDamping = 50.0f;
        defCuerpo.fixedRotation = true; 

        Vector2 posicionSiguiente = new Vector2(cuerpoAnclarA.getPosition());
        int distanciaCuerpos = (int) (cuerpoAnclarA.getPosition().dst(cuerpoAnclarB.getPosition()));

        listaCuerpos.add(cuerpoAnclarA);

        // Crear los eslabones de la cuerda
        for (float i = 0; i < distanciaCuerpos; i += pasoGeneracion) {
            posicionSiguiente.add(vectorDistancia.x, vectorDistancia.y);
            defCuerpo.position.set(posicionSiguiente);

            cuerpoTemporal = mundo.createBody(defCuerpo);
            cuerpoTemporal.createFixture(defFixture);

            // Crear un distancejoint entre los segmentos de la cuerda
            DistanceJointDef defCuerda = new DistanceJointDef();
            defCuerda.bodyA = cuerpoPrevio;
            defCuerda.bodyB = cuerpoTemporal;
            defCuerda.length = pasoGeneracion;
            defCuerda.dampingRatio = 3f;
            defCuerda.frequencyHz =5f;
           
            defCuerda.collideConnected = false;

            Joint joint = mundo.createJoint(defCuerda);
            listaJoints.add(joint);

            listaCuerpos.add(cuerpoTemporal);
            cuerpoPrevio = cuerpoTemporal;
        }

        // Conectar el ultimo eslabon con el dulce
        DistanceJointDef defCuerdaFinal = new DistanceJointDef();
        defCuerdaFinal.bodyA = cuerpoPrevio;
        defCuerdaFinal.bodyB = cuerpoAnclarB;
        defCuerdaFinal.length = pasoGeneracion;
        defCuerdaFinal.dampingRatio = 3f;
        defCuerdaFinal.frequencyHz = 5f;
       
        defCuerdaFinal.collideConnected = false;

        Joint jointFinal = mundo.createJoint(defCuerdaFinal);
        listaJoints.add(jointFinal);

     
        formaEslabon.dispose();

    }

    public boolean verificarPunto(Vector2 punto) {
        for (Body cuerpo : listaCuerpos) {
            float distancia = cuerpo.getPosition().dst(punto);
            if (distancia < 0.5f) {
                return true;
            }
        }
        return false;
    }

   public void setLongitud(final float nuevaLongitud) {
    final float pasoGeneracion = 1.5f; // mismo valor que usas actualmente
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.execute(new Runnable() {
        @Override
        public void run() {
            // Calcular el número de segmentos y precomputar las posiciones
            int numSegmentos = (int)(nuevaLongitud / pasoGeneracion);
            final ArrayList<Vector2> posiciones = new ArrayList<>();
            Vector2 vectorDistancia = new Vector2(cuerpoAnclarB.getPosition()).sub(cuerpoAnclarA.getPosition()).nor().scl(0.5f);
            Vector2 posicionActual = new Vector2(cuerpoAnclarA.getPosition());
            for (int i = 0; i < numSegmentos; i++) {
                posicionActual.add(vectorDistancia);
                posiciones.add(new Vector2(posicionActual));
            }
            // Al terminar, se publica la tarea en el hilo principal para modificar el mundo
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    // Destruir joints y cuerpos existentes (excepto el anclaje A)
                    for (Joint joint : listaJoints) {
                        mundo.destroyJoint(joint);
                    }
                    listaJoints.clear();
                    for (int i = 1; i < listaCuerpos.size(); i++) {
                        mundo.destroyBody(listaCuerpos.get(i));
                    }
                    listaCuerpos.clear();
                    listaCuerpos.add(cuerpoAnclarA);
                    
                    // Recrear los eslabones con las posiciones calculadas
                    PolygonShape formaEslabon = new PolygonShape();
                    formaEslabon.setAsBox(0.5f, 0.125f);
                    FixtureDef defFixture = new FixtureDef();
                    defFixture.shape = formaEslabon;
                    defFixture.density = 5f;
                    defFixture.friction = 0.4f;
                    defFixture.restitution = 0.0f;
                    defFixture.filter.groupIndex = -1;
                    
                    BodyDef defCuerpo = new BodyDef();
                    defCuerpo.type = BodyDef.BodyType.DynamicBody;
                    defCuerpo.angularDamping = 50.0f;
                    defCuerpo.fixedRotation = true;
                    
                    Body cuerpoPrevio = cuerpoAnclarA;
                    for (Vector2 pos : posiciones) {
                        defCuerpo.position.set(pos);
                        Body cuerpoTemporal = mundo.createBody(defCuerpo);
                        cuerpoTemporal.createFixture(defFixture);
                        
                        DistanceJointDef defCuerda = new DistanceJointDef();
                        defCuerda.bodyA = cuerpoPrevio;
                        defCuerda.bodyB = cuerpoTemporal;
                        defCuerda.length = pasoGeneracion;
                        defCuerda.dampingRatio = 3f;
                        defCuerda.frequencyHz = 5f;
                        defCuerda.collideConnected = false;
                        
                        Joint joint = mundo.createJoint(defCuerda);
                        listaJoints.add(joint);
                        listaCuerpos.add(cuerpoTemporal);
                        cuerpoPrevio = cuerpoTemporal;
                    }
                    
                    // Conectar el último eslabón con el cuerpoB
                    DistanceJointDef defCuerdaFinal = new DistanceJointDef();
                    defCuerdaFinal.bodyA = cuerpoPrevio;
                    defCuerdaFinal.bodyB = cuerpoAnclarB;
                    defCuerdaFinal.length = pasoGeneracion;
                    defCuerdaFinal.dampingRatio = 3f;
                    defCuerdaFinal.frequencyHz = 5f;
                    defCuerdaFinal.collideConnected = false;
                    Joint jointFinal = mundo.createJoint(defCuerdaFinal);
                    listaJoints.add(jointFinal);
                    
                    formaEslabon.dispose();
                }
            });
        }
    });
    executor.shutdown();
}

    public void eliminar() {
        for (Body cuerpo : listaCuerpos) {
            while (!cuerpo.getJointList().isEmpty()) {
                mundo.destroyJoint(cuerpo.getJointList().get(0).joint);
            }
            cuerpo.setGravityScale(3);
        }
    }

    public Vector2 getInicio() {
        return cuerpoAnclarA.getPosition();
    }

    public Vector2 getFin() {
        return cuerpoAnclarB.getPosition();
    }
}
