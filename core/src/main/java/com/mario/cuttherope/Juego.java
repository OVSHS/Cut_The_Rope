/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import javax.swing.SwingUtilities;

public abstract class Juego implements Screen {
  
    protected World mundo;
    protected Box2DDebugRenderer depurador;
    protected OrthographicCamera camara;
    protected Thread hiloEventos;
    protected boolean enEjecucion;

    protected MainGame mainGame;
    protected ManejoUsuario loginManager;
    
    protected Thread timerThread;
    protected volatile boolean timerRunning;
    protected long startTime; 
    protected long elapsedTime; 
    // ...
    public Juego(MainGame mainGame, ManejoUsuario loginManager) {
        this.mainGame = mainGame;
        this.loginManager = loginManager;
    }

    @Override
    public void show() {
        enEjecucion = true;
        hiloEventos = new Thread(() -> {
            while (enEjecucion) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        hiloEventos.start();
    }

    @Override
    public void hide() {
        enEjecucion = false;
        if (hiloEventos != null && hiloEventos.isAlive()) {
            hiloEventos.interrupt();
        }
    }

    @Override
    public void render(float delta) { }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        if (mundo != null) {
            mundo.dispose();
        }
        if (depurador != null) {
            depurador.dispose();
        }
    }
}