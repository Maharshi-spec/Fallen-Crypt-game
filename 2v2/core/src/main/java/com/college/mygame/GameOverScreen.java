package com.college.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOverScreen implements Screen {

    private static final float W = 960f;
    private static final float H = 540f;

    private final Main game;
    private final String winner;

    private final SpriteBatch batch =
        new SpriteBatch();

    private final FitViewport viewport =
        new FitViewport(W, H);

    private final BitmapFont font =
        new BitmapFont();

    private final GlyphLayout layout =
        new GlyphLayout();

    private final Texture background =
        new Texture(
            Gdx.files.internal(
                "background/fallen_crypt.png"
            )
        );

    private boolean victoryPlayed = false;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public GameOverScreen(
        Main game,
        String winner) {

        this.game = game;
        this.winner = winner;

        // Victory sound.
        game.getAudioManager()
            .playVictory();

        victoryPlayed = true;
    }

    // =========================================================
    // RENDER
    // =========================================================

    @Override
    public void render(float delta) {

        // =====================================================
        // RESTART
        // =====================================================

        if (Gdx.input.isKeyJustPressed(
            Input.Keys.ENTER)) {

            game.getAudioManager()
                .playButtonClick();

            game.setScreen(
                new GameScreen(game)
            );

            return;
        }

        // =====================================================
        // MAIN MENU
        // =====================================================

        if (Gdx.input.isKeyJustPressed(
            Input.Keys.M)) {

            game.getAudioManager()
                .playButtonClick();

            game.setScreen(
                new MainMenuScreen(game)
            );

            return;
        }

        // =====================================================
        // CLEAR
        // =====================================================

        Gdx.gl.glClearColor(
            0f,
            0f,
            0f,
            1f
        );

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
        );

        // =====================================================
        // VIEWPORT
        // =====================================================

        viewport.apply();

        batch.setProjectionMatrix(
            viewport.getCamera().combined
        );

        batch.begin();

        // =====================================================
        // BACKGROUND
        // =====================================================

        batch.draw(
            background,
            0,
            0,
            W,
            H
        );

        // =====================================================
        // TEXT
        // =====================================================

        drawCentered(
            "GAME OVER",
            480,
            390,
            55
        );

        drawCentered(
            winner + " WINS",
            480,
            300,
            36
        );

        drawCentered(
            "PRESS [ENTER] TO REMATCH",
            480,
            180,
            20
        );

        drawCentered(
            "PRESS [M] FOR MAIN MENU",
            480,
            125,
            20
        );

        batch.end();
    }

    // =========================================================
    // CENTERED TEXT
    // =========================================================

    private void drawCentered(
        String text,
        float x,
        float y,
        float size) {

        font.getData().setScale(
            size / 15f
        );

        layout.setText(
            font,
            text
        );

        font.draw(
            batch,
            text,
            x - layout.width / 2f,
            y
        );
    }

    // =========================================================
    // RESIZE
    // =========================================================

    @Override
    public void resize(
        int width,
        int height) {

        viewport.update(
            width,
            height,
            true
        );
    }

    @Override
    public void show() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    // =========================================================
    // DISPOSE
    // =========================================================

    @Override
    public void dispose() {

        batch.dispose();
        font.dispose();
        background.dispose();
    }
}
