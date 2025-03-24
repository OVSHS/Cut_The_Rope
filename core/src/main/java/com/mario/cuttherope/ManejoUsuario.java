/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class ManejoUsuario {

    private PerfilUsuario perfilUsuarioActual;
    private int nivelDesbloqueado = 1;
    private Preferences preferences;

    public ManejoUsuario() {
        preferences = Gdx.app.getPreferences("Cut The Rope");
        cargarProgreso();
    }

    public boolean hayJugadorLogueado() {
        return perfilUsuarioActual != null;
    }

    public boolean registerJugador(String apodo, String contrasena, String nombreCompleto, String rutaAvatar) {
        FileHandle folder = Gdx.files.local("usuario/" + apodo);
        if (folder.exists()) {
            return false;
        }
        folder.mkdirs();
        PerfilUsuario nuevo = new PerfilUsuario();
        nuevo.setApodo(apodo);
        nuevo.setContrasena(contrasena);
        nuevo.setNombreCompleto(nombreCompleto);
        nuevo.setRutaAvatar(rutaAvatar);
        long now = new Date().getTime();
        nuevo.setFechaRegistro(now);
        nuevo.setUltimaSesion(now);
        nuevo.setVolumen(1.0f);
        FileHandle file = folder.child("datos.bin");
        return saveUserData(file, nuevo);
    }

    public boolean login(String apodo, String contrasena) {
        FileHandle folder = Gdx.files.local("usuario/" + apodo);
        if (!folder.exists()) {
            return false;
        }
        FileHandle file = folder.child("datos.bin");
        PerfilUsuario data = loadUserData(file);
        if (data == null) {
            return false;
        }
        if (!data.getContrasena().equals(contrasena)) {
            return false;
        }
        data.setUltimaSesion(new Date().getTime());
        saveUserData(file, data);
        perfilUsuarioActual = data;
        return true;
    }

    public void logout() {
        perfilUsuarioActual = null;
    }

    public PerfilUsuario getPerfilUsuarioActual() {
        return perfilUsuarioActual;
    }

    public String getUsuarioActual() {
        if (perfilUsuarioActual != null) {
            return perfilUsuarioActual.getApodo();
        }
        return null;
    }

    public boolean actualizarPerfil(PerfilUsuario perfil) {
        FileHandle folder = Gdx.files.local("usuario/" + perfil.getApodo());
        folder.mkdirs();
        FileHandle datosBin = folder.child("datos.bin");
        return saveUserData(datosBin, perfil);
    }

    public int getNivelDesbloqueado() {
        return nivelDesbloqueado;
    }
    
    public void desbloquearSiguienteNivel() {
    nivelDesbloqueado++;
}

    public void setNivelDesbloqueado(int nivel) {
        // Only increase level if the new one is higher
        if (nivel > nivelDesbloqueado) {
            nivelDesbloqueado = nivel;
            // Save progress to preferences/database
            guardarProgreso();
        }

    }
    private void cargarProgreso() {
    // Load from preferences
    if (preferences.contains("nivelDesbloqueado")) {
        nivelDesbloqueado = preferences.getInteger("nivelDesbloqueado");
    }
}

    private void guardarProgreso() {
        // Save to preferences
        preferences.putInteger("nivelDesbloqueado", nivelDesbloqueado);
        preferences.flush();
    }

    public boolean saveUserData(FileHandle file, PerfilUsuario p) {
        try (DataOutputStream dos = new DataOutputStream(file.write(false))) {
            dos.writeUTF(p.getApodo());
            dos.writeUTF(p.getContrasena());
            dos.writeUTF(p.getNombreCompleto());
            dos.writeUTF(p.getRutaAvatar());
            dos.writeLong(p.getFechaRegistro());
            dos.writeLong(p.getUltimaSesion());
            dos.writeFloat(p.getVolumen());
            dos.writeLong(p.getTiempoJugado());
            dos.writeInt(p.getCantEstrellas());
            dos.writeInt(p.getNivelDesbloqueado());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PerfilUsuario loadUserData(FileHandle file) {
        if (!file.exists()) {
            return null;
        }
        try (DataInputStream dis = new DataInputStream(file.read())) {
            PerfilUsuario p = new PerfilUsuario();
            p.setApodo(dis.readUTF());
            p.setContrasena(dis.readUTF());
            p.setNombreCompleto(dis.readUTF());
            p.setRutaAvatar(dis.readUTF());
            p.setFechaRegistro(dis.readLong());
            p.setUltimaSesion(dis.readLong());
            p.setVolumen(dis.readFloat());
            p.setTiempoJugado(dis.readLong());
            p.setCantEstrellas(dis.readInt());
            p.setNivelDesbloqueado(dis.readInt());
            return p;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
