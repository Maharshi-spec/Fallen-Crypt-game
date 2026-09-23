package com.college.mygame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class Main extends Game {

    private AudioManager audioManager;

    @Override
    public void create() {

        audioManager = new AudioManager();

        setScreen(new MainMenuScreen(this));
    }

    public AudioManager getAudioManager() {

        return audioManager;
    }

    @Override
    public void setScreen(Screen screen) {

        Screen oldScreen = getScreen();

        super.setScreen(screen);

        // Dispose the previous screen to prevent
        // textures and other resources from accumulating.
        if (oldScreen != null && oldScreen != screen) {
            oldScreen.dispose();
        }
    }

    @Override
    public void dispose() {

        super.dispose();

        if (audioManager != null) {
            audioManager.dispose();
        }
    }
}
