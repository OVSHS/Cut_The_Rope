/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

/**
 *
 * @author Maria Gabriela
 */
import java.util.Locale;
import java.util.ResourceBundle;

public class Idiomas {
    private static Idiomas instancia;
    private ResourceBundle mensajes;

    // Constructor privado para singleton
    private Idiomas(Locale locale) {
        // Asegúrate de tener los archivos messages_es.properties y messages_en.properties en el classpath
        mensajes = ResourceBundle.getBundle("messages", locale);
    }

    public static Idiomas getInstance() {
        if (instancia == null) {
            instancia = new Idiomas(new Locale("es"));
        }
        return instancia;
    }

    public void setLocale(Locale locale) {
        mensajes = ResourceBundle.getBundle("messages", locale);
    }

    // Devuelve el mensaje asociado a la clave
    public String get(String key) {
        return mensajes.getString(key);
    }
}
