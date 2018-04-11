package com.ratowski.gameobjects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.ratowski.voicegame.GameWorld;

public class Player {

    private Vector2 position;
    public Vector2 velocity;
    private float originalY;
    private float rotation; // For handling bird rotation
    private int width;
    private int height;
    private GameWorld myWorld;

    private boolean isAlive;

    private Circle boundingCircle;

    public Player(float x, float y, int width, int height, GameWorld myWorld) {
        this.width = width;
        this.height = height;
        this.originalY = y;
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        boundingCircle = new Circle();
        isAlive=true;
        this.myWorld=myWorld;
    }
    public void update() {
        boundingCircle.set(position.x+width/2, position.y+height/2, 30.0f);
    }

    public void updateGameOver(float delta) {
        position.add(velocity.cpy().scl(delta));
    }

    public void onRestart() {
        rotation = 0;
        position.y = 62;
        position.x = 24;
        velocity.x = 0;
        velocity.y = 0;
        isAlive = true;
    }

    public void die() {
        isAlive = false;
        velocity.set(ScrollHandler.WALL_SCROLL_SPEED, 0);
    }

    public float getX() {
        return position.x;
    }
    public float getY() {
        return position.y;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
    public Circle getBoundingCircle() {return boundingCircle;}
    public boolean isAlive() {return isAlive;}


}
