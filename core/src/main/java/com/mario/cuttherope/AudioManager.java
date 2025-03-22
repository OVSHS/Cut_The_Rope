/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mario.cuttherope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 *
 * @author Mario
 */
public class AudioManager {
   
    private static AudioManager instance;
    private Music backgroundMusic;
    private float volume = 1.0f; 

    private AudioManager() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/musica.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(volume);
    }
        public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playMusic() {
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    public void stopMusic() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    public void dispose() {
        backgroundMusic.dispose();
    }
    public void setVolume(float volume) {
        this.volume = volume;
        backgroundMusic.setVolume(volume);
    }

    public float getVolume() {
        return volume;
    }
}
