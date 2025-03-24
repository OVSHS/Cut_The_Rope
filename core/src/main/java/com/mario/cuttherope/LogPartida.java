/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

/**
 *
 * @author Maria Gabriela
 */
public class LogPartida {

    private int nivel;       // Nivel jugado
    private int estrellas;   // Estrellas obtenidas (0, 1, 2, 3)
    private String fechaHora; // Fecha y hora de la partida

    public LogPartida(int nivel, int estrellas, String fechaHora) {
        this.nivel = nivel;
        this.estrellas = estrellas;
        this.fechaHora = fechaHora;
    }

    // Getters
    public int getNivel() {
        return nivel;
    }

    public int getEstrellas() {
        return estrellas;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    @Override
    public String toString() {
        return "Nivel: " + nivel + ", Estrellas: " + estrellas + ", Fecha: " + fechaHora;
    }
}

