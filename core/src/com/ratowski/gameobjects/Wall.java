package com.ratowski.gameobjects;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.ratowski.helpers.AssetManager;

import java.util.Random;

public class Wall extends Scrollable {

    private Rectangle wallRectangle;
    public float wallX, widthBar;
    public int wallPitch, meanWallPitch;
    public float wallHealth, wallMaxHealth;
    public int noteNumber;
    public boolean isActive = false;
    public boolean scoreUpdated = false;
    public boolean isContinuous;
    public int currentSinger;
    public boolean isVisible;
    public int wallNumber;
    public int missionSineVariant;
    public int currentMissionNumber;

    private final int WALL_VISIBILITY_THRESHOLD = 280;
    private final int WALL_ACTIVE_THRESHOLD = 265;

    public Wall(float x, float y, int width, int height,
                float scrollSpeed, int currentSinger, int noteNumber,
                int wallHealth, boolean wallContinuous, int currentChalenge,
                int wallNumber, int missionSineVariant) {

        super(x, y, width, height, scrollSpeed);
        wallRectangle = new Rectangle();
        this.currentSinger = currentSinger;
        this.wallHealth = wallHealth;
        this.wallNumber = wallNumber;
        this.currentMissionNumber = currentChalenge;
        this.missionSineVariant = missionSineVariant;

        if (!wallContinuous) {
            this.noteNumber = setRandomNoteNumber();
        }
        else {
            this.noteNumber = noteNumber;
        }

        if (!wallContinuous) {
            wallMaxHealth = setWallHealth();
        }
        else {
            wallMaxHealth = wallHealth;
        }

        this.wallHealth = wallMaxHealth;
        this.width = (int) this.wallHealth;
        this.isContinuous = wallContinuous;

        wallPitch = setWallPitchFromNoteNumber();
        meanWallPitch = wallPitch;

        isActive = false;
        isVisible = false;

        this.wallX = x;
        this.widthBar = width;
    }

    public Rectangle getWallRectangle() {
        return wallRectangle;
    }

    @Override
    public void updateScrollable(float delta, float runTime) {
        super.updateScrollable(delta, runTime);

        wallX = position.x + width * ((wallMaxHealth - wallHealth) / wallMaxHealth);
        widthBar = width * (wallHealth / wallMaxHealth);

        updateSinusoidalWallPitch(runTime);
        wallRectangle.set(wallX, position.y, widthBar, height);

        updateWallVisibility();
        updateWallActiveness();

    }

    @Override
    public void reset(float newX, float scrollSpeed) {
        isActive = false;
        scoreUpdated = false;
        noteNumber = setRandomNoteNumber();
        wallPitch = setWallPitchFromNoteNumber();
        meanWallPitch = wallPitch;
        wallMaxHealth = setWallHealth();
        wallHealth = wallMaxHealth;
        width = (int) wallHealth;
        velocity.x = scrollSpeed;
        super.reset(newX, scrollSpeed);
    }

    public boolean collides(Player player) {
        if (position.x < player.getX() + player.getWidth() && wallHealth > 0) {
            return (Intersector.overlaps(player.getBoundingCircle(), wallRectangle));
        }
        return false;
    }

    public void addHealth(int howMuchToAdd) {
        if (wallHealth > 0) {
            wallHealth += howMuchToAdd;
        }
    }

    public void setVelocity(float scrollSpeed) {
        velocity.x = scrollSpeed;
    }

    private int setRandomNoteNumber() {

        // Sinusoidal walls
        if (!(missionSineVariant == 0)) {
            Random random = new Random();
            if (currentSinger == 5) {
                return random.nextInt(14) + 9;
            } else {
                return random.nextInt(14) + currentSinger * 5 + 1;
            }
        }

        // Continuous missions' walls
        if (isContinuous) {
            return 0;
        }

        // All other walls
        else {
            Random random = new Random();
            if (currentSinger == 5) {
                return random.nextInt(22) + 7;
            } else if (currentSinger == 4) {
                return random.nextInt(24) + 18;
            } else {
                return random.nextInt(24) + currentSinger * 5 - 4;
            }
        }
    }

    private int setWallPitchFromNoteNumber() {
        int wallPitch = (int) AssetManager.noteFrequencies[noteNumber];
        return wallPitch;
    }

    private int setWallHealth() {

        // Free play
        if (currentMissionNumber != -1) {
            return AssetManager.missionNotesNewHealthArray[currentMissionNumber];
        }

        // Missions
        else {
            Random random = new Random();
            return random.nextInt(5) * 10 + 40;
        }
    }

    private void updateSinusoidalWallPitch(float runTime) {
        if (missionSineVariant != 0) {
            wallPitch = (int) (meanWallPitch + 18.54 * Math.exp(0.057 * (noteNumber + 1)) * Math.sin(missionSineVariant * runTime));
        }
    }

    private void updateWallVisibility(){
        if (position.x < WALL_VISIBILITY_THRESHOLD) {
            isVisible = true;
        }
    }

    private void updateWallActiveness(){
        if (position.x < WALL_ACTIVE_THRESHOLD && wallHealth > 0){
            isActive = true;
        }
    }
}
