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

public class ObjetoJuego {
    public Pieza infoPieza;
    public Body cuerpo;
    
    public ObjetoJuego(Pieza infoPieza, Body cuerpo) {
        this.infoPieza = infoPieza;
        this.cuerpo = cuerpo;
    }
}
