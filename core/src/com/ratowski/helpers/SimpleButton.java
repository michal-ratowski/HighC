package com.ratowski.helpers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class SimpleButton {

    public float x, y, width, height;
    private TextureRegion buttonUpTexture;
    private TextureRegion buttonDownTexture;
    private Rectangle buttonBounds;
    public boolean isPressed = false;

    public SimpleButton(float x, float y, float width, float height, TextureRegion buttonUpTexture, TextureRegion buttonDownTexture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buttonUpTexture = buttonUpTexture;
        this.buttonDownTexture = buttonDownTexture;
        buttonBounds = new Rectangle(x, y, width, height);
    }

    public void draw(SpriteBatch spriteBatch) {
        if (isPressed) {
            spriteBatch.draw(buttonDownTexture, x, y, width, height);
        } else {
            spriteBatch.draw(buttonUpTexture, x, y, width, height);
        }
    }

    public boolean isTouchDown(int screenX, int screenY) {
        if (buttonBounds.contains(screenX, screenY)) {
            isPressed = true;
            return true;
        }
        return false;
    }

    public boolean isTouchUp(int screenX, int screenY) {
        if (buttonBounds.contains(screenX, screenY) && isPressed) {
            isPressed = false;

            if(AssetManager.soundEnabled) {
                AssetManager.clickSound.play(0.5f);
            }
            return true;
        }

        isPressed = false;
        return false;
    }
}
