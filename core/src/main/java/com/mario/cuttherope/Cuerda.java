/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mario
 */
public class Cuerda {

    public List<Body> segmentosCuerda;
    public List<Joint> juntasCuerda;
    public Body cuerpoDulce;
    private World mundo;
    private float radioDulce, longitudSegmento, anchoSegmento;
    private int numSegmentos;

    public Cuerda(World mundo, float startX, float startY, int numSegmentos, float longitudSegmento, float anchoSegmento, float radioDulce) {
        this.mundo = mundo;
        this.numSegmentos = numSegmentos;
        this.longitudSegmento = longitudSegmento;
        this.anchoSegmento = anchoSegmento;
        this.radioDulce = radioDulce;
        segmentosCuerda = new ArrayList<>();
        juntasCuerda = new ArrayList<>();
        crearCuerda(startX, startY);
    }

    private void crearCuerda(float startX, float startY) {
        Body cuerpoPrevio = null;
      
        for (int i = 0; i < numSegmentos; i++) {
            BodyDef definicion = new BodyDef();
            definicion.type = BodyDef.BodyType.DynamicBody;
            definicion.position.set(startX, startY - i * longitudSegmento);

            Body segmento = mundo.createBody(definicion);

            PolygonShape forma = new PolygonShape();
            forma.setAsBox(anchoSegmento, longitudSegmento / 2);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = forma;
            fixtureDef.density = 0.5f;
            fixtureDef.friction = 0.3f;
            fixtureDef.restitution = 0.1f;
            
           
            fixtureDef.filter.groupIndex = -1;

            segmento.createFixture(fixtureDef);
            forma.dispose();
            
            segmentosCuerda.add(segmento);
            
            if (cuerpoPrevio != null) {
                RevoluteJointDef jd = new RevoluteJointDef();
                jd.bodyA = cuerpoPrevio;
                jd.bodyB = segmento;
                jd.localAnchorA.set(0, -longitudSegmento / 2);
                jd.localAnchorB.set(0, longitudSegmento / 2);
                Joint j = mundo.createJoint(jd);
                juntasCuerda.add(j);
            } else {
             
                segmento.setType(BodyDef.BodyType.StaticBody);
            }
            
            cuerpoPrevio = segmento;
        }
        
        // Creacion del dulce
        BodyDef defDulce = new BodyDef();
        defDulce.type = BodyDef.BodyType.DynamicBody;
        defDulce.position.set(startX, startY - numSegmentos * longitudSegmento - 1f);
        cuerpoDulce = mundo.createBody(defDulce);
        
        CircleShape formaDulce = new CircleShape();
        formaDulce.setRadius(radioDulce);
        
        FixtureDef fixDulce = new FixtureDef();
        fixDulce.shape = formaDulce;
        fixDulce.density = 0.5f;
        fixDulce.friction = 0.3f;
        fixDulce.restitution = 0.2f;
       

        cuerpoDulce.createFixture(fixDulce);
        formaDulce.dispose();
        
        RevoluteJointDef jdDulce = new RevoluteJointDef();
        jdDulce.bodyA = segmentosCuerda.get(segmentosCuerda.size() - 1);
        jdDulce.bodyB = cuerpoDulce;
        jdDulce.localAnchorA.set(0, -longitudSegmento / 2);
        jdDulce.localAnchorB.set(0, radioDulce);
        Joint jDulce = mundo.createJoint(jdDulce);
        juntasCuerda.add(jDulce);
    }

    public void cortarCuerda(Vector3 posAnt, Vector3 posNue) {
         float x3 = posAnt.x;
        float y3 = posAnt.y;
        float x4 = posNue.x;
        float y4 = posNue.y;
        
        // Cortar las juntas entre los segmentos
        for (int i = 0; i < segmentosCuerda.size() - 1; i++) {
            if (juntasCuerda.get(i) == null) continue;
            Vector2 a = segmentosCuerda.get(i).getPosition();
            Vector2 b = segmentosCuerda.get(i + 1).getPosition();
            if (lineasIntersecan(a.x, a.y, b.x, b.y, x3, y3, x4, y4)) {
                mundo.destroyJoint(juntasCuerda.get(i));
                juntasCuerda.set(i, null);
            }
        }
        
        // Cortar la union del dulce, si la linea la intersecta
        int indiceDulce = juntasCuerda.size() - 1;
        if (indiceDulce >= 0 && juntasCuerda.get(indiceDulce) != null) {
            Vector2 ult = segmentosCuerda.get(segmentosCuerda.size() - 1).getPosition();
            Vector2 dulce = cuerpoDulce.getPosition();
            if (lineasIntersecan(ult.x, ult.y, dulce.x, dulce.y, x3, y3, x4, y4)) {
                mundo.destroyJoint(juntasCuerda.get(indiceDulce));
                juntasCuerda.set(indiceDulce, null);
            }
        }
    }
    
   
    private boolean lineasIntersecan(float x1, float y1, float x2, float y2,
                                      float x3, float y3, float x4, float y4) {
        float denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denom == 0) return false;
        float ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
        float ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;
        return (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1);
    }
    
    
    public void dispose() {
        for (Joint joint : juntasCuerda) {
            if (joint != null) {
                mundo.destroyJoint(joint);
            }
        }
        for (Body body : segmentosCuerda) {
            mundo.destroyBody(body);
        }
        if (cuerpoDulce != null) {
            mundo.destroyBody(cuerpoDulce);
        }
    }
}

