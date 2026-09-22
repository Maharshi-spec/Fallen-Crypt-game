package com.college.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ScoresScreen implements Screen {
    private static final float W = 960f, H = 540f;

    private final Main game;
    private final SpriteBatch batch = new SpriteBatch();
    private final FitViewport viewport = new FitViewport(W, H);
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();
    private final Texture background =
            new Texture(Gdx.files.internal("background/fallen_crypt.png"));

    public ScoresScreen(Main game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.M) ||
            Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        Preferences prefs = Gdx.app.getPreferences("TheFallenCrypt");

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(background, 0, 0, W, H);
        drawCentered("SCORES", 480, 410, 48);
        drawCentered("RED KNIGHT WINS: " + prefs.getInteger("p1Wins", 0), 480, 300, 28);
        drawCentered("BLUE KNIGHT WINS: " + prefs.getInteger("p2Wins", 0), 480, 245, 28);
        drawCentered("PRESS [M] OR [ESC] FOR MENU", 480, 100, 18);
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
    @Override public void dispose() {
        batch.dispose();
        font.dispose();
        background.dispose();
    }
}
