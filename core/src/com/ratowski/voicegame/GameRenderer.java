package com.ratowski.voicegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.ratowski.accessors.Value;
import com.ratowski.accessors.ValueAccessor;
import com.ratowski.gameobjects.Alien;
import com.ratowski.gameobjects.Monit;
import com.ratowski.gameobjects.Player;
import com.ratowski.gameobjects.Sky;
import com.ratowski.gameobjects.Wall;
import com.ratowski.gameobjects.ScrollHandler;
import com.ratowski.helpers.AssetManager;
import com.ratowski.helpers.InputHandler;
import com.ratowski.helpers.SimpleButton;
import com.ratowski.helpers.BistableButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class GameRenderer {

    private GameWorld gameWorld;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    float currentRotation;

    enum panelOptionsEnum {
        PAUSE_PANEL,
        MISSION_PANEL
    }

    private int midPointY;
    private int gameHeight;
    private String textToDisplay;


    // Game Objects
    private Player player;
    private ScrollHandler scroller;
    private Wall activeWall;
    private Sky frontSky, backSky;

    boolean gettingDarker = false;
    boolean gettingLighter = false;
    boolean wasPaused = false;
    boolean successTransition = false;

    private ArrayList<Wall> walls;

    public float voiceLevel = 0;
    private int randomAngle = 0;
    int shakeCameraCounter = 0;
    int shakeConstant = 4;
    int sinusCounter = 0;
    private final int GAME_WIDTH = 272;
    float cameraReturnY;
    boolean firstClockRotation = false;

    Array<ParticleEffectPool.PooledEffect> glassParticleEffects = new Array();
    Array<ParticleEffectPool.PooledEffect> correctPitchParticleEffects = new Array();

    private GlyphLayout glyphLayout;
    private boolean cameraChanged = false;

    // Buttons
    private List<com.ratowski.helpers.SimpleButton> menuButtons, optionsButtons, gameOverButtons, panelButtons, missionsButtons, pauseButtons;
    private List<BistableButton> bistableButtons;

    // Tweening
    private TweenManager manager;
    private Value alpha = new Value();

    public GameRenderer(GameWorld gameWorld, int gameHeight, int midPointY) {
        this.gameWorld = gameWorld;
        this.midPointY = midPointY;
        this.gameHeight = gameHeight;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, GAME_WIDTH, gameHeight);

        spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(camera.combined);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(camera.combined);

        cameraReturnY = camera.position.y;

        initGameObjects();
    }

    private void initGameObjects() {
        player = gameWorld.getPlayer();
        scroller = gameWorld.getScrollHandler();
        activeWall = scroller.getActiveWall();
        frontSky = scroller.getFrontSky();
        backSky = scroller.getBackSky();
        glassParticleEffects = AssetManager.glassParticleEffects;
        correctPitchParticleEffects = AssetManager.correctPitchParticleEffects;
        walls = scroller.getWalls();
        glyphLayout = new GlyphLayout();

        menuButtons = ((InputHandler) Gdx.input.getInputProcessor()).getMenuScreenButtons();
        pauseButtons = ((InputHandler) Gdx.input.getInputProcessor()).getPauseScreenButtons();
        optionsButtons = ((InputHandler) Gdx.input.getInputProcessor()).getOptionsScreenButtons();
        gameOverButtons = ((InputHandler) Gdx.input.getInputProcessor()).getGameOverScreenButtons();
        bistableButtons = ((InputHandler) Gdx.input.getInputProcessor()).getBistableButtons();
        missionsButtons = ((InputHandler) Gdx.input.getInputProcessor()).getChallengeScreenButtons();
        panelButtons = ((InputHandler) Gdx.input.getInputProcessor()).getPanelScreenButtons();
    }


    public void render(float delta, float runTime) {

        clearAll();

        spriteBatch.begin();
        drawSpaceship();
        shakeCameraIfNecessary();
        rotateCameraIfNecessary();
        drawAliensIfNecessary(runTime);
        startTransitionIfNecessary();
        spriteBatch.end();

        shapeRenderer.begin(ShapeType.Filled);
        drawBlackRectangles();
        drawWallsIfNecessary();
        shapeRenderer.end();

        spriteBatch.begin();
        spriteBatch.enableBlending();

        if (gameWorld.stateIsRunning()) {
            drawNoteString();
            drawScore();
            drawPlayer(runTime);
            drawCrushingEffects(delta);
        } else if (gameWorld.stateIsReadyToPlay()) {
            drawGetReadyMonit();
            drawPlayer(runTime);
        } else if (gameWorld.stateIsMenu()) {
            drawMenuUI();

            if (gameWorld.gamePaused) {
                drawPanel(panelOptionsEnum.PAUSE_PANEL);
            }

        } else if (gameWorld.stateIsOptions()) {
            drawOptionsUI();
        } else if (gameWorld.stateIsStats()) {
            drawStatsUI();
        } else if (gameWorld.stateIsHelp()) {
            drawHelpUI();
        } else if (gameWorld.stateIsChallenge()) {
            drawMissionsMenuUI();
            drawMissionPreviewIFNecessary();
        } else if (gameWorld.stateIsGameOver() || gameWorld.stateIsHighScore()) {
            cameraReturn(currentRotation);
            drawPlayer(runTime);
            drawScore();
            spriteBatch.end();
            spriteBatch.begin();
            drawScoreboard(delta);
            if (gameWorld.stateIsHighScore()) {
                drawHighScoreMonit(runTime);
            }
        } else if (gameWorld.stateIsSuccess()) {
            drawPlayer(runTime);
            drawScoreboard(delta);
        }

        spriteBatch.end();

        shapeRenderer.begin(ShapeType.Filled);

        if (gameWorld.stateIsRunning() && !gameWorld.gamePaused) {
            drawVoicePitch();
            if (gameWorld.missionMode && (gameWorld.voiceLevelMission())) {
                drawVoiceLevel();
            }
            drawWallStatus();
        }

        shapeRenderer.end();
        spriteBatch.begin();

        if (gameWorld.stateIsRunning() || gameWorld.stateIsReadyToPlay()) {
            drawGoodPitchEffects(delta);

            if (gameWorld.missionMode && gameWorld.currentMissionNumber == AssetManager.MISSION_ENERGY_SAVING) {
                drawExtraColor(0.8f, AssetManager.fakeBlackTextureRegion);
            } else if (gameWorld.missionMode && gameWorld.currentMissionNumber == AssetManager.MISSION_ROSE_COLORED_GLASSES) {
                drawExtraColor(0.5f, AssetManager.pinkColorTextureRegion);
            } else if (gameWorld.missionMode && gameWorld.currentMissionNumber == AssetManager.MISSION_THINK_FAST_IN_GREEN) {
                drawExtraColor(0.5f, AssetManager.greenColorTextureRegion);
            } else if (gameWorld.missionMode && gameWorld.currentMissionNumber == AssetManager.MISSION_LIGHTS_OUT) {
                drawExtraColor(1f, AssetManager.fakeBlackTextureRegion);
            }

            if (gameWorld.gamePaused) {
                drawPanel(panelOptionsEnum.PAUSE_PANEL);
            }
        }

        if (gameWorld.stateIsGameOver() || gameWorld.stateIsHighScore() || gameWorld.stateIsSuccess()) {
            drawStarEffects(delta);
        }

        drawMonits(delta);
        spriteBatch.end();
        drawTransition(delta);
    }


    private void drawPlayer(float runTime) {

        if (gameWorld.stateIsSuccess()) {
            spriteBatch.draw(AssetManager.singersTextureRegion[gameWorld.currentSingerNumber][6], player.getX(), player.getY(), player.getWidth(), player.getHeight());
        } else if (gameWorld.stateIsReadyToPlay()) {
            spriteBatch.draw(AssetManager.singersTextureRegion[gameWorld.currentSingerNumber][0], player.getX(), player.getY(), player.getWidth(), player.getHeight());
        } else {

            if (player.isAlive()) {
                if (gameWorld.currentPitch > AssetManager.noteFrequencies[20 + 5 * (gameWorld.currentSingerNumber - 1)])
                    spriteBatch.draw(AssetManager.singersAnimation[gameWorld.currentSingerNumber].getKeyFrame(runTime), player.getX(), player.getY(), player.getWidth(), player.getHeight());
                else if (gameWorld.currentPitch > AssetManager.noteFrequencies[12 + 5 * (gameWorld.currentSingerNumber - 1)])
                    spriteBatch.draw(AssetManager.singersTextureRegion[gameWorld.currentSingerNumber][2], player.getX(), player.getY(), player.getWidth(), player.getHeight());
                else if (gameWorld.currentPitch > 65)
                    spriteBatch.draw(AssetManager.singersTextureRegion[gameWorld.currentSingerNumber][1], player.getX(), player.getY(), player.getWidth(), player.getHeight());
                else
                    spriteBatch.draw(AssetManager.singersTextureRegion[gameWorld.currentSingerNumber][0], player.getX(), player.getY(), player.getWidth(), player.getHeight());
            } else {
                spriteBatch.draw(AssetManager.singersTextureRegion[gameWorld.currentSingerNumber][5], player.getX(), player.getY(), player.getWidth(), player.getHeight());
            }
        }
    }

    private void drawAlienRow(ArrayList<Alien> alienList, float runTime) {

        for (int i = 0; i < alienList.size(); i++) {
            if (alienList.get(i).getAlienState() == Alien.AlienState.NEUTRAL || alienList.get(i).getAlienState() == Alien.AlienState.BEFORE_COOL || alienList.get(i).getAlienState() == Alien.AlienState.BEFORE_MEH) {
                spriteBatch.draw(AssetManager.alienTextureRegion[0], alienList.get(i).getX(), alienList.get(i).getY(),
                        alienList.get(i).getWidth(), alienList.get(i).getHeight());
            } else if (alienList.get(i).getAlienState() == Alien.AlienState.COOL) {
                spriteBatch.draw(AssetManager.alienAnimation[0].getKeyFrame(runTime), alienList.get(i).getX(), alienList.get(i).getY(),
                        alienList.get(i).getWidth(), alienList.get(i).getHeight());
            } else if (alienList.get(i).getAlienState() == Alien.AlienState.SUCCESS) {
                spriteBatch.draw(AssetManager.alienTextureRegion[1], alienList.get(i).getX(), alienList.get(i).getY(),
                        alienList.get(i).getWidth(), alienList.get(i).getHeight());
            } else if (alienList.get(i).getAlienState() == Alien.AlienState.READY) {
                spriteBatch.draw(AssetManager.alienTextureRegion[0], alienList.get(i).getX(), alienList.get(i).getY(),
                        alienList.get(i).getWidth(), alienList.get(i).getHeight());
            } else if (alienList.get(i).getAlienState() == Alien.AlienState.MEH) {
                spriteBatch.draw(AssetManager.alienTextureRegion[8], alienList.get(i).getX(), alienList.get(i).getY(),
                        alienList.get(i).getWidth(), alienList.get(i).getHeight());
            }

        }
    }

    private void drawAliens(float runTime) {

        // Pause/unpause alien animation
        if (gameWorld.gamePaused) {
            wasPaused = true;
            AssetManager.alienAnimation[0].setFrameDuration(3600f);
            AssetManager.alienAnimation[1].setFrameDuration(3600f);
        } else {
            if (wasPaused) {
                wasPaused = false;
                AssetManager.alienAnimation[0].setFrameDuration(0.08f);
                AssetManager.alienAnimation[1].setFrameDuration(0.08f);
            }
        }

        if (AssetManager.aliensEnabled) {
            drawAlienRow(gameWorld.alienRow3, runTime);
        }

        spriteBatch.draw(AssetManager.blackColorTextureRegion, 110, gameHeight / 6 + 40, 200, 60);

        if (AssetManager.aliensEnabled)
            drawAlienRow(gameWorld.alienRow2, runTime);

        spriteBatch.draw(AssetManager.blackColorTextureRegion, 100, gameHeight / 6, 200, 60);

        if (AssetManager.aliensEnabled)
            drawAlienRow(gameWorld.alienRow1, runTime);
    }

    private void drawOptionsUI() {

        spriteBatch.draw(AssetManager.playerNamesTextureRegion[gameWorld.currentSingerNumber], 20, 360);
        spriteBatch.draw(AssetManager.optionsTexture, 31, 100);

        for (SimpleButton button : optionsButtons) {
            button.draw(spriteBatch);
        }

        for (int i = 0; i < 4; i++) {
            bistableButtons.get(i).draw(spriteBatch);
        }

        if (AssetManager.secretPlayerUnlocked) {
            bistableButtons.get(4).draw(spriteBatch);
        } else {
            spriteBatch.draw(AssetManager.otherButtonsTextureRegions[4][0], 221, 290, 50, 50);
        }

        for (int i = 5; i < 11; i++) {
            bistableButtons.get(i).draw(spriteBatch);
        }

    }

    private void drawMissionPreviewIFNecessary() {
        if (((InputHandler) Gdx.input.getInputProcessor()).missionPreviewVisible) {
            drawPanel(panelOptionsEnum.MISSION_PANEL);
            drawMissionPanelTexts();
        }
    }

    private void drawMissionsMenuUI() {
        drawMissionPages();
        missionsButtons.get(64).draw(spriteBatch);
        missionsButtons.get(65).draw(spriteBatch);
        missionsButtons.get(66).draw(spriteBatch);
        AssetManager.smallWhiteFont.setColor(1, 1, 1, 1);
    }

    private void drawMissionPanelTexts() {

        glyphLayout.setText(AssetManager.smallWhiteFont, "MISSION " + gameWorld.currentMissionNumber);
        AssetManager.smallWhiteFont.draw(spriteBatch, "MISSION " + gameWorld.currentMissionNumber, 136 - glyphLayout.width / 2, gameHeight / 2 + 85);

        textToDisplay = AssetManager.missionNames[gameWorld.currentMissionNumber - 1];
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallWhiteFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight / 2 + 60);

        for (SimpleButton button : panelButtons) {
            button.draw(spriteBatch);
        }

        for (int i = 1; i <= 5; i++) {
            if (AssetManager.getMissionScore(gameWorld.currentMissionNumber) > AssetManager.missionMaxScores[gameWorld.currentMissionNumber] * 0.1 * i) {
                spriteBatch.draw(AssetManager.starTextureRegion, 43 + (i - 1) * 40, gameHeight / 2 + 5, 26, 26);
            } else {
                spriteBatch.draw(AssetManager.noStarTextureRegion, 43 + (i - 1) * 40, gameHeight / 2 + 5, 26, 26);
            }
        }

        textToDisplay = "HIGH SCORE - " + AssetManager.missionHighScores[gameWorld.currentMissionNumber] + "/" + AssetManager.missionMaxScores[gameWorld.currentMissionNumber];
        glyphLayout.setText(AssetManager.smallYellowFont, textToDisplay);
        AssetManager.smallYellowFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight / 2 - 10);
    }

    private void drawMenuUI() {
        spriteBatch.draw(AssetManager.gameLogoTextureRegion, 0, gameHeight - 180, 272, 118);
        for (SimpleButton button : menuButtons) {
            button.draw(spriteBatch);
        }
    }

    private void drawStatsUI() {

        String tempString;
        optionsButtons.get(0).draw(spriteBatch);

        textToDisplay = "GAMES PLAYED";
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallWhiteFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 20);

        textToDisplay = Integer.toString(AssetManager.gamesPlayed);
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallYellowFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 40);

        textToDisplay = "WALLS CRUSHED";
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallWhiteFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 70);

        textToDisplay = Integer.toString(AssetManager.overallWallsDestroyed);
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallYellowFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 90);

        textToDisplay = "WALLS CRUSHED IN FREE MODE";
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallWhiteFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 120);

        textToDisplay = Integer.toString(AssetManager.freePlayWallsDestroyed);
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallYellowFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 140);

        textToDisplay = "HIGH SCORE IN FREE MODE";
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallWhiteFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 170);

        textToDisplay = Integer.toString(AssetManager.freePlayHighScore);
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallYellowFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 190);

        // Favourite singer
        if (AssetManager.favouriteSingerIndex == -1) {
            tempString = "-";
        } else {
            tempString = AssetManager.favouriteSingerIndex + " (" + AssetManager.favouriteSingerGamesPlayed + " GAMES)";
            if (AssetManager.favouriteSingerGamesPlayed == 1) {
                tempString = AssetManager.favouriteSingerIndex + " (1 GAME)";
            }
        }

        textToDisplay = "FAVOURITE SINGER";
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallWhiteFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 220);

        textToDisplay = tempString;
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallYellowFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 240);

        // Most failed wall
        if (AssetManager.worstWallIndex == -1) {
            tempString = "-";
        } else {
            tempString = AssetManager.noteNames[AssetManager.worstWallIndex] + " (" + AssetManager.worstWallScore + " FAILS)";
        }

        textToDisplay = "MOST FAILED WALL";
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallWhiteFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 270);

        textToDisplay = tempString;
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallYellowFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 290);

        tempString = AssetManager.missionsCompleted + "/64";

        // Missions completed
        textToDisplay = "MISSIONS COMPLETED";
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallWhiteFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 320);

        textToDisplay = tempString;
        glyphLayout.setText(AssetManager.smallYellowFont, textToDisplay);
        AssetManager.smallYellowFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, gameHeight - 340);

    }


    private void drawMonits(float delta) {
        for (Iterator<Monit> iter = gameWorld.monits.listIterator(); iter.hasNext(); ) {
            Monit monit = iter.next();
            if (!monit.finished) {
                monit.draw(delta, spriteBatch);
            } else {
                iter.remove();
            }
        }
    }

    private void drawSpaceship() {
        spriteBatch.enableBlending();
        spriteBatch.draw(AssetManager.skyTextureRegion, frontSky.getX(), frontSky.getY(), frontSky.getWidth(), frontSky.getHeight());
        spriteBatch.draw(AssetManager.skyTextureRegion, backSky.getX(), backSky.getY(), backSky.getWidth(), backSky.getHeight());

        if (gameWorld.stateIsRunning() || gameWorld.stateIsGameOver() || gameWorld.stateIsHighScore() || gameWorld.stateIsReadyToPlay() || gameWorld.stateIsSuccess()) {
            spriteBatch.draw(AssetManager.metalTextureRegion, -205, -105, 682, gameHeight + 210);
        }
    }


    private void drawScoreboard(float delta) {
        drawSuccessTransition(delta);

        if (gameWorld.blackAlphaValue.getValue() >= 0.55) {

            if (gameWorld.missionMode) {
                if (gameWorld.stateIsSuccess()) {
                    spriteBatch.draw(AssetManager.successScoreboardTextureRegion, 10, midPointY - 82, 252, 160);
                } else {
                    spriteBatch.draw(AssetManager.missionModeGameOverScoreboardTexture, 10, midPointY - 82, 252, 160);
                }

                textToDisplay = "" + gameWorld.getScore();
                glyphLayout.setText(AssetManager.scoreFont, textToDisplay);

                displayMissionTexts();


                if (gameWorld.allStarsDrawn) {
                    for (int i = 0; i < gameOverButtons.size() - 1; i++) {
                        gameOverButtons.get(i).draw(spriteBatch);
                    }
                    if (gameWorld.stateIsSuccess() || AssetManager.missionsCompleted >= gameWorld.currentMissionNumber) {
                        gameOverButtons.get(gameOverButtons.size() - 1).draw(spriteBatch);
                    }
                }
            } else {

                spriteBatch.draw(AssetManager.gameOverScoreboardTextureRegion, 10, midPointY - 82, 252, 160);
                if (gameWorld.allStarsDrawn) {

                    for (int i = 0; i < gameOverButtons.size() - 1; i++) {
                        gameOverButtons.get(i).draw(spriteBatch);
                    }
                }

                textToDisplay = "" + gameWorld.getScore();
                glyphLayout.setText(AssetManager.scoreFont, textToDisplay);
                AssetManager.scoreFont.draw(spriteBatch, textToDisplay, 73 - glyphLayout.width / 2, midPointY - 33);

                textToDisplay = "" + AssetManager.freePlayHighScore;
                glyphLayout.setText(AssetManager.scoreFont, textToDisplay);
                AssetManager.scoreFont.draw(spriteBatch, textToDisplay, 199 - glyphLayout.width / 2, midPointY - 33);

            }

            drawScoreboardStars();
        }

    }

    private void drawExtraColor(float alpha, TextureRegion textureRegion) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Color c = spriteBatch.getColor();
        spriteBatch.setColor(c.r, c.g, c.b, alpha);
        spriteBatch.draw(textureRegion, -5, -5, 277, gameHeight + 10);
        spriteBatch.setColor(c.r, c.g, c.b, 1);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawPanel(panelOptionsEnum option) {

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Color c = spriteBatch.getColor();
        spriteBatch.setColor(c.r, c.g, c.b, 0.5f);
        spriteBatch.draw(AssetManager.fakeBlackTextureRegion, 0, 0, 272, gameHeight);
        spriteBatch.setColor(c.r, c.g, c.b, 1);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        if (option == panelOptionsEnum.PAUSE_PANEL) {
            spriteBatch.draw(AssetManager.pausePanelTexture, 36, gameHeight / 2 - 60, 200, 120);
            for (SimpleButton button : pauseButtons) {
                button.draw(spriteBatch);
            }
        } else if (option == panelOptionsEnum.MISSION_PANEL) {
            spriteBatch.draw(AssetManager.challengePanelTexture, 11, gameHeight / 2 - 100, 250, 200);
            for (SimpleButton button : panelButtons) {
                button.draw(spriteBatch);
            }
        }
    }

    private void drawMissionPages() {

        int howmuch;

        if (gameWorld.currentMissionPage == 1) {

            if (AssetManager.missionsCompleted < 16) {
                howmuch = AssetManager.missionsCompleted;
            } else {
                howmuch = 16;
            }

            for (int i = 0; i < howmuch; i++) {
                spriteBatch.draw(AssetManager.blackRectangleTexture, missionsButtons.get(i).x, missionsButtons.get(i).y - 15, 50, 30);
                missionsButtons.get(i).draw(spriteBatch);
                drawMenuStars(i);
            }

            if (AssetManager.missionsCompleted < 16) {
                missionsButtons.get(AssetManager.missionsCompleted).draw(spriteBatch);

                for (int i = AssetManager.missionsCompleted + 1; i < 16; i++) {
                    spriteBatch.draw(AssetManager.otherButtonsTextureRegions[4][0], missionsButtons.get(i).x, missionsButtons.get(i).y, 50, 50);
                }
            }
        } else if (gameWorld.currentMissionPage == 2) {

            //System.out.println("Challenges completed: "+AssetManager.missionsCompleted);

            if (AssetManager.missionsCompleted >= 16 && AssetManager.missionsCompleted < 32)
                howmuch = AssetManager.missionsCompleted;
            else if (AssetManager.missionsCompleted < 16) howmuch = 14;
            else howmuch = 32;

            for (int i = 16; i < howmuch; i++) {
                spriteBatch.draw(AssetManager.blackRectangleTexture, missionsButtons.get(i).x, missionsButtons.get(i).y - 15, 50, 30);
                missionsButtons.get(i).draw(spriteBatch);
                drawMenuStars(i);
            }

            if (AssetManager.missionsCompleted >= 16 && AssetManager.missionsCompleted < 32) {
                missionsButtons.get(AssetManager.missionsCompleted).draw(spriteBatch);
            }

            for (int i = howmuch + 1; i < 32; i++) {
                spriteBatch.draw(AssetManager.otherButtonsTextureRegions[4][0], missionsButtons.get(i).x, missionsButtons.get(i).y, 50, 50);
            }

        } else if (gameWorld.currentMissionPage == 3) {

            if (AssetManager.missionsCompleted >= 32 && AssetManager.missionsCompleted < 48)
                howmuch = AssetManager.missionsCompleted;
            else if (AssetManager.missionsCompleted < 32) howmuch = 30;
            else howmuch = 48;

            for (int i = 32; i < howmuch; i++) {
                spriteBatch.draw(AssetManager.blackRectangleTexture, missionsButtons.get(i).x, missionsButtons.get(i).y - 15, 50, 30);
                missionsButtons.get(i).draw(spriteBatch);
                drawMenuStars(i);
            }

            if (AssetManager.missionsCompleted >= 32 && AssetManager.missionsCompleted < 48) {
                missionsButtons.get(AssetManager.missionsCompleted).draw(spriteBatch);
            }

            for (int i = howmuch + 1; i < 48; i++) {
                spriteBatch.draw(AssetManager.otherButtonsTextureRegions[4][0], missionsButtons.get(i).x, missionsButtons.get(i).y, 50, 50);
            }
        } else if (gameWorld.currentMissionPage == 4) {

            if (AssetManager.missionsCompleted >= 48) howmuch = AssetManager.missionsCompleted;
            else if (AssetManager.missionsCompleted < 48) howmuch = 46;
            else howmuch = 64;

            for (int i = 48; i < howmuch; i++) {
                spriteBatch.draw(AssetManager.blackRectangleTexture, missionsButtons.get(i).x, missionsButtons.get(i).y - 15, 50, 30);
                missionsButtons.get(i).draw(spriteBatch);
                drawMenuStars(i);
            }

            if (AssetManager.missionsCompleted >= 48 && AssetManager.missionsCompleted < 64) {
                missionsButtons.get(AssetManager.missionsCompleted).draw(spriteBatch);
            }

            for (int i = howmuch + 1; i < 64; i++) {
                spriteBatch.draw(AssetManager.otherButtonsTextureRegions[4][0], missionsButtons.get(i).x, missionsButtons.get(i).y, 50, 50);
            }
        }
    }

    private void drawMenuStars(int i) {
        for (int j = 1; j < 6; j++) {
            if (AssetManager.getMissionScore(i + 1) > AssetManager.missionMaxScores[i + 1] * 0.1 * j)
                spriteBatch.draw(AssetManager.starTextureRegion, missionsButtons.get(i).x - 5 + 9 * j, missionsButtons.get(i).y - 8, 6, 6);
            else
                spriteBatch.draw(AssetManager.noStarTextureRegion, missionsButtons.get(i).x - 5 + 9 * j, missionsButtons.get(i).y - 8, 6, 6);
        }
    }

    private void drawScoreboardStars() {
        for (int i = 0; i < 5; i++) {
            spriteBatch.draw(AssetManager.noStarTextureRegion, 41 + 40 * i, midPointY + 8, 26, 26);
            if (gameWorld.starsAlreadyDrawnArray[i]) {
                spriteBatch.draw(AssetManager.starTextureRegion, 41 + 40 * i, midPointY + 8, 26, 26);
            }
        }
    }

    // WALLS

    private void drawVisibleWalls() {

        for (Wall wall : walls) {
            float color = wall.noteNumber * 6;

            if (wall.wallHealth > 0) {
                shapeRenderer.setColor(color / 255.0f, 255 / 255.0f, 255 / 255.0f, 1);
                shapeRenderer.rect(wall.getWallRectangle().getX(), wall.getWallRectangle().getY(), wall.getWallRectangle().getWidth(), wall.getWallRectangle().getHeight());
            }

            shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1);
            shapeRenderer.rect(wall.getX(), wall.getY() + wall.getHeight(), wall.getWidth(), 10);
            shapeRenderer.rect(wall.getX() + wall.getWidth() / 2 - 2, wall.getY() + wall.getHeight() + 10, 4, 20);
            shapeRenderer.rect(-200, wall.getY() + wall.getHeight() + 30, 672, 2);
        }

    }

    private void drawWallStatus() {

        if (gameWorld.missionMode) {
            int w = gameWorld.currentMissionNumber;
            if (w == 4 || w == 8 || w == 12 || w == 16 || w == 20 || w == 24 || w == 28 || w == 32
                    || w == 36 || w == 40 || w == 44 || w == 48 || w == 52 || w == 56 || w == 60 || w == 64) {
                shapeRenderer.setColor(50 / 255.0f, 50 / 255.0f, 50 / 255.0f, 1);
                shapeRenderer.rect(8, 8, 256, 14);
                shapeRenderer.setColor(0, 0, 0, 1);
                shapeRenderer.rect(10, 10, 252, 10);
                float howmuch = (((float) (activeWall.wallNumber - 1) / (float) AssetManager.missionNotesHealthArray[gameWorld.currentMissionNumber].length) * 252);
                shapeRenderer.setColor(0, 0.5f, 0, 1);
                shapeRenderer.rect(10, 10, howmuch, 10);
            }
        }
    }


    private void drawCrushingEffects(float delta) {
        for (int i = glassParticleEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = glassParticleEffects.get(i);
            effect.draw(spriteBatch, delta);
            effect.draw(spriteBatch, delta);
        }
    }

    private void drawGoodPitchEffects(float delta) {
        for (int i = correctPitchParticleEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = correctPitchParticleEffects.get(i);
            effect.draw(spriteBatch, delta);
        }

        for (int i = correctPitchParticleEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = correctPitchParticleEffects.get(i);
            effect.draw(spriteBatch, delta);
        }

    }

    private void drawStarEffects(float delta) {
        for (int i = AssetManager.starParticleEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = AssetManager.starParticleEffects.get(i);
            effect.draw(spriteBatch, delta);
        }
    }

    private void drawVoicePitch() {

        shapeRenderer.setColor(50 / 255.0f, 50 / 255.0f, 50 / 255.0f, 1);
        shapeRenderer.rect(6, midPointY - 172, 16, 324);

        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(8, midPointY - 170, 12, 320);

        float wallPitchLevel, voicePitchLevel, thresholdHeight;

        if (gameWorld.currentSingerNumber == 1) {
            wallPitchLevel = (float) (221.6 * Math.log(activeWall.wallPitch) - 1016);
            voicePitchLevel = (float) (221.6 * Math.log(gameWorld.currentPitch) - 1016);
        } else if (gameWorld.currentSingerNumber == 2) {
            wallPitchLevel = (float) (221.6 * Math.log(activeWall.wallPitch) - 1080);
            voicePitchLevel = (float) (221.6 * Math.log(gameWorld.currentPitch) - 1080);
        } else if (gameWorld.currentSingerNumber == 3) {
            wallPitchLevel = (float) (221.6 * Math.log(activeWall.wallPitch) - 1144);
            voicePitchLevel = (float) (221.6 * Math.log(gameWorld.currentPitch) - 1144);
        } else if (gameWorld.currentSingerNumber == 4) {
            wallPitchLevel = (float) (221.6 * Math.log(activeWall.wallPitch) - 1233);
            voicePitchLevel = (float) (221.6 * Math.log(gameWorld.currentPitch) - 1233);
        } else {
            wallPitchLevel = (float) (221.6 * Math.log(activeWall.wallPitch) - 1080);
            voicePitchLevel = (float) (221.6 * Math.log(gameWorld.currentPitch) - 1080);
        }

        if (!gameWorld.missionMode && gameWorld.currentSingerNumber == 5) {
            thresholdHeight = 51.2f;
        } else {
            thresholdHeight = 25.6f;
        }

        if (activeWall.wallActive) {
            if (gameWorld.currentlyCrushing) {
                shapeRenderer.setColor(0, 0.5f, 0, 1);
            } else {
                shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
            }
            shapeRenderer.rect(8, midPointY - 170 + wallPitchLevel - thresholdHeight / 2, 12, thresholdHeight);
        }


        if (voicePitchLevel < 0) voicePitchLevel = 0;
        else if (voicePitchLevel > 320) voicePitchLevel = 320;

        voiceLevel = midPointY - 170 + voicePitchLevel;
        gameWorld.voiceEffectLevel = voiceLevel;

        shapeRenderer.setColor(1, 0.8f, 0, 1);
        shapeRenderer.rect(8, voiceLevel - 1, 12, 2);

    }

    private void drawVoiceLevel() {

        shapeRenderer.setColor(50 / 255.0f, 50 / 255.0f, 50 / 255.0f, 1);
        shapeRenderer.rect(250, midPointY - 172, 16, 324);

        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(252, midPointY - 170, 12, 320);

        float voiceLevel;

        voiceLevel = (float) (5.333 * gameWorld.currentVolume + 480);

        if (voiceLevel < 0) voiceLevel = 0;
        else if (voiceLevel > 320) voiceLevel = 320;

        if (activeWall.wallActive) {
            if (gameWorld.currentlyCrushing) {
                shapeRenderer.setColor(0, 0.5f, 0, 1);
            } else {
                shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
            }
            if (gameWorld.currentMissionNumber == AssetManager.MISSION_SILENCE_IS_GOLDEN) {
                shapeRenderer.rect(252, midPointY - 170, 12, 53.36f);
            } else if (gameWorld.currentMissionNumber == AssetManager.MISSION_HUMMING_GENTLY) {
                shapeRenderer.rect(252, midPointY - 170, 12, 160);
            } else if (gameWorld.currentMissionNumber == AssetManager.MISSION_HEAR_ME_ROAR) {
                shapeRenderer.rect(252, midPointY - 170 + 213.35f, 12, 106.66f);
            } else if (gameWorld.currentMissionNumber == AssetManager.MISSION_HEAR_ME_ROARER) {
                shapeRenderer.rect(252, midPointY - 170 + 213.35f, 12, 106.66f);
            } else if (gameWorld.currentMissionNumber == AssetManager.MISSION_TWIST_AND_SHOUT) {
                shapeRenderer.rect(252, midPointY - 170 + 213.35f, 12, 106.66f);
            }
        }

        this.voiceLevel = midPointY - 170 + voiceLevel;

        shapeRenderer.setColor(1, 0.8f, 0, 1);
        shapeRenderer.rect(252, this.voiceLevel - 1, 12, 2);

    }

    // TEXT

    private void drawScore() {
        glyphLayout.setText(AssetManager.numberFont, "" + gameWorld.getScore());
        AssetManager.numberFont.draw(spriteBatch, "" + gameWorld.getScore(), 10, gameHeight - glyphLayout.height / 2);
    }

    private void drawNoteString() {
        glyphLayout.setText(AssetManager.numberFont, "" + gameWorld.getScore());
        AssetManager.numberFont.draw(spriteBatch, "" + gameWorld.noteString, 190, gameHeight - glyphLayout.height / 2);
    }

    private void drawHighScoreMonit(float runTime) {
        spriteBatch.draw(AssetManager.highScoreAnimation.getKeyFrame(runTime), 36, midPointY - 120);
    }

    private void drawGetReadyMonit() {
        textToDisplay = "TOUCH TO START!";
        glyphLayout.setText(AssetManager.smallWhiteFont, textToDisplay);
        AssetManager.smallWhiteFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, midPointY + 10);
    }

    // TWEENING
    public void prepareTransition() {
        alpha.setValue(1);
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        Tween.to(alpha, -1, 0.8f).target(0).ease(TweenEquations.easeOutQuad).start(manager);
        gettingLighter = true;
    }

    private void drawSuccessTransition(float delta) {
        gameWorld.blackoutTweenManager.update(delta);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Color c = spriteBatch.getColor();
        spriteBatch.setColor(c.r, c.g, c.b, gameWorld.blackAlphaValue.getValue());
        spriteBatch.draw(AssetManager.fakeBlackTextureRegion, 0, 0, 272, gameHeight);
        spriteBatch.setColor(c.r, c.g, c.b, 1);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawTransition(float delta) {

        if (gettingDarker) {
            if (alpha.getValue() < 1) {
                manager.update(delta);
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                shapeRenderer.begin(ShapeType.Filled);
                shapeRenderer.setColor(0, 0, 0, alpha.getValue());
                shapeRenderer.rect(0, 0, 272, gameHeight);
                shapeRenderer.end();
                Gdx.gl.glDisable(GL20.GL_BLEND);
            } else {
                gettingDarker = false;
                prepareTransition();
            }
        } else if (gettingLighter) {
            if (alpha.getValue() > 0) {
                manager.update(delta);
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                shapeRenderer.begin(ShapeType.Filled);
                shapeRenderer.setColor(0, 0, 0, alpha.getValue());
                shapeRenderer.rect(0, 0, 272, gameHeight);
                shapeRenderer.end();
                Gdx.gl.glDisable(GL20.GL_BLEND);

            } else {
                gettingLighter = false;
            }
        }
    }

    private void drawHelpUI() {
        spriteBatch.draw(AssetManager.helpPagesTextureRegions[gameWorld.currentHelpPage - 1], 31, 80);
        optionsButtons.get(0).draw(spriteBatch);
        missionsButtons.get(65).draw(spriteBatch);
        missionsButtons.get(66).draw(spriteBatch);
    }

    private void shakeCameraIfNecessary() {
        updateShakeCameraCounter();

        if (gameWorld.currentlyCrushing && !gameWorld.gamePaused && gameWorld.stateIsRunning()) {
            if (shakeCameraCounter == 0) {
                shakeCamera(shakeConstant);
            }
        } else {
            if (cameraChanged == true) {
                cameraReturn(0);
            }
        }

        if (gameWorld.stateIsRunning() && gameWorld.missionMode && !gameWorld.gamePaused) {
            if (gameWorld.currentMissionNumber == AssetManager.MISSION_LIGHT_TURBULENCE)
                shakeCamera(2 * shakeConstant);
            else if (gameWorld.currentMissionNumber == AssetManager.MISSION_FASTEN_YOUR_SEATBELTS)
                shakeCamera(5 * shakeConstant);
            else if (gameWorld.currentMissionNumber == AssetManager.MISSION_METEOR_SHOWER)
                shakeCamera(10 * shakeConstant);
        }
    }

    private void updateShakeCameraCounter() {
        if (shakeCameraCounter < 2) {
            shakeCameraCounter++;
        } else {
            shakeCameraCounter = 0;
        }
    }

    private void rotateCameraIfNecessary() {
        sinusCounter++;

        if (gameWorld.missionMode) {
            if (gameWorld.currentMissionNumber == AssetManager.MISSION_SOMETHING_WRONG) {
                if (gameWorld.stateIsRunning() && !gameWorld.gamePaused) {
                    if (currentRotation == 0) {
                        rotateCamera(180);
                    }
                } else {
                    if (currentRotation == 180) {
                        rotateCamera(-180);
                    }
                }
            } else if (gameWorld.currentMissionNumber == AssetManager.MISSION_YOU_DONT_KNOW_WHEN) {

                Random random = new Random();
                int randNum = random.nextInt(50) + 1;

                if (gameWorld.stateIsRunning() && !gameWorld.gamePaused) {
                    if (randNum == 1) {
                        if (currentRotation == 0) {
                            rotateCamera(180);
                        } else if (currentRotation == 180) {
                            rotateCamera(-180);
                        }
                    }
                } else {
                    if (currentRotation == 180) {
                        rotateCamera(-180);
                    }
                }

            } else if (gameWorld.currentMissionNumber == AssetManager.MISSION_CANT_GET_IT_RIGHT) {
                if (gameWorld.stateIsRunning() && !gameWorld.gamePaused) {
                    if (gameWorld.accelerometerValues[1] > 0 && currentRotation == 0) {
                        rotateCamera(180);
                    }

                    if (gameWorld.accelerometerValues[1] < 0 && currentRotation == 180) {
                        rotateCamera(-180);
                    }
                } else {
                    if (currentRotation != 0) {
                        rotateCamera(-currentRotation);
                    }
                }
            } else if (gameWorld.currentMissionNumber == AssetManager.MISSION_GRANDFATHERS_CLOCK) {
                if (gameWorld.stateIsRunning() && !gameWorld.gamePaused) {
                    if (!firstClockRotation) {
                        rotateCamera(-45);
                        sinusCounter = 0;
                        firstClockRotation = true;
                    }
                    rotateCamera((float) Math.sin(0.02 * sinusCounter));
                } else {
                    firstClockRotation = false;
                    if (currentRotation != 0) {
                        rotateCamera(-currentRotation);
                    }
                }
            } else if (gameWorld.currentMissionNumber == AssetManager.MISSION_DIZZY_MISS_LIZZY) {
                if (gameWorld.stateIsRunning() && !gameWorld.gamePaused) {
                    rotateCamera(-2f);
                } else {
                    if (currentRotation != 0) {
                        rotateCamera(-currentRotation);
                    }
                }
            }
        }
    }

    private void displayMissionTexts() {
        if (gameWorld.stateIsSuccess()) {
            AssetManager.scoreFont.draw(spriteBatch, textToDisplay, 73 - glyphLayout.width / 2, midPointY - 33);
            textToDisplay = "" + AssetManager.missionHighScores[gameWorld.currentMissionNumber];
            glyphLayout.setText(AssetManager.scoreFont, textToDisplay);
            AssetManager.scoreFont.draw(spriteBatch, textToDisplay, 199 - glyphLayout.width / 2, midPointY - 33);
        } else {
            AssetManager.scoreFont.draw(spriteBatch, textToDisplay, 136 - glyphLayout.width / 2, midPointY - 33);
        }
    }


    private void cameraReturn(float rotation) {

        if (rotation != 0) {
            camera.rotate(-rotation);
            currentRotation = 0;
        }

        camera.position.x = 136f;
        camera.position.y = cameraReturnY;
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        cameraChanged = false;

    }

    private void shakeCamera(int constant) {
        Random random = new Random();
        randomAngle = random.nextInt(360);
        camera.position.x = (float) (Math.sin((double) randomAngle) * constant + 136);
        camera.position.y = (float) (Math.cos((double) randomAngle) * constant + cameraReturnY);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        cameraChanged = true;
    }

    private void rotateCamera(float angle) {
        currentRotation += angle;
        camera.rotate(angle);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        cameraChanged = true;
    }

    private void clearAll() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void drawAliensIfNecessary(float runTime) {
        if (!gameWorld.stateIsMenu() && !gameWorld.stateIsOptions() && !gameWorld.stateIsChallenge() && !gameWorld.stateIsStats() && !gameWorld.stateIsHelp()) {
            if (AssetManager.aliensEnabled)
                drawAliens(runTime);
        }
    }

    private void startTransitionIfNecessary() {
        if (gameWorld.startTransition) {
            prepareTransition();
            gameWorld.startTransition = false;
        }

        if (!gameWorld.stateIsReadyToPlay()) {
            successTransition = false;
        }
    }

    private void drawBlackRectangles() {
        if (gameWorld.stateIsHighScore() || gameWorld.stateIsRunning() || gameWorld.stateIsReadyToPlay() || gameWorld.stateIsSuccess() || gameWorld.stateIsGameOver()) {

            shapeRenderer.setColor(0, 0, 0, 1);
            shapeRenderer.rect(-60, gameHeight / 8 + gameHeight * 2 / 3 + 30, 392, gameHeight / 3);

            shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
            shapeRenderer.rect(-105, -35, 502, gameHeight / 6 + 40);
        }
    }

    private void drawWallsIfNecessary() {
        if (gameWorld.stateIsRunning() || gameWorld.stateIsHighScore() || gameWorld.stateIsGameOver() || gameWorld.stateIsReadyToPlay()) {
            activeWall = scroller.getActiveWall();
            drawVisibleWalls();
        } else if (gameWorld.stateIsSuccess() && gameWorld.currentMissionNumber == AssetManager.MISSION_THE_LUCKY_NUMBER) {
            drawVisibleWalls();
        }
    }

}
