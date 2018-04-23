package com.ratowski.gameobjects;

import com.ratowski.helpers.AssetManager;
import com.ratowski.voicegame.GameWorld;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ScrollHandler {

    // Game objects
    private Sky frontSky, backSky;
    private GameWorld gameWorld;
    private ArrayList<Wall> walls;
    private Wall activeWall;

    // Game variables
    float gameHeight;
    public static int WALL_SCROLL_SPEED = -20;
    public static int SKY_SCROLL_SPEED = -200;
    public static int WALL_INITIAL_POSITION_X = 280;
    public static int GAME_WIDTH = 272;
    public static int SKY_HEIGHT = 490;
    public boolean gameOverStopShowingWalls = false;
    public int currentWallNumber = 1;
    public int currentWallScore = 0;
    private int singerNoteShiftConstant;
    public boolean isStopped = false;
    private boolean missionWithoutPredefinedWalls = false;

    public ScrollHandler(GameWorld gameWorld, float gameHeight) {
        this.gameWorld = gameWorld;
        this.gameHeight = gameHeight;
        frontSky = new Sky(0, 0, GAME_WIDTH, SKY_HEIGHT, SKY_SCROLL_SPEED);
        backSky = new Sky(GAME_WIDTH, 0, GAME_WIDTH, SKY_HEIGHT, SKY_SCROLL_SPEED);
        walls = new ArrayList<Wall>();
        getContinuousMissionScrollSpeed();
    }

    private void getContinuousMissionScrollSpeed() {
        if (gameWorld.missionMode && gameWorld.continuousWalls) {
            WALL_SCROLL_SPEED = AssetManager.missionTempos[gameWorld.currentMissionNumber];
        }
    }

    public void updateMenuState(float delta, float runTime) {
        updateSkies(delta, runTime);
    }

    public void updateNotMenuState(float delta, float runTime) {
        updateWalls(delta, runTime);
        updateSkies(delta, runTime);
        if (gameWorld.missionMode && gameWorld.continuousWalls) {
            updateContinuousWallsScroller();
        } else {
            updateOneWallAtATimeScroller();
        }
        handleWallDestruction();
    }

    private void updateSkies(float delta, float runTime) {
        frontSky.updateScrollable(delta, runTime);
        backSky.updateScrollable(delta, runTime);
        resetSkiesIfNecessary();
    }

    private void updateWalls(float delta, float runTime) {
        for (Wall wall : walls) {
            wall.updateScrollable(delta, runTime);
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
        getWallsScrollSpeed();
        frontSky.onRestart(0, SKY_SCROLL_SPEED);
        backSky.onRestart(frontSky.getTailX(), SKY_SCROLL_SPEED);
        beginScrolling();
        gameOverStopShowingWalls = false;
        currentWallNumber = 1;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    private void getWallsScrollSpeed() {
        if (gameWorld.missionMode) {
            WALL_SCROLL_SPEED = AssetManager.missionTempos[gameWorld.currentMissionNumber];
        } else {
            WALL_SCROLL_SPEED = -20;
        }
    }

    private void beginScrolling() {
        if (gameWorld.missionMode && gameWorld.continuousWalls) {
            beginContinuousScrolling();
        } else {
            beginOneByOneScrolling();
        }
    }

    public void beginOneByOneScrolling() {
        walls.clear();
        Wall wall;

        // Wait for it mission
        if (gameWorld.missionMode && gameWorld.currentMissionNumber == AssetManager.MISSION_WAIT_FOR_IT) {
            wall = new Wall(1000, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, 15, 0, 0);
        }

        // Sinusoidal missions
        else if (gameWorld.missionMode && gameWorld.currentMissionNumber == AssetManager.MISSION_TRIGONOMETRY_101) {
            wall = new Wall(280, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, 30, 0, 1);
        } else if (gameWorld.missionMode && gameWorld.currentMissionNumber == AssetManager.MISSION_TRIGONOMETRY_102) {
            wall = new Wall(280, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, 45, 0, 2);
        } else if (gameWorld.missionMode && gameWorld.currentMissionNumber == AssetManager.MISSION_TRIGONOMETRY_EXAM) {
            wall = new Wall(280, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, 59, 0, 3);
        }

        // Other missions
        else if (gameWorld.missionMode) {
            wall = new Wall(280, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, gameWorld.currentMissionNumber, 0, 0);
        }

        // Free mode
        else {
            wall = new Wall(280, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, 1, 1, false, -1, 0, 0);
        }

        walls.add(0, wall);
        activeWall = walls.get(0);
    }

    public void beginContinuousScrolling() {
        walls.clear();
        getSingerNoteShiftConstant();

        // First wall created separately
        Wall wall = new Wall(280, gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, AssetManager.missionNotesPitchArray[gameWorld.currentMissionNumber][0] + singerNoteShiftConstant, AssetManager.missionNotesHealthArray[gameWorld.currentMissionNumber][0], true, -1, 1, 0);
        walls.add(0, wall);

        // All other walls
        for (int i = 1; i < AssetManager.missionNotesPitchArray[gameWorld.currentMissionNumber].length; i++) {
            wall = new Wall(walls.get(i - 1).getTailX() + AssetManager.missionNotesIntervalArray[gameWorld.currentMissionNumber][i - 1], gameHeight / 8, 1, (int) gameHeight * 2 / 3, WALL_SCROLL_SPEED, gameWorld.currentSingerNumber, AssetManager.missionNotesPitchArray[gameWorld.currentMissionNumber][i] + singerNoteShiftConstant, AssetManager.missionNotesHealthArray[gameWorld.currentMissionNumber][i], true, -1, i + 1, 0);
            walls.add(i, wall);
        }

        activeWall = walls.get(0);
    }

    private void resetWallPositionIfNecessary() {
        if (activeWall.position.x > WALL_INITIAL_POSITION_X) {
            activeWall.position.x = WALL_INITIAL_POSITION_X;
        }
    }

    private void resetSkiesIfNecessary() {
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
            missionWithoutPredefinedWalls = true;
            checkIfMissionWithPredefinedWalls();
            checkForMissionSuccess();
            getNextWall();
        }
    }

    private void checkForMissionSuccess() {
        if (gameWorld.missionMode && missionWithoutPredefinedWalls) {
            if (currentWallNumber == AssetManager.missionSuccesses[gameWorld.currentMissionNumber] && gameWorld.currentGameState == GameWorld.GameState.RUNNING) {
                gameWorld.success();
            }
        }
    }

    private void getSingerNoteShiftConstant() {
        if (gameWorld.currentSingerNumber == 4) {
            singerNoteShiftConstant = 17;
        } else if (gameWorld.currentSingerNumber == 5) {
            singerNoteShiftConstant = 5;
        } else {
            singerNoteShiftConstant = 5 * (gameWorld.currentSingerNumber - 1);
        }
    }

    private void checkIfMissionWithPredefinedWalls() {
        for (int i = 0; i < AssetManager.missionsWithPredefinedWalls.length; i++) {
            if (gameWorld.currentMissionNumber == AssetManager.missionsWithPredefinedWalls[i]) {
                missionWithoutPredefinedWalls = false;
                break;
            }
        }
        missionWithoutPredefinedWalls = true;
    }

    private void removeUnnecessaryInvisibleWalls() {
        for (Iterator<Wall> iter = walls.listIterator(); iter.hasNext(); ) {
            Wall wall = iter.next();
            if (!wall.isVisible) {
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

    private void handleWallDestruction() {
        if (activeWall.wallHealth <= 0 && !activeWall.scoreUpdatedAfterDescruction) {
            activeWall.scoreUpdatedAfterDescruction = true;
            currentWallScore = 0;
            updateWallsCount();
            gameWorld.activateAliens();
            AssetManager.vibrate(200);
            createDestroyedWallMonit();
            scrollDestroyedWallLeft();
            getMissionsNextActiveWall();
        }
    }

    private void updateWallsCount() {
        if (!gameWorld.missionMode) {
            gameWorld.addScore(1);
        } else {
            gameWorld.crushedWallsCounter++;
        }
    }

    private void createDestroyedWallMonit() {
        Random random = new Random();
        gameWorld.createMonit(AssetManager.monitsTextureRegions[random.nextInt(5)], 2, 1);
    }

    private void scrollDestroyedWallLeft() {
        activeWall.velocity.set(-200, 0);
    }

    private void getMissionsNextActiveWall() {
        if (gameWorld.missionMode && walls.size() > 1) {
            for (int i = 1; i < walls.size(); i++) {
                if (walls.get(i).wallHealth > 0) {
                    activeWall = walls.get(i);
                    break;
                }
            }
        }
    }

    private void getNextWall() {
        if (!gameOverStopShowingWalls) {
            currentWallNumber++;
            activeWall.reset(WALL_INITIAL_POSITION_X, WALL_SCROLL_SPEED);
            updateMission61WallHealth();
        }
    }

    private void updateMission61WallHealth() {
        if (gameWorld.currentMissionNumber == AssetManager.MISSION_ALMOST_THERE && currentWallNumber == AssetManager.missionSuccesses[AssetManager.MISSION_ALMOST_THERE]) {
            activeWall.wallHealth = AssetManager.MISSION_61_LAST_WALL_HEALTH;
        }
    }

}