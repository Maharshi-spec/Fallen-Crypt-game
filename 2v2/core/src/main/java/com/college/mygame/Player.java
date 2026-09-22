package com.college.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player {
    public enum State { IDLE, WALK, JUMP, ATTACK, BLOCK, HIT, SPECIAL }

    private static final float SPRITE_SIZE = 88f;
    private static final float MOVE_SPEED = 260f;
    private static final float JUMP_SPEED = 520f;
    private static final float GRAVITY = -1400f;
    private static final float GROUND_Y = 92f;
    private static final float MAX_HP = 100f;
    private static final float MAX_MP = 100f;

    private final boolean playerOne;
    private final Vector2 position = new Vector2();
    private final Vector2 velocity = new Vector2();
    private final Rectangle body = new Rectangle();
    private final Rectangle attackBox = new Rectangle();

    private final Animation<TextureRegion> idle;
    private final Animation<TextureRegion> walk;
    private final Animation<TextureRegion> jump;
    private final Animation<TextureRegion> attack;
    private final Animation<TextureRegion> block;
    private final Animation<TextureRegion> hit;
    private final Animation<TextureRegion> special;

    private State state = State.IDLE;
    private float stateTime;
    private float hp = MAX_HP;
    private float mp = MAX_MP;
    private boolean grounded = true;
    private boolean facingRight = true;
    private boolean attackHitApplied;
    private boolean specialFired;
    private float hitRecovery = 0f;

    public Player(boolean playerOne, float x, float y) {
        this.playerOne = playerOne;
        position.set(x, y);

        String root = playerOne ? "player1/" : "player2/";
        idle = loadAnimation(root + "idle.png", 6, 0.11f);
        walk = loadAnimation(root + "walk.png", 6, 0.09f);
        jump = loadAnimation(root + "jump.png", 4, 0.10f);
        attack = loadAnimation(root + "attack.png", 6, 0.07f);
        block = loadAnimation(root + "block.png", 6, 0.09f);
        hit = loadAnimation(root + "hit_spell.png", 7, 0.08f);
        special = hit;

        updateBody();
    }

    private Animation<TextureRegion> loadAnimation(String path, int count, float duration) {
        Texture texture = new Texture(Gdx.files.internal(path));
        int frameWidth = texture.getWidth() / count;
        int frameHeight = texture.getHeight();

        TextureRegion[] frames = new TextureRegion[count];
        for (int i = 0; i < count; i++) {
            frames[i] = new TextureRegion(texture, i * frameWidth, 0, frameWidth, frameHeight);
        }
        return new Animation<>(duration, frames);
    }

    public void update(float delta, Player opponent, Array<FlyingSwordAura> projectiles) {
        stateTime += delta;
        mp = Math.min(MAX_MP, mp + 8f * delta);
        hitRecovery = Math.max(0f, hitRecovery - delta);

        // Always face the opponent. Both knights therefore look toward each other.
        facingRight = opponent.getCenterX() >= getCenterX();

        boolean left = playerOne ? Gdx.input.isKeyPressed(Input.Keys.A)
                                 : Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = playerOne ? Gdx.input.isKeyPressed(Input.Keys.D)
                                  : Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean jumpKey = playerOne ? Gdx.input.isKeyJustPressed(Input.Keys.W)
                                    : Gdx.input.isKeyJustPressed(Input.Keys.UP);
        boolean blockKey = playerOne ? Gdx.input.isKeyPressed(Input.Keys.S)
                                     : Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean attackKey = playerOne ? Gdx.input.isKeyJustPressed(Input.Keys.F)
                                      : Gdx.input.isKeyJustPressed(Input.Keys.L);
        boolean specialKey = playerOne ? Gdx.input.isKeyJustPressed(Input.Keys.E)
                                       : Gdx.input.isKeyJustPressed(Input.Keys.O);

        // Block has priority while the key is held.
        if (blockKey && grounded && hitRecovery <= 0f) {
            velocity.x = 0;
            state = State.BLOCK;
            stateTime += delta;
        }
        // Non-looping sword attack.
        else if (state == State.ATTACK && stateTime < attack.getAnimationDuration()) {
            velocity.x = 0;
        }
        // Non-looping special.
        else if (state == State.SPECIAL && stateTime < special.getAnimationDuration()) {
            velocity.x = 0;

            if (!specialFired && stateTime >= 0.18f) {
                specialFired = true;
                projectiles.add(new FlyingSwordAura(
                        GameScreen.getAuraAnimation(),
                        facingRight ? position.x + 40f : position.x - 40f,
                        position.y + 24f,
                        facingRight ? 1f : -1f,
                        playerOne
                ));
            }
        }
        // Hit reaction.
        else if (state == State.HIT && stateTime < hit.getAnimationDuration()) {
            velocity.x = 0;
        }
        else {
            if (attackKey && grounded && hitRecovery <= 0f) {
                state = State.ATTACK;
                stateTime = 0f;
                attackHitApplied = false;
                velocity.x = 0;
            }
            else if (specialKey && grounded && mp >= 10f && hitRecovery <= 0f) {
                mp -= 10f;
                state = State.SPECIAL;
                stateTime = 0f;
                specialFired = false;
                velocity.x = 0;
            }
            else {
                if (left) velocity.x = -MOVE_SPEED;
                else if (right) velocity.x = MOVE_SPEED;
                else velocity.x = 0;

                if (jumpKey && grounded) {
                    velocity.y = JUMP_SPEED;
                    grounded = false;
                }

                if (!grounded) state = State.JUMP;
                else if (Math.abs(velocity.x) > 0.1f) state = State.WALK;
                else state = State.IDLE;
            }
        }

        velocity.y += GRAVITY * delta;
        position.mulAdd(velocity, delta);

        if (position.y <= GROUND_Y) {
            position.y = GROUND_Y;
            velocity.y = 0;
            grounded = true;
        }

        position.x = Math.max(0f, Math.min(GameScreen.WORLD_W - SPRITE_SIZE, position.x));
        updateBody();
    }

    public void draw(SpriteBatch batch) {
        Animation<TextureRegion> animation;

        switch (state) {
            case WALK: animation = walk; break;
            case JUMP: animation = jump; break;
            case ATTACK: animation = attack; break;
            case BLOCK: animation = block; break;
            case HIT: animation = hit; break;
            case SPECIAL: animation = special; break;
            default: animation = idle;
        }

        boolean looping = state == State.IDLE ||
                          state == State.WALK ||
                          state == State.BLOCK;

        TextureRegion frame = animation.getKeyFrame(stateTime, looping);

        boolean desiredFlip = !facingRight;
        if (frame.isFlipX() != desiredFlip) frame.flip(true, false);

        batch.draw(frame, position.x, position.y, SPRITE_SIZE, SPRITE_SIZE);

        if (frame.isFlipX() != desiredFlip) frame.flip(true, false);
    }

    public boolean canDealMeleeHit() {
        if (state != State.ATTACK || attackHitApplied) return false;

        float t = stateTime;
        if (t >= 0.15f && t <= 0.30f) {
            attackHitApplied = true;
            return true;
        }
        return false;
    }

    public Rectangle getAttackBox() {
        float x = facingRight ? position.x + 54f : position.x - 48f;
        attackBox.set(x, position.y + 20f, 55f, 45f);
        return attackBox;
    }

    public void takeDamage(float damage) {
        if (state == State.BLOCK) {
            damage *= 0.15f; // 85% damage reduction while shield is raised.
        }

        hp = Math.max(0f, hp - damage);
        state = State.HIT;
        stateTime = 0f;
        hitRecovery = 0.12f;
    }

    private void updateBody() {
        body.set(position.x + 16f, position.y + 5f, 56f, 78f);
    }

    public float getHp() { return hp; }
    public float getMp() { return mp; }
    public boolean isDead() { return hp <= 0f; }
    public boolean isBlocking() { return state == State.BLOCK; }
    public boolean isPlayerOne() { return playerOne; }
    public Rectangle getBody() { return body; }
    public float getCenterX() { return position.x + SPRITE_SIZE / 2f; }
    public State getState() { return state; }

    public void dispose() {
        disposeAnimation(idle);
        disposeAnimation(walk);
        disposeAnimation(jump);
        disposeAnimation(attack);
        disposeAnimation(block);
        disposeAnimation(hit);
    }

    private void disposeAnimation(Animation<TextureRegion> animation) {
        if (animation == null || animation.getKeyFrames().length == 0) return;
        animation.getKeyFrames()[0].getTexture().dispose();
    }
}
