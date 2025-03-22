package com.mario.cuttherope;

import com.badlogic.gdx.Game;

public class MainGame extends Game {
    @Override
    public void create() {
        AudioManager.getInstance().playMusic();
        this.setScreen(new MenuInicio(this));
    }
    
    @Override
    public void dispose() {
        AudioManager.getInstance().dispose();
        super.dispose();
    }
}
