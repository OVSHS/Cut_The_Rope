/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class ManejoUsuario {

    private UserData usuarioDataActual;

    public ManejoUsuario() {
    }

    public boolean hayJugadorLogueado() {
        return (usuarioDataActual != null);
    }

    public boolean registerJugador(String apodo, String contrasena, String nombreCompleto, String rutaAvatar) {
        FileHandle folder = Gdx.files.local("usuarios/" + apodo);
        if (folder.exists()) {
            return false;
        }
        folder.mkdirs();
        UserData nuevoUsuario = new UserData();
        nuevoUsuario.apodo = apodo;
        nuevoUsuario.contrasena = contrasena;
        nuevoUsuario.nombreCompleto = nombreCompleto;
        nuevoUsuario.rutaAvatar = rutaAvatar;
        nuevoUsuario.fechaRegistro = new Date().getTime();
        FileHandle file = folder.child("datos.bin");
        return saveUserData(file, nuevoUsuario);
    }

    public boolean login(String apodo, String contrasena) {
        FileHandle folder = Gdx.files.local("usuarios/" + apodo);
        if (!folder.exists()) {
            return false;
        }
        FileHandle file = folder.child("datos.bin");
        UserData data = loadUserData(file);
        if (data == null) {
            return false;
        }
        if (data.contrasena.equals(contrasena)) {
            usuarioDataActual = data;
            return true;
        }
        return false;
    }

    public void logout() {
        usuarioDataActual = null;
    }

    public String getUsuarioActual() {
        if (usuarioDataActual != null) {
            return usuarioDataActual.apodo;
        }
        return null;
    }

    private boolean saveUserData(FileHandle file, UserData data) {
        try {
            DataOutputStream dos = new DataOutputStream(file.write(false));
            dos.writeUTF(data.apodo);
            dos.writeUTF(data.contrasena);
            dos.writeUTF(data.nombreCompleto);
            dos.writeUTF(data.rutaAvatar);
            dos.writeLong(data.fechaRegistro);
            dos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private UserData loadUserData(FileHandle file) {
        if (!file.exists()) {
            return null;
        }
        UserData data = new UserData();
        try {
            DataInputStream dis = new DataInputStream(file.read());
            data.apodo = dis.readUTF();
            data.contrasena = dis.readUTF();
            data.nombreCompleto = dis.readUTF();
            data.rutaAvatar = dis.readUTF();
            data.fechaRegistro = dis.readLong();
            dis.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class UserData {
        String apodo;
        String contrasena;
        String nombreCompleto;
        String rutaAvatar;
        long fechaRegistro;
    }
}

