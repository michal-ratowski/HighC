package com.ratowski.helpers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class BistableButton {

    public float x, y, width, height;
    private TextureRegion buttonUp;
    private TextureRegion buttonDown;
    private TextureRegion buttonActive;
    private Rectangle bounds;
    public boolean isActive = false;
    private boolean isPressed = false;

    public BistableButton(float x, float y, float width, float height, TextureRegion buttonUp, TextureRegion buttonDown, TextureRegion buttonActive) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buttonUp = buttonUp;
        this.buttonDown = buttonDown;
        this.buttonActive = buttonActive;
        bounds = new Rectangle(x, y, width, height);
    }

    public void draw(SpriteBatch batcher) {
        if (isActive) {
            batcher.draw(buttonActive, x, y, width, height);
        } else {
            if (isPressed) {
                batcher.draw(buttonDown, x, y, width, height);
            } else {
                batcher.draw(buttonUp, x, y, width, height);
            }
        }
    }

    public boolean isTouchDown(int screenX, int screenY) {
        if (bounds.contains(screenX, screenY)) {
            isPressed = true;
            return true;
        }
        return false;
    }

    public boolean isTouchUp(int screenX, int screenY) {
        if (bounds.contains(screenX, screenY) && isPressed) {
            isPressed = false;
            return true;
        }
        isPressed = false;
        return false;
    }
}
