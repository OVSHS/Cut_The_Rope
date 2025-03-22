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

    public PerfilUsuario() {
    }

    public PerfilUsuario(String apodo, String contrasena, String nombreCompleto, String rutaAvatar,
                         long fechaRegistro, long ultimaSesion, float volumen) {
        this.apodo = apodo;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.rutaAvatar = rutaAvatar;
        this.fechaRegistro = fechaRegistro;
        this.ultimaSesion = ultimaSesion;
        this.volumen = volumen;
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
}
