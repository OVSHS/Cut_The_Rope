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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Nivel2 extends RopeSimulacion {
    private Vector2[] posicionesEstrella;
    private boolean[] estrellasRecolectadas;
    private float ANCHO_OMNOM = 1.0f;
    private float ALTO_OMNOM = 1.0f;
    private float UMBRAL_ESTRELLA = 0.7f;
    private float UMBRAL_OMNOM = 2.0f;
    private float UMBRAL_COMER = 0.3f;
    private float tamEstrella = 0.6f;
    private Skin pielNivel;
    
  
    public Nivel2(MainGame mainGame, ManejoUsuario loginManager) {
  
    super(mainGame, loginManager, 2);

    // Inicializamos los arrays de posiciones y estado de las estrellas
    posicionesEstrella = new Vector2[3];
    posicionesEstrella[0] = new Vector2(2, 5.2f);
    posicionesEstrella[1] = new Vector2(2, 4.4f);
    posicionesEstrella[2] = new Vector2(5, 3.6f);
    estrellasRecolectadas = new boolean[3];

    // Creamos las tres cuerdas en posiciones cercanas (con leves offsets en X)
   cuerdas.add(new Cuerda(mundo, 2f, 9, 12, LONGITUD_SEGMENTO, ANCHO_SEGMENTO, RADIO_DULCE)); // Izquierda
   cuerdas.add(new Cuerda(mundo, 3f, 9, 13, LONGITUD_SEGMENTO, ANCHO_SEGMENTO, RADIO_DULCE)); // Centro
cuerdas.add(new Cuerda(mundo, 5f, 9, 15, LONGITUD_SEGMENTO, ANCHO_SEGMENTO, RADIO_DULCE)); 

    
    Cuerda mainCuerda = cuerdas.get(0);
    for (int i = 1; i < cuerdas.size(); i++) {
        Cuerda aux = cuerdas.get(i);
        int indiceDulce = aux.juntasCuerda.size() - 1;
     
        if (indiceDulce >= 0 && aux.juntasCuerda.get(indiceDulce) != null) {
            mundo.destroyJoint(aux.juntasCuerda.get(indiceDulce));
            aux.juntasCuerda.set(indiceDulce, null);
        }
       
        mundo.destroyBody(aux.cuerpoDulce);
    
        aux.cuerpoDulce = mainCuerda.cuerpoDulce;
     
        RevoluteJointDef jdDulce = new RevoluteJointDef();
        jdDulce.bodyA = aux.segmentosCuerda.get(aux.segmentosCuerda.size() - 1);
        jdDulce.bodyB = mainCuerda.cuerpoDulce;
        jdDulce.localAnchorA.set(0, -LONGITUD_SEGMENTO / 2);
        jdDulce.localAnchorB.set(0, RADIO_DULCE);
        Joint jDulce = mundo.createJoint(jdDulce);
        aux.juntasCuerda.set(indiceDulce, jDulce);
    }

  
    InputMultiplexer multiplexer = new InputMultiplexer();
    multiplexer.addProcessor(escenario);
    multiplexer.addProcessor(Gdx.input.getInputProcessor());
    Gdx.input.setInputProcessor(multiplexer);
}
    
    @Override
    public void render(float delta) {
        super.render(delta);
        renderizadorFiguras.setProjectionMatrix(camara.combined);
        loteSprites.setProjectionMatrix(camara.combined);
        dibujarCuerdas();
        loteSprites.begin();
        dibujarDulce();
        dibujarEstrellas();
        dibujarOmNom();
        loteSprites.end();
        verificarRecolectas();
        escenario.act(delta);
        escenario.draw();
    }
    
    // Dibuja las tres cuerdas
    private void dibujarCuerdas() {
        renderizadorFiguras.begin(ShapeRenderer.ShapeType.Line);
        for (Cuerda cuerda : cuerdas) {
            for (int i = 0; i < cuerda.segmentosCuerda.size() - 1; i++) {
                if (cuerda.juntasCuerda.get(i) != null) {
                    Vector2 a = cuerda.segmentosCuerda.get(i).getPosition();
                    Vector2 b = cuerda.segmentosCuerda.get(i + 1).getPosition();
                    renderizadorFiguras.line(a.x, a.y, b.x, b.y);
                }
            }
            int ultimo = cuerda.segmentosCuerda.size() - 1;
            if (ultimo >= 0 && ultimo < cuerda.juntasCuerda.size() && cuerda.juntasCuerda.get(ultimo) != null) {
                Vector2 fin = cuerda.segmentosCuerda.get(ultimo).getPosition();
                Vector2 dulce = cuerda.cuerpoDulce.getPosition();
                renderizadorFiguras.line(fin.x, fin.y, dulce.x, dulce.y);
            }
        }
        renderizadorFiguras.end();
    }
    
    private void dibujarDulce() {
        // Se utiliza el dulce de la primera cuerda (compartido por todas)
        Cuerda mainCuerda = cuerdas.get(0);
        if (!juegoCompleto) {
            Vector2 posDulce = mainCuerda.cuerpoDulce.getPosition();
            float escala = 3.0f;
            float diam = (RADIO_DULCE * 2f) * escala;
            loteSprites.draw(texturaDulce, posDulce.x - diam / 2, posDulce.y - diam / 2, diam, diam);
        }
    }
    
    private void dibujarEstrellas() {
        for (int i = 0; i < posicionesEstrella.length; i++) {
            if (!estrellasRecolectadas[i]) {
                Vector2 p = posicionesEstrella[i];
                loteSprites.draw(new Texture("estrella.png"),
                        p.x - tamEstrella / 2,
                        p.y - tamEstrella / 2,
                        tamEstrella,
                        tamEstrella
                );
            }
        }
    }
    
    private void dibujarOmNom() {
        Vector2 posOmNom = new Vector2(5, 2);
        Cuerda mainCuerda = cuerdas.get(0);
        Vector2 posDulce = mainCuerda.cuerpoDulce.getPosition();
        boolean bocaAbierta = posDulce.dst(posOmNom) < UMBRAL_OMNOM;
        if (bocaAbierta) {
            loteSprites.draw(omNomAbierto,
                    posOmNom.x - ANCHO_OMNOM / 2,
                    posOmNom.y,
                    ANCHO_OMNOM,
                    ALTO_OMNOM
            );
        } else {
            loteSprites.draw(omNomCerrado,
                    posOmNom.x - ANCHO_OMNOM / 2,
                    posOmNom.y,
                    ANCHO_OMNOM,
                    ALTO_OMNOM
            );
        }
    }
    
    private void verificarRecolectas() {
        Cuerda mainCuerda = cuerdas.get(0);
        Vector2 posDulce = mainCuerda.cuerpoDulce.getPosition();
        for (int i = 0; i < posicionesEstrella.length; i++) {
            if (!estrellasRecolectadas[i]) {
                if (posDulce.dst(posicionesEstrella[i]) < UMBRAL_ESTRELLA) {
                    estrellasRecolectadas[i] = true;
                }
            }
        }
        Vector2 posOmNom = new Vector2(5, 2);
        boolean todas = true;
        for (boolean r : estrellasRecolectadas) {
            if (!r) {
                todas = false;
                break;
            }
        }
        if (todas && posDulce.dst(posOmNom) < UMBRAL_COMER && !dialogoMostrado) {
            juegoCompleto = true;
            dialogoMostrado = true;
            mostrarDialogoGanaste();
        }
    }
    
    private void mostrarDialogoGanaste() {
        Dialog dialog = new Dialog("¡Ganaste!", pielNivel) {
            @Override
            protected void result(Object object) {
                if (object.equals(true)) {
                    goToMainMenu();
                }
                hide();
            }
        };
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.text("Has recolectado las 3 estrellas, Felicidades");
        dialog.button("Aceptar", true);
        dialog.show(escenario);
    }
    
    private void goToMainMenu() {
        System.out.println("Volviendo al menú principal");
        mainGame.setScreen(new MenuPrincipal(mainGame, loginManager));
    }
}
