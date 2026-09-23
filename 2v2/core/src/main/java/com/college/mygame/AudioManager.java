package com.college.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    // =========================================================
    // MUSIC
    // =========================================================

    private final Music backgroundMusic;

    // =========================================================
    // SOUND EFFECTS
    // =========================================================

    private final Sound swordSlash;
    private final Sound swordClash;
    private final Sound jump;
    private final Sound victory;
    private final Sound buttonClick;
    private final Sound blast;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AudioManager() {

        // Background music
        backgroundMusic = Gdx.audio.newMusic(
            Gdx.files.internal("audio/bg_music.mp3")
        );

        // Sound effects
        swordSlash = Gdx.audio.newSound(
            Gdx.files.internal("audio/sword_slash.mp3")
        );

        swordClash = Gdx.audio.newSound(
            Gdx.files.internal("audio/sword_clash.mp3")
        );

        jump = Gdx.audio.newSound(
            Gdx.files.internal("audio/jump.mp3")
        );

        victory = Gdx.audio.newSound(
            Gdx.files.internal("audio/victory.mp3")
        );

        buttonClick = Gdx.audio.newSound(
            Gdx.files.internal("audio/button_click.mp3")
        );

        blast = Gdx.audio.newSound(
            Gdx.files.internal("audio/blast.mp3")
        );

        // Music should automatically repeat.
        backgroundMusic.setLooping(true);

        // Adjust this if the music is too loud.
        backgroundMusic.setVolume(0.55f);
    }

    // =========================================================
    // BACKGROUND MUSIC
    // =========================================================

    public void startMusicFromBeginning() {

        backgroundMusic.stop();
        backgroundMusic.setPosition(0f);
        backgroundMusic.play();
    }

    public void stopMusic() {

        backgroundMusic.stop();
    }

    // =========================================================
    // SOUND EFFECTS
    // =========================================================

    public void playSwordSlash() {

        swordSlash.play(0.8f);
    }

    public void playSwordClash() {

        swordClash.play(0.9f);
    }

    public void playJump() {

        jump.play(0.8f);
    }

    public void playVictory() {

        victory.play(1.0f);
    }

    public void playButtonClick() {

        buttonClick.play(0.8f);
    }

    public void playBlast() {

        blast.play(0.9f);
    }

    // =========================================================
    // DISPOSE
    // =========================================================

    public void dispose() {

        backgroundMusic.dispose();

        swordSlash.dispose();
        swordClash.dispose();
        jump.dispose();
        victory.dispose();
        buttonClick.dispose();
        blast.dispose();
    }
}
