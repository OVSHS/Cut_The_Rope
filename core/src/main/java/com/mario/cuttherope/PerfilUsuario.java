/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

/**
 *
 * @author Mario
 */
public class PerfilUsuario {

    private String apodo;
    private String contrasena;
    private String nombreCompleto;
    private String rutaAvatar;
    private long fechaRegistro;
    private long ultimaSesion;
    private float volumen;
    private long tiempoJugado;
    private int cantEstrellas;
    private int nivelDesbloqueado;

    public PerfilUsuario() {
    }

    public PerfilUsuario(String apodo, String contrasena, String nombreCompleto, String rutaAvatar,
            long fechaRegistro, long ultimaSesion, float volumen, long tiempoJugado, int cantEstrellas) {
        this.apodo = apodo;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.rutaAvatar = rutaAvatar;
        this.fechaRegistro = fechaRegistro;
        this.ultimaSesion = ultimaSesion;
        this.volumen = volumen;
        this.tiempoJugado = tiempoJugado;
        this.cantEstrellas= cantEstrellas;
        this.nivelDesbloqueado= 1;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRutaAvatar() {
        return rutaAvatar;
    }

    public void setRutaAvatar(String rutaAvatar) {
        this.rutaAvatar = rutaAvatar;
    }

    public long getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(long fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public long getUltimaSesion() {
        return ultimaSesion;
    }

    public void setUltimaSesion(long ultimaSesion) {
        this.ultimaSesion = ultimaSesion;
    }

    public float getVolumen() {
        return volumen;
    }
    public void setVolumen(float volumen) {
        this.volumen = volumen;
    }

    public long getTiempoJugado() {
        return tiempoJugado;
    }
    
    public void setTiempoJugado(long segundos) {
        this.tiempoJugado = segundos;
    }
    
    public int getCantEstrellas(){
        return cantEstrellas;
    }
    
    public void setCantEstrellas(int cantEstrellas){
        this.cantEstrellas= cantEstrellas;
    }
    
    public void addCantEstrellas(int cantEstrellas){
        this.cantEstrellas+= cantEstrellas; 
   }
    
    public int getNivelDesbloqueado(){
        return nivelDesbloqueado;
    }
    
    public void setNivelDesbloqueado(int nivelDesbloqueado){
        this.nivelDesbloqueado= nivelDesbloqueado;
    }

    public void addTiempoJugado(long segundos) {
        this.tiempoJugado += segundos;
    }

    public String getTiempoFormateado() {
        long hours = tiempoJugado / 3600;
        long minutes = (tiempoJugado % 3600) / 60;
        long seconds = tiempoJugado % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
