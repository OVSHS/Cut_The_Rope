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

public class StarObject {

    public Body body;    
    public boolean owned = false;  

   
    public StarObject(Pieza pieza, Body body) {
        this.body = body;
    }
}
