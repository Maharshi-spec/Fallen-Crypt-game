package com.college.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen implements Screen {

    public static final float WORLD_W = 960f;
    public static final float WORLD_H = 540f;

    private static Animation<TextureRegion> auraAnimation;

    private final Main game;

    private final SpriteBatch batch = new SpriteBatch();
    private final FitViewport viewport =
        new FitViewport(WORLD_W, WORLD_H);

    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    // =========================
    // BACKGROUND
    // =========================

    private final Texture background =
        new Texture(Gdx.files.internal(
            "background/fallen_crypt.png"));

    // =========================
    // UI
    // =========================

    private final Texture hpFrame =
        new Texture(Gdx.files.internal(
            "ui/health_frame.png"));

    private final Texture hpFill =
        new Texture(Gdx.files.internal(
            "ui/health_fill.png"));

    private final Texture mpFrame =
        new Texture(Gdx.files.internal(
            "ui/mana_frame.png"));

    private final Texture mpFill =
        new Texture(Gdx.files.internal(
            "ui/mana_fill.png"));

    private final Texture redPortrait =
        new Texture(Gdx.files.internal(
            "ui/red_portrait.png"));

    private final Texture bluePortrait =
        new Texture(Gdx.files.internal(
            "ui/blue_portrait.png"));

    private final Texture hitTexture =
        new Texture(Gdx.files.internal(
            "ui/hit.png"));

    // =========================
    // PLAYERS
    // =========================

    private final Player p1;
    private final Player p2;

    // =========================
    // PROJECTILES
    // =========================

    private final Array<FlyingSwordAura> projectiles =
        new Array<>();

    // =========================
    // HIT POPUPS
    // =========================

    private float p1HitPopup = 0f;
    private float p2HitPopup = 0f;

    private boolean ending = false;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public GameScreen(Main game) {

        this.game = game;

        // Load Flying Sword Aura animation.
        auraAnimation = loadAuraAnimation();

        // Starting positions.
        p1 = new Player(
            true,
            180f,
            76f
        );

        p2 = new Player(
            false,
            716f,
            76f
        );
    }

    // =========================================================
    // LOAD FLYING SWORD AURA
    // =========================================================

    private Animation<TextureRegion> loadAuraAnimation() {

        Texture texture =
            new Texture(Gdx.files.internal(
                "effects/flying_sword_aura.png"));

        int frameCount = 4;

        int frameWidth =
            texture.getWidth() / frameCount;

        TextureRegion[] frames =
            new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {

            frames[i] = new TextureRegion(
                texture,
                i * frameWidth,
                0,
                frameWidth,
                texture.getHeight()
            );
        }

        return new Animation<>(
            0.08f,
            frames
        );
    }

    // =========================================================
    // GET AURA ANIMATION
    // =========================================================

    public static Animation<TextureRegion> getAuraAnimation() {
        return auraAnimation;
    }

    // =========================================================
    // RENDER
    // =========================================================

    @Override
    public void render(float delta) {

        // Update game logic.
        if (!ending) {
            update(delta);
        }

        // Clear screen.
        Gdx.gl.glClearColor(
            0f,
            0f,
            0f,
            1f
        );

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
        );

        // Apply viewport.
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
            WORLD_W,
            WORLD_H
        );

        // =====================================================
        // PLAYERS
        // =====================================================

        p1.draw(batch);
        p2.draw(batch);

        // =====================================================
        // PROJECTILES
        // =====================================================

        for (FlyingSwordAura aura : projectiles) {
            aura.draw(batch);
        }

        // =====================================================
        // UI
        // =====================================================

        drawUI();

        batch.end();

        // =====================================================
        // GAME OVER
        // =====================================================

        if (!ending) {
            checkGameOver();
        }
    }

    // =========================================================
    // UPDATE GAME
    // =========================================================

    private void update(float delta) {

        // Update players.
        p1.update(
            delta,
            p2,
            projectiles
        );

        p2.update(
            delta,
            p1,
            projectiles
        );

        // =====================================================
        // P1 MELEE ATTACK
        // =====================================================

        if (p1.canDealMeleeHit()) {

            Rectangle attackBox =
                p1.getAttackBox();

            if (attackBox.overlaps(
                p2.getBody())) {

                p2.takeDamage(10f);

                p1HitPopup = 0.35f;
            }
        }

        // =====================================================
        // P2 MELEE ATTACK
        // =====================================================

        if (p2.canDealMeleeHit()) {

            Rectangle attackBox =
                p2.getAttackBox();

            if (attackBox.overlaps(
                p1.getBody())) {

                p1.takeDamage(10f);

                p2HitPopup = 0.35f;
            }
        }

        // =====================================================
        // UPDATE PROJECTILES
        // =====================================================

        for (int i = projectiles.size - 1;
             i >= 0;
             i--) {

            FlyingSwordAura aura =
                projectiles.get(i);

            aura.update(
                delta,
                WORLD_W
            );

            if (aura.isAlive()) {

                // P1 projectile hits P2.
                if (aura.belongsToPlayerOne()
                    && aura.getBounds()
                    .overlaps(p2.getBody())) {

                    p2.takeDamage(
                        aura.getDamage()
                    );

                    aura.destroy();

                    p1HitPopup = 0.35f;
                }

                // P2 projectile hits P1.
                else if (!aura.belongsToPlayerOne()
                    && aura.getBounds()
                    .overlaps(p1.getBody())) {

                    p1.takeDamage(
                        aura.getDamage()
                    );

                    aura.destroy();

                    p2HitPopup = 0.35f;
                }
            }

            // Remove dead/out-of-screen projectile.
            if (!aura.isAlive()) {

                projectiles.removeIndex(i);
            }
        }

        // =====================================================
        // HIT POPUP TIMER
        // =====================================================

        p1HitPopup =
            Math.max(
                0f,
                p1HitPopup - delta
            );

        p2HitPopup =
            Math.max(
                0f,
                p2HitPopup - delta
            );
    }

    // =========================================================
    // GAME OVER
    // =========================================================

    private void checkGameOver() {

        if (!p1.isDead()
            && !p2.isDead()) {

            return;
        }

        ending = true;

        boolean p1Won =
            !p1.isDead();

        // Save win history.
        Preferences prefs =
            Gdx.app.getPreferences(
                "TheFallenCrypt"
            );

        String key =
            p1Won
                ? "p1Wins"
                : "p2Wins";

        prefs.putInteger(
            key,
            prefs.getInteger(key, 0) + 1
        );

        prefs.flush();

        // Go to Game Over screen.
        game.setScreen(
            new GameOverScreen(
                game,
                p1Won
                    ? "RED KNIGHT"
                    : "BLUE KNIGHT"
            )
        );
    }

    // =========================================================
    // DRAW UI
    // =========================================================

    private void drawUI() {

        // =====================================================
        // P1
        // =====================================================

        // P1 HP:
        // Full bar -> empty from RIGHT to LEFT.
        drawBar(
            55,
            478,
            360,
            22,
            p1.getHp(),
            hpFrame,
            hpFill,
            false
        );

        // P1 MP:
        // Full bar -> empty from RIGHT to LEFT.
        drawBar(
            55,
            445,
            360,
            16,
            p1.getMp(),
            mpFrame,
            mpFill,
            false
        );

        // =====================================================
        // P2
        // =====================================================

        // P2 HP:
        // Full bar -> empty from LEFT to RIGHT.
        drawBar(
            545,
            478,
            360,
            22,
            p2.getHp(),
            hpFrame,
            hpFill,
            true
        );

        // P2 MP:
        // Full bar -> empty from LEFT to RIGHT.
        drawBar(
            545,
            445,
            360,
            16,
            p2.getMp(),
            mpFrame,
            mpFill,
            true
        );

        // =====================================================
        // PORTRAITS
        // =====================================================

        batch.draw(
            redPortrait,
            15,
            430,
            70,
            70
        );

        batch.draw(
            bluePortrait,
            875,
            430,
            70,
            70
        );

        // =====================================================
        // TEXT
        // =====================================================

        drawText(
            "P1 HP "
                + Math.round(p1.getHp())
                + "/100",
            100,
            495,
            17
        );

        drawText(
            "P1 MP "
                + Math.round(p1.getMp())
                + "/100",
            100,
            457,
            15
        );

        drawText(
            "P2 HP "
                + Math.round(p2.getHp())
                + "/100",
            690,
            495,
            17
        );

        drawText(
            "P2 MP "
                + Math.round(p2.getMp())
                + "/100",
            690,
            457,
            15
        );

        // =====================================================
        // HIT POPUPS
        // =====================================================

        if (p1HitPopup > 0f) {

            batch.draw(
                hitTexture,
                270,
                405,
                80,
                42
            );
        }

        if (p2HitPopup > 0f) {

            batch.draw(
                hitTexture,
                610,
                405,
                80,
                42
            );
        }
    }

    // =========================================================
    // DRAW HEALTH / MANA BAR
    // =========================================================

    private void drawBar(
        float x,
        float y,
        float width,
        float height,
        float value,
        Texture frame,
        Texture fill,
        boolean reverse) {

        // Convert HP/MP to 0.0 -> 1.0.
        float ratio =
            Math.max(
                0f,
                Math.min(
                    1f,
                    value / 100f
                )
            );

        // Draw the complete frame.
        batch.draw(
            frame,
            x,
            y,
            width,
            height
        );

        // Nothing to draw when empty.
        if (ratio <= 0f) {
            return;
        }

        float fillWidth =
            width * ratio;

        // =====================================================
        // P2 REVERSED BAR
        // =====================================================

        if (reverse) {

            /*
             * P2 starts full:
             *
             * ████████████████████
             *
             * After taking damage:
             *
             * ██████████████------
             *
             * The fill remains anchored on the LEFT.
             *
             * Therefore the empty part grows from
             * LEFT -> RIGHT.
             */

            batch.draw(
                fill,
                x,
                y,
                fillWidth,
                height
            );

        }

        // =====================================================
        // P1 NORMAL BAR
        // =====================================================

        else {

            /*
             * P1 starts full:
             *
             * ████████████████████
             *
             * After taking damage:
             *
             * ------██████████████
             *
             * The fill remains anchored on the RIGHT.
             *
             * Therefore the empty part grows from
             * RIGHT -> LEFT.
             */

            batch.draw(
                fill,
                x + width - fillWidth,
                y,
                fillWidth,
                height
            );
        }
    }

    // =========================================================
    // DRAW TEXT
    // =========================================================

    private void drawText(
        String text,
        float x,
        float y,
        float size) {

        font.getData().setScale(
            size / 15f
        );

        font.draw(
            batch,
            text,
            x,
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

        /*
         * FitViewport keeps the game world at
         * 960 x 540 and scales it to the window.
         *
         * This keeps the game layout consistent
         * when maximizing or resizing the window.
         */

        viewport.update(
            width,
            height,
            true
        );
    }

    // =========================================================
    // SCREEN LIFECYCLE
    // =========================================================

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

        hpFrame.dispose();
        hpFill.dispose();

        mpFrame.dispose();
        mpFill.dispose();

        redPortrait.dispose();
        bluePortrait.dispose();

        hitTexture.dispose();

        p1.dispose();
        p2.dispose();

        if (auraAnimation != null
            && auraAnimation.getKeyFrames().length > 0) {

            auraAnimation
                .getKeyFrames()[0]
                .getTexture()
                .dispose();
        }
    }
}
