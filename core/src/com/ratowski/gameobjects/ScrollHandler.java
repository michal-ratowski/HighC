package com.ratowski.gameobjects;

import com.badlogic.gdx.Gdx;
import com.ratowski.helpers.AssetManager;
import com.ratowski.voicegame.GameWorld;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ScrollHandler {

    private Sky frontSky, backSky;
    private GameWorld gameWorld;
    private ArrayList<Wall> walls;
    private Wall activeWall;

    float gameHeight;
    public static int WALL_SCROLL_SPEED = -20;
    public static int SKY_SCROLL_SPEED = -200;
    public static int WALL_INITIAL_POSITION_X = 280;
    public boolean gameOverStopShowingWalls = false;
    public int currentWallNumber = 1;
    public int currentWallScore = 0;
    public boolean isStopped = false;


    public ScrollHandler(GameWorld gameWorld, float gameHeight) {
        this.gameWorld = gameWorld;
        this.gameHeight = gameHeight;

        frontSky = new Sky(0, 0, 272, 490, SKY_SCROLL_SPEED);
        backSky = new Sky(272, 0, 272, 490, SKY_SCROLL_SPEED);

        if (gameWorld.missionMode && gameWorld.continuousWalls)
            WALL_SCROLL_SPEED = AssetManager.missionTempos[gameWorld.currentMissionNumber];
        walls = new ArrayList<Wall>();

    }

    public void updateMenuState(float delta, float runTime) {
        frontSky.updateScrollable(delta, runTime);
        backSky.updateScrollable(delta, runTime);
        if (frontSky.isScrolledLeft()) {
            frontSky.reset(backSky.getTailX(), SKY_SCROLL_SPEED);
        } else if (backSky.isScrolledLeft()) {
            backSky.reset(frontSky.getTailX(), SKY_SCROLL_SPEED);
        }
    }

    public void updateNotMenuState(float delta, float runTime) {
        frontSky.updateScrollable(delta, runTime);
        backSky.updateScrollable(delta, runTime);

        for (Wall wall : walls) {
            wall.updateScrollable(delta, runTime);
        }

        // CONTINUOUS WALLS
        if (gameWorld.missionMode && gameWorld.continuousWalls) {
            updateContinuousWallsScroller();
        }

        // ONE WALL AT A TIME ////////////////////////////////////////////////////
        else {
            updateOneWallAtATimeScroller();
        }

        // SKY /////////////////////////////////////////////////////////
        resetSkyIfNecessary();

        // WALL DESTROYED
        if (activeWall.wallHealth <= 0 && !activeWall.scoreUpdated) {

            activeWall.scoreUpdated = true;
            currentWallScore = 0;

            if (!gameWorld.missionMode) {
                gameWorld.addScore(1);
            } else {
                gameWorld.crushedWallsCounter++;
            }

            gameWorld.activateAliens();

            Random random = new Random();
            gameWorld.createMonit(AssetManager.monitsTextureRegions[random.nextInt(5)], 2, 1);

            if (AssetManager.vibrationEnabled) {
                Gdx.input.vibrate(200);
            }

            activeWall.velocity.set(-200, 0);

            if (gameWorld.missionMode && walls.size() > 1) {
                for (int i = 1; i < walls.size(); i++) {
                    if (walls.get(i).wallHealth > 0) {
                        activeWall = walls.get(i);
                        break;
                    }
                }
            }
        }
    }

    public Wall getActiveWall() {
        return activeWall;
    }

    public Sky getFrontSky() {
        return frontSky;
    }

    public Sky getBackSky() {
        return backSky;
    }

    public void stopScrolling() {
        frontSky.stopScrolling();
        backSky.stopScrolling();
        for (Wall wall : walls) {
            wall.stopScrolling();
        }
        isStopped = true;
    }

    public void startScrolling() {
        frontSky.startScrolling();
        backSky.startScrolling();
        for (Wall wall : walls) {
            wall.startScrolling();
        }
        isStopped = false;
    }

    public boolean collides(Player player) {
        return (activeWall.collides(player));
    }


    public void onRestart() {

        if (gameWorld.missionMode) {
            WALL_SCROLL_SPEED = AssetManager.missionTempos[gameWorld.currentMissionNumber];
        }
        else {
            WALL_SCROLL_SPEED = -20;
        }

        frontSky.onRestart(0, SKY_SCROLL_SPEED);
        backSky.onRestart(frontSky.getTailX(), SKY_SCROLL_SPEED);

        if (gameWorld.missionMode && gameWorld.continuousWalls) {
            beginContinuous();
        } else {
            beginOneByOne();
        }
        gameOverStopShowingWalls = false;
        currentWallNumber = 1;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public void beginOneByOne() {
        walls.clear();
        Wall wall;

        if (gameWorld.missionMode && gameWorld.currentMissionNumber == 15)
            wall = new Wall(1000, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, 15, 0, 0);

        else if (gameWorld.missionMode && gameWorld.currentMissionNumber == 30)
            wall = new Wall(280, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, 30, 0, 1);

        else if (gameWorld.missionMode && gameWorld.currentMissionNumber == 45)
            wall = new Wall(280, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, 45, 0, 2);

        else if (gameWorld.missionMode && gameWorld.currentMissionNumber == 59)
            wall = new Wall(280, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, 59, 0, 3);

        else if (gameWorld.missionMode)
            wall = new Wall(280, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, gameWorld.currentMissionNumber, 0, 0);

        else
            wall = new Wall(280, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, -1, 0, 0);

        walls.add(0, wall);
        activeWall = walls.get(0);
    }

    public void beginContinuous() {

        int singerPitchConstant;
        if (gameWorld.currentSingerNumber == 4) singerPitchConstant = 17;
        else if (gameWorld.currentSingerNumber == 5) singerPitchConstant = 5;
        else singerPitchConstant = 5 * (gameWorld.currentSingerNumber - 1);

        walls.clear();

        // First wall created separately
        Wall wall = new Wall(
                280,
                gameHeight / 8,
                1,
                (int) gameHeight * 2 / 3,
                WALL_SCROLL_SPEED,
                gameWorld.currentSingerNumber,
                AssetManager.missionNotesPitchArray[gameWorld.currentMissionNumber][0] + singerPitchConstant,
                AssetManager.missionNotesHealthArray[gameWorld.currentMissionNumber][0],
                true,
                -1,
                1,
                0
        );
        walls.add(0, wall);

        // All other walls
        for (int i = 1; i < AssetManager.missionNotesPitchArray[gameWorld.currentMissionNumber].length; i++) {
            wall = new Wall(
                    walls.get(i - 1).getTailX() + AssetManager.missionNotesIntervalArray[gameWorld.currentMissionNumber][i - 1],
                    gameHeight / 8, 1,
                    (int) gameHeight * 2 / 3,
                    WALL_SCROLL_SPEED,
                    gameWorld.currentSingerNumber,
                    AssetManager.missionNotesPitchArray[gameWorld.currentMissionNumber][i] + singerPitchConstant,
                    AssetManager.missionNotesHealthArray[gameWorld.currentMissionNumber][i],
                    true,
                    -1,
                    i + 1,
                    0
            );
            walls.add(i, wall);
        }

        activeWall = walls.get(0);
    }

    private void resetWallPositionIfNecessary() {
        if (activeWall.position.x > WALL_INITIAL_POSITION_X) {
            activeWall.position.x = WALL_INITIAL_POSITION_X;
        }
    }

    private void resetSkyIfNecessary() {
        if (frontSky.isScrolledLeft()) {
            frontSky.reset(backSky.getTailX(), WALL_SCROLL_SPEED);
        } else if (backSky.isScrolledLeft()) {
            backSky.reset(frontSky.getTailX(), WALL_SCROLL_SPEED);
        }
    }

    private void updateContinuousWallsScroller() {
        if (walls.isEmpty() && !gameWorld.stateIsGameOver() && !gameWorld.stateIsHighScore() && !gameWorld.stateIsSuccess()) {
            gameWorld.success();
        }
        if (gameOverStopShowingWalls) {
            removeUnnecessaryInvisibleWalls();
        }
        removeScrolledLeftWalls();
        resetWallPositionIfNecessary();
    }

    private void updateOneWallAtATimeScroller() {
        if (activeWall.isScrolledLeft()) {
            boolean missionSuccess = true;

            for (int i = 0; i < AssetManager.missionsWithPredefinedWalls.length; i++) {
                if (gameWorld.currentMissionNumber == AssetManager.missionsWithPredefinedWalls[i])
                    missionSuccess = false;
            }

            if (gameWorld.missionMode && missionSuccess) {
                if (currentWallNumber == AssetManager.missionSuccesses[gameWorld.currentMissionNumber] && gameWorld.currentGameState == GameWorld.GameState.RUNNING) {
                    gameWorld.success();
                }
            }

            if (!gameOverStopShowingWalls) {
                currentWallNumber++;
                activeWall.reset(WALL_INITIAL_POSITION_X, WALL_SCROLL_SPEED);

                if (gameWorld.currentMissionNumber == 61 && currentWallNumber == AssetManager.missionSuccesses[61]) {
                    activeWall.wallHealth = 500;
                }
            }
        }
    }

    private void removeUnnecessaryInvisibleWalls() {
        for (Iterator<Wall> iter = walls.listIterator(); iter.hasNext(); ) {
            Wall wall = iter.next();
            if (!wall.wallVisible) {
                iter.remove();
            }
        }
    }

    private void removeScrolledLeftWalls() {
        for (Iterator<Wall> iter = walls.listIterator(); iter.hasNext(); ) {
            Wall wall = iter.next();
            if (wall.isScrolledLeft) {
                iter.remove();
            }
        }
    }

}
