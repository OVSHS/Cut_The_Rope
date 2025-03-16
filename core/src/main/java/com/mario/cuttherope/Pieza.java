/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

/**
 *
 * @author Mario
 */
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.math.Vector2;

public class Pieza {

    public Shape forma;

    public Vector2 posicion;

    public BodyType tipoCuerpo;

    public Body cuerpo;

    public float angulo = 0;

    public float friccion = 0.5f;
    public float restitucion = 0.5f;
    public float escalaGravedad = 1.0f;

    public boolean esSensor = false;

    public boolean estaCreada = false;

    // Vector cero para reutilizar en calculos
    public static final Vector2 vectorCero = new Vector2(0, 0);

    public int numeroUniones;

    public Pieza(Pieza otraPieza) {
        this.forma = otraPieza.forma;
        if (otraPieza.posicion != null) {
            this.posicion = new Vector2(otraPieza.posicion);
        }
        this.tipoCuerpo = otraPieza.tipoCuerpo;
        this.cuerpo = otraPieza.cuerpo;
        this.angulo = otraPieza.angulo;
        this.friccion = otraPieza.friccion;
        this.restitucion = otraPieza.restitucion;
        this.escalaGravedad = otraPieza.escalaGravedad;

        this.esSensor = otraPieza.esSensor;
        this.estaCreada = otraPieza.estaCreada;

        this.numeroUniones = otraPieza.numeroUniones;
    }

    public Pieza(float radio, BodyType tipo) {
        this.forma = new CircleShape();
        ((CircleShape) this.forma).setRadius(radio);
        this.posicion = new Vector2(0, 0);
        this.tipoCuerpo = tipo;
    }

    public Pieza(float mitadAncho, float mitadAlto, float anguloGrados, BodyType tipo) {
        this.forma = new PolygonShape();
        ((PolygonShape) this.forma).setAsBox(mitadAncho, mitadAlto, vectorCero, 0);
        this.posicion = new Vector2(0, 0);
        this.tipoCuerpo = tipo;
        this.angulo = (float) Math.toRadians(anguloGrados);
    }

    public Pieza(BodyType tipo, Vector2... puntos) {
        this.forma = new ChainShape();
        ((ChainShape) this.forma).createLoop(puntos);
        this.posicion = null;
        this.tipoCuerpo = tipo;
    }

    public Pieza(BodyType tipo, int numeroDeUniones) {
        this.posicion = null;
        this.tipoCuerpo = tipo;
        this.numeroUniones = numeroDeUniones;
    }

    public Pieza establecerFisica(float friccion, float restitucion, float escalaGravedad, boolean esSensor) {
        this.friccion = friccion;
        this.restitucion = restitucion;
        this.escalaGravedad = escalaGravedad;
        this.esSensor = esSensor;
        return this;
    }

    public Pieza establecerSensor(boolean valor) {
        this.esSensor = valor;
        return this;
    }

    public Pieza asignarCuerpo(Body cuerpo) {
        this.cuerpo = cuerpo;
        this.estaCreada = true;
        return this;
    }

    public Pieza establecerAngulo(float anguloGrados) {
        this.angulo = (float) Math.toRadians(anguloGrados);
        return this;

    }
}
