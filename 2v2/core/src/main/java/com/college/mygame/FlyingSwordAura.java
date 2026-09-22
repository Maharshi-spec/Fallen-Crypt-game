package com.college.mygame;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class FlyingSwordAura {
    private static final float SIZE = 64f;
    private static final float SPEED = 480f;
    private static final float DAMAGE = 20f;

    private final Animation<TextureRegion> animation;
    private final Rectangle bounds = new Rectangle();
    private final boolean ownerIsPlayerOne;
    private float x, y, direction, stateTime;
    private boolean alive = true;

    public FlyingSwordAura(Animation<TextureRegion> animation,
                           float x, float y,
                           float direction,
                           boolean ownerIsPlayerOne) {
        this.animation = animation;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.ownerIsPlayerOne = ownerIsPlayerOne;
        bounds.set(x, y, SIZE, SIZE);
    }

    public void update(float delta, float worldWidth) {
        stateTime += delta;
        x += SPEED * direction * delta;
        bounds.set(x + 8f, y + 12f, SIZE - 16f, SIZE - 24f);

        if (x > worldWidth + SIZE || x < -SIZE * 2f) {
            alive = false;
        }
    }

    public void draw(SpriteBatch batch) {
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        boolean desiredFlip = direction < 0;

        if (frame.isFlipX() != desiredFlip) frame.flip(true, false);
        batch.draw(frame, x, y, SIZE, SIZE);
        if (frame.isFlipX() != desiredFlip) frame.flip(true, false);
    }

    public boolean belongsToPlayerOne() { return ownerIsPlayerOne; }
    public Rectangle getBounds() { return bounds; }
    public float getDamage() { return DAMAGE; }
    public boolean isAlive() { return alive; }
    public void destroy() { alive = false; }
}
