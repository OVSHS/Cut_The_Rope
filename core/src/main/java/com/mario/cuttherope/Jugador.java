/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

/**
 *
 * @author Mario
 */
public class Jugador {
    private String nombre;
    private String contraseña;

    public Jugador(String nombre, String contraseña) {
        this.nombre = nombre;
        this.contraseña = contraseña;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean validarContraseña(String contraseña) {
        return this.contraseña.equals(contraseña);
    }

    public String toFileString() {
        return nombre + "|" + contraseña;
    }

    public static Jugador fromFileString(String line) {
        String[] datos = line.split("\\|");
        if (datos.length == 2) {
            return new Jugador(datos[0], datos[1]);
        }
        return null;
    }
}
