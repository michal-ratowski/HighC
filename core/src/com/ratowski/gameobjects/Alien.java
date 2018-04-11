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

    public enum AlienState {NEUTRAL, COOL, BEFORE_COOL, MEH, BEFORE_MEH, READY, SUCCESS}

    AlienState alienState;

    public Alien(float x, float y, int width, int height, GameWorld gameWorld) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.gameWorld = gameWorld;
        this.alienState = AlienState.READY;
    }


    public void update() {

        if (gameWorld.gameEnded()) {
            gameWorld.deactivateAliens();
        }
        else if (gameWorld.stateIsReadyToPlay()) {
            alienState = AlienState.READY;
        }

        switch (alienState) {
            case COOL:
                if (cool < coolTimeThreshold) {
                    cool++;
                } else {
                    cool = 0;
                    alienState = AlienState.NEUTRAL;
                }
                break;
            case BEFORE_COOL:
                if (wait < waitTimeThreshold) {
                    wait++;
                } else {
                    wait = 0;
                    alienState = AlienState.COOL;
                }
                break;
            case BEFORE_MEH:
                if (wait < waitTimeThreshold) {
                    wait++;
                }
                else {
                    wait = 0;
                    alienState = AlienState.MEH;
                }
                break;
            default:
                break;
        }


    }

    public void reset() {
        alienState = AlienState.NEUTRAL;
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

    public AlienState getAlienState() {
        return alienState;
    }

    public void setAlienState(AlienState alienState) {
        this.alienState = alienState;
    }
}
