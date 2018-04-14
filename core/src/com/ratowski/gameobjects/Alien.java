package com.ratowski.gameobjects;

import com.ratowski.voicegame.GameWorld;

public class Alien {

    float x, y;
    int width, height;
    GameWorld gameWorld;
    int cool = 0;
    int wait = 0;
    public int coolTimeThreshold = 100;
    public int waitTimeThreshold = 10;

    public enum State {NEUTRAL, COOL, BEFORE_COOL, MEH, BEFORE_MEH, READY, SUCCESS}

    State state;

    public Alien(float x, float y, int width, int height, GameWorld gameWorld) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gameWorld = gameWorld;
        this.state = State.READY;
    }


    public void update() {

        if (gameWorld.gameEnded()) {
            gameWorld.deactivateAliens();
        }
        else if (gameWorld.stateIsReadyToPlay()) {
            state = State.READY;
        }

        switch (state) {
            case COOL:
                if (cool < coolTimeThreshold) {
                    cool++;
                } else {
                    cool = 0;
                    state = State.NEUTRAL;
                }
                break;
            case BEFORE_COOL:
                if (wait < waitTimeThreshold) {
                    wait++;
                } else {
                    wait = 0;
                    state = State.COOL;
                }
                break;
            case BEFORE_MEH:
                if (wait < waitTimeThreshold) {
                    wait++;
                }
                else {
                    wait = 0;
                    state = State.MEH;
                }
                break;
            default:
                break;
        }


    }

    public void reset() {
        state = State.NEUTRAL;
    }

    // Getters and setters
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
