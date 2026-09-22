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

public class MainMenuScreen implements Screen {
    private static final float W = 960f, H = 540f;

    private final Main game;
    private final SpriteBatch batch = new SpriteBatch();
    private final FitViewport viewport = new FitViewport(W, H);
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    private final Texture background =
            new Texture(Gdx.files.internal("background/fallen_crypt.png"));
    private final Texture playButton =
            new Texture(Gdx.files.internal("ui/play.png"));
    private final Texture scoresButton =
            new Texture(Gdx.files.internal("ui/scores.png"));

    public MainMenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            game.setScreen(new ScoresScreen(game));
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(background, 0, 0, W, H);

        drawCentered("THE FALLEN CRYPT", 480, 435, 42);
        batch.draw(playButton, 580, 275, 300, 82);
        batch.draw(scoresButton, 580, 165, 300, 82);

        drawCentered("PRESS [ENTER] TO PLAY", 480, 85, 18);
        drawCentered("PRESS [S] FOR SCORES", 480, 55, 18);
        batch.end();
    }

    private void drawCentered(String text, float x, float y, float size) {
        font.getData().setScale(size / 15f);
        layout.setText(font, text);
        font.draw(batch, text, x - layout.width / 2f, y);
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        background.dispose();
        playButton.dispose();
        scoresButton.dispose();
    }
}
