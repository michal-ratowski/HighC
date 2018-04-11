package com.ratowski.gameobjects;

import com.badlogic.gdx.math.Vector2;

public class Scrollable {

    protected Vector2 position;
    protected Vector2 velocity;
    protected int width;
    protected int height;
    protected float velocityX;
    protected boolean isScrolledLeft;
    protected float scrollSpeed;

    public Scrollable(float x, float y, int width, int height, float scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
        position = new Vector2(x, y);
        velocity = new Vector2(this.scrollSpeed, 0);
        this.width = width;
        this.height = height;
        isScrolledLeft = false;
    }

    public void startScrolling() {
        velocity.x = velocityX;
    }

    public void stopScrolling() {
        velocityX = velocity.x;
        velocity.x = 0;
    }

    public void updateScrollable(float delta, float runTime) {
        position.add(velocity.cpy().scl(delta));
        if (position.x + width < 0) {
            isScrolledLeft = true;
        }
    }

    public void reset(float newX, float scrollSpeed) {
        position.x = newX;
        isScrolledLeft = false;
        this.scrollSpeed = scrollSpeed;
    }

    public boolean isScrolledLeft() {
        return isScrolledLeft;
    }

    public float getTailX() {
        return position.x + width;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
