package com.ratowski.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.ratowski.voicegame.GameWorld;

import java.util.List;
import java.util.ArrayList;

public class InputHandler implements InputProcessor {

    private GameWorld gameWorld;

    // Buttons
    private SimpleButton optionsBackButton, facebookButton, rateOnGooglePlayButton;
    private SimpleButton freePlayButton, missionsButton, optionsButton, statsButton, helpButton, exitButton;
    private SimpleButton playButtonChallenge, backPreviewChallenge, nextChallengesButton, previousChallengesButton;
    private SimpleButton gameOverBackButton, tryAgainButton, nextChallengeButton;
    private SimpleButton exitMenuButton, unpauseButton;
    private BistableButton singer1, singer2, singer3, singer4, singer5;
    private BistableButton[] singerButtons = new BistableButton[5];

    private BistableButton[][] settingsButtons = new BistableButton[3][2];

    // Button lists
    private List<SimpleButton> menuScreenButtons;
    private List<SimpleButton> gameOverScreenButtons;
    private List<SimpleButton> challengeScreenButtons;
    private List<SimpleButton> panelScreenButtons;
    private List<SimpleButton> optionsScreenButtons;
    private List<SimpleButton> pauseScreenButtons;
    private List<BistableButton> bistableButtons;

    // Variables
    public boolean missionPreviewVisible = false;
    private float screenScaleFactorX;
    private float screenScaleFactorY;
    private float gameHeight;
    public int debug = 0;
    public int adTimer = 0;
    public boolean adsEnabled = false;
    int assetLoaderMissionsCompleted = 0;
    private String cheatCodeString = "";

    public InputHandler(GameWorld gameWorld, float screenScaleFactorX, float screenScaleFactorY, float gameHeight) {
        this.gameWorld = gameWorld;
        this.gameHeight = gameHeight;
        this.screenScaleFactorX = screenScaleFactorX;
        this.screenScaleFactorY = screenScaleFactorY;
        setupAllButtons();
    }

    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Input.Keys.BACK) {

            if ((gameWorld.stateIsRunning() || gameWorld.stateIsReadyToPlay()) && !gameWorld.gamePaused) {
                gameWorld.getScrollHandler().stopScrolling();
                gameWorld.gamePaused = true;
            } else if (gameWorld.stateIsMenu() && !gameWorld.gamePaused) {
                gameWorld.gamePaused = true;
            } else if (gameWorld.stateIsOptions()) {
                gameWorld.startTransition = true;
                gameWorld.menu();
                checkCheatCode();
                for (SimpleButton button : menuScreenButtons)
                    button.isPressed = false;
            } else if (gameWorld.stateIsStats()) {
                gameWorld.startTransition = true;
                gameWorld.menu();
                for (SimpleButton button : menuScreenButtons)
                    button.isPressed = false;
            } else if (gameWorld.stateIsHelp()) {
                gameWorld.startTransition = true;
                gameWorld.menu();
                for (SimpleButton button : menuScreenButtons)
                    button.isPressed = false;
            } else if (gameWorld.stateIsChallenge()) {
                if (missionPreviewVisible) {
                    missionPreviewVisible = false;
                    for (SimpleButton button : challengeScreenButtons)
                        button.isPressed = false;
                } else {
                    gameWorld.startTransition = true;
                    gameWorld.menu();
                    for (SimpleButton button : menuScreenButtons)
                        button.isPressed = false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        screenX = scaleX(screenX);
        screenY = scaleY(screenY);
        screenY = (int) gameHeight - screenY;

        if (gameWorld.stateIsMenu()) {
            if (gameWorld.gamePaused) {
                exitMenuButton.isTouchDown(screenX, screenY);
                unpauseButton.isTouchDown(screenX, screenY);
            } else {
                freePlayButton.isTouchDown(screenX, screenY);
                missionsButton.isTouchDown(screenX, screenY);
                optionsButton.isTouchDown(screenX, screenY);
                helpButton.isTouchDown(screenX, screenY);
                statsButton.isTouchDown(screenX, screenY);
                exitButton.isTouchDown(screenX, screenY);
            }
        } else if (gameWorld.stateIsOptions()) {

            optionsBackButton.isTouchDown(screenX, screenY);
            singer1.isTouchDown(screenX, screenY);
            singer2.isTouchDown(screenX, screenY);
            singer3.isTouchDown(screenX, screenY);
            singer4.isTouchDown(screenX, screenY);

            if (AssetManager.secretPlayerUnlocked) singer5.isTouchDown(screenX, screenY);

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 2; j++) {
                    settingsButtons[i][j].isTouchDown(screenX, screenY);
                }
            }

            facebookButton.isTouchDown(screenX, screenY);
            rateOnGooglePlayButton.isTouchDown(screenX, screenY);
        }

        if (gameWorld.stateIsStats()) {
            optionsBackButton.isTouchDown(screenX, screenY);
        }


        if (gameWorld.stateIsHelp()) {
            optionsBackButton.isTouchDown(screenX, screenY);
            nextChallengesButton.isTouchDown(screenX, screenY);
            previousChallengesButton.isTouchDown(screenX, screenY);
        } else if (gameWorld.stateIsReadyToPlay() && !gameWorld.gamePaused) {
            gameWorld.start();
            AssetManager.addGamesPlayed();
            AssetManager.addSingerTimesPlayed(gameWorld.currentSingerNumber);
        } else if ((gameWorld.stateIsRunning() || gameWorld.stateIsReadyToPlay()) && gameWorld.gamePaused) {
            exitMenuButton.isTouchDown(screenX, screenY);
            unpauseButton.isTouchDown(screenX, screenY);
        } else if (gameWorld.stateIsRunning() && !gameWorld.gamePaused && gameWorld.missionMode && gameWorld.currentMissionNumber == 5) {

            if (gameWorld.activeWall.getWallRectangle().contains(screenX, screenY)) {
                gameWorld.activeWall.addHealth(-5);
                gameWorld.addScore(10);
                gameWorld.addCrushingParticleEffect();
                gameWorld.currentlyCrushing = true;
                if (gameWorld.glassSoundCounter > 5) {
                    gameWorld.glassSoundCounter = 0;
                    if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                }
            }
        } else if (gameWorld.stateIsChallenge()) {

            if (!missionPreviewVisible) {
                if (gameWorld.currentMissionPage == 1) {
                    for (int i = 0; i < 16; i++) {
                        if (AssetManager.missionsCompleted >= i)
                            challengeScreenButtons.get(i).isTouchDown(screenX, screenY);
                    }
                } else if (gameWorld.currentMissionPage == 2) {
                    for (int i = 16; i < 32; i++) {
                        if (AssetManager.missionsCompleted >= i)
                            challengeScreenButtons.get(i).isTouchDown(screenX, screenY);
                    }
                } else if (gameWorld.currentMissionPage == 3) {
                    for (int i = 32; i < 48; i++) {
                        if (AssetManager.missionsCompleted >= i)
                            challengeScreenButtons.get(i).isTouchDown(screenX, screenY);
                    }
                } else if (gameWorld.currentMissionPage == 4) {
                    for (int i = 48; i < 64; i++) {
                        if (AssetManager.missionsCompleted >= i)
                            challengeScreenButtons.get(i).isTouchDown(screenX, screenY);
                    }
                }
            }

            if (missionPreviewVisible) {
                panelScreenButtons.get(0).isTouchDown(screenX, screenY);
                panelScreenButtons.get(1).isTouchDown(screenX, screenY);
            } else {
                challengeScreenButtons.get(64).isTouchDown(screenX, screenY);
                challengeScreenButtons.get(65).isTouchDown(screenX, screenY);
                challengeScreenButtons.get(66).isTouchDown(screenX, screenY);
            }
        } else if (gameWorld.stateIsGameOver() || gameWorld.stateIsHighScore()) {
            gameOverBackButton.isTouchDown(screenX, screenY);
            tryAgainButton.isTouchDown(screenX, screenY);

            if (gameWorld.missionMode && AssetManager.missionsCompleted >= gameWorld.currentMissionNumber)
                nextChallengeButton.isTouchDown(screenX, screenY);
        } else if (gameWorld.stateIsSuccess()) {

            gameOverBackButton.isTouchDown(screenX, screenY);
            tryAgainButton.isTouchDown(screenX, screenY);
            if (gameWorld.currentMissionNumber != 64) {
                nextChallengeButton.isTouchDown(screenX, screenY);
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        gameWorld.currentlyCrushing = false;

        screenX = scaleX(screenX);
        screenY = scaleY(screenY);
        screenY = (int) gameHeight - screenY;

        if (gameWorld.stateIsMenu()) {

            // Pause in main menu
            if (gameWorld.gamePaused) {
                // Return to main menu
                if (unpauseButton.isTouchUp(screenX, screenY)) {
                    gameWorld.gamePaused = false;
                    if (gameWorld.getScrollHandler().isStopped) {
                        gameWorld.getScrollHandler().startScrolling();
                    }
                    for (SimpleButton simpleButton : menuScreenButtons) {
                        simpleButton.isPressed = false;
                    }
                    return true;
                }
                // Exit game
                else if (exitMenuButton.isTouchUp(screenX, screenY)) {
                    Gdx.app.exit();
                    return true;
                }
            }

            //
            if (freePlayButton.isTouchUp(screenX, screenY)) {
                gameWorld.missionMode = false;
                gameWorld.startTransition = true;
                gameWorld.readyToPlay();
                return true;
            } else if (optionsButton.isTouchUp(screenX, screenY)) {
                gameWorld.goToOptions();
                gameWorld.startTransition = true;
                cheatCodeString = "";
                for (SimpleButton simpleButton : optionsScreenButtons) {
                    simpleButton.isPressed = false;
                }

                return true;
            } else if (missionsButton.isTouchUp(screenX, screenY)) {
                missionPreviewVisible = false;
                for (SimpleButton b : challengeScreenButtons) b.isPressed = false;
                gameWorld.goToMissions();
                gameWorld.startTransition = true;
                return true;
            } else if (helpButton.isTouchUp(screenX, screenY)) {
                gameWorld.currentHelpPage = 1;
                gameWorld.startTransition = true;
                gameWorld.goToHelp();
                for (SimpleButton b : optionsScreenButtons)
                    b.isPressed = false;
                return true;
            } else if (statsButton.isTouchUp(screenX, screenY)) {
                gameWorld.goToStats();
                gameWorld.startTransition = true;
                AssetManager.updateStats();
                return true;
            } else if (exitButton.isTouchUp(screenX, screenY)) {
                gameWorld.gamePaused = true;
                return true;
            }
        } else if (gameWorld.stateIsGameOver() || gameWorld.stateIsHighScore()) {
            if (tryAgainButton.isTouchUp(screenX, screenY)) {

                if (adsEnabled) {
                    adTimer++;
                    if (adTimer % 4 == 0) {
                        if (AssetManager.adInterface.isWifiConnected()) {
                            AssetManager.adInterface.showInterstitialAd(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    }
                }

                gameWorld.readyToPlay();
                gameWorld.startTransition = true;
                return true;
            } else if (gameOverBackButton.isTouchUp(screenX, screenY)) {
                gameWorld.menu();
                gameWorld.startTransition = true;
                for (SimpleButton b : menuScreenButtons)
                    b.isPressed = false;
                return true;
            } else if (nextChallengeButton.isTouchUp(screenX, screenY)) {
                gameWorld.startTransition = true;
                missionPreviewVisible = true;
                gameWorld.currentMissionNumber++;
                gameWorld.goToMissions();
                return true;
            }
        } else if ((gameWorld.stateIsRunning() || gameWorld.stateIsReadyToPlay()) && gameWorld.gamePaused) {

            if (unpauseButton.isTouchUp(screenX, screenY)) {
                gameWorld.getScrollHandler().startScrolling();
                gameWorld.gamePaused = false;
                return true;
            } else if (exitMenuButton.isTouchUp(screenX, screenY)) {

                if (!gameWorld.missionMode) {
                    AssetManager.addOverallWallsCount(gameWorld.score);
                    AssetManager.addFreePlayWalls(gameWorld.score);
                    if (gameWorld.score > AssetManager.freePlayHighScore) {
                        AssetManager.setHighScore(gameWorld.score);
                    }
                } else {
                    AssetManager.addOverallWallsCount(gameWorld.crushedWallsCounter);
                }

                gameWorld.getScrollHandler().startScrolling();
                gameWorld.startTransition = true;

                missionPreviewVisible = false;
                if (gameWorld.missionMode) gameWorld.goToMissions();
                else gameWorld.menu();

                gameWorld.gamePaused = false;
                return true;
            }
        } else if (gameWorld.stateIsOptions()) {

            if (singer1.isTouchUp(screenX, screenY)) {
                AssetManager.setSelectedSinger(1);
                gameWorld.setCurrentSingerNumber(1);
                cheatCodeString += "1";
                return true;
            } else if (singer2.isTouchUp(screenX, screenY)) {
                AssetManager.setSelectedSinger(2);
                gameWorld.setCurrentSingerNumber(2);
                cheatCodeString += "2";
                return true;
            } else if (singer3.isTouchUp(screenX, screenY)) {
                AssetManager.setSelectedSinger(3);
                gameWorld.setCurrentSingerNumber(3);
                cheatCodeString += "3";
                return true;
            } else if (singer4.isTouchUp(screenX, screenY)) {
                AssetManager.setSelectedSinger(4);
                gameWorld.setCurrentSingerNumber(4);
                cheatCodeString += "4";
                return true;
            } else if (singer5.isTouchUp(screenX, screenY)) {
                AssetManager.setSelectedSinger(5);
                gameWorld.setCurrentSingerNumber(5);
                return true;
            } else if (settingsButtons[0][0].isTouchUp(screenX, screenY)) {
                settingsButtons[0][0].isActive = true;
                settingsButtons[0][1].isActive = false;
                AssetManager.setSoundEnabled(true);
                return true;
            } else if (settingsButtons[0][1].isTouchUp(screenX, screenY)) {
                settingsButtons[0][0].isActive = false;
                settingsButtons[0][1].isActive = true;
                AssetManager.setSoundEnabled(false);
                return true;
            } else if (settingsButtons[1][0].isTouchUp(screenX, screenY)) {
                settingsButtons[1][0].isActive = true;
                settingsButtons[1][1].isActive = false;
                if (!AssetManager.vibrationEnabled) Gdx.input.vibrate(200);
                AssetManager.setVibrationEnabled(true);
                return true;
            } else if (settingsButtons[1][1].isTouchUp(screenX, screenY)) {
                settingsButtons[1][0].isActive = false;
                settingsButtons[1][1].isActive = true;
                AssetManager.setVibrationEnabled(false);
                return true;
            } else if (settingsButtons[2][0].isTouchUp(screenX, screenY)) {
                settingsButtons[2][0].isActive = true;
                settingsButtons[2][1].isActive = false;
                AssetManager.setAliensEnabled(true);
                return true;
            } else if (settingsButtons[2][1].isTouchUp(screenX, screenY)) {
                settingsButtons[2][0].isActive = false;
                settingsButtons[2][1].isActive = true;
                AssetManager.setAliensEnabled(false);
                return true;
            } else if (facebookButton.isTouchUp(screenX, screenY)) {
                AssetManager.internetInterface.goToFacebook();
                return true;
            } else if (rateOnGooglePlayButton.isTouchUp(screenX, screenY)) {
                AssetManager.internetInterface.rateOnGooglePlay();
                return true;
            } else if (optionsBackButton.isTouchUp(screenX, screenY)) {
                gameWorld.menu();
                gameWorld.startTransition = true;
                checkCheatCode();
                return true;
            }
        } else if (gameWorld.stateIsStats()) {
            if (optionsBackButton.isTouchUp(screenX, screenY)) {
                gameWorld.startTransition = true;
                gameWorld.menu();
                return true;
            }
        } else if (gameWorld.stateIsHelp()) {
            if (optionsBackButton.isTouchUp(screenX, screenY)) {
                gameWorld.startTransition = true;
                gameWorld.menu();
                return true;
            } else if (nextChallengesButton.isTouchUp(screenX, screenY)) {
                if (gameWorld.currentHelpPage < 4) gameWorld.currentHelpPage++;
                else gameWorld.currentHelpPage = 1;
            } else if (previousChallengesButton.isTouchUp(screenX, screenY)) {
                if (gameWorld.currentHelpPage > 1) gameWorld.currentHelpPage--;
                else gameWorld.currentHelpPage = 4;
            }
        } else if (gameWorld.stateIsChallenge()) {
            if (!missionPreviewVisible) {
                for (int i = 0; i < 64; i++) {
                    if (challengeScreenButtons.get(i).isTouchUp(screenX, screenY)) {
                        missionPreviewVisible = true;
                        gameWorld.currentMissionNumber = i + 1;
                        return true;
                    }
                }
                if (challengeScreenButtons.get(64).isTouchUp(screenX, screenY)) {
                    gameWorld.startTransition = true;
                    gameWorld.menu();
                    for (SimpleButton b : menuScreenButtons)
                        b.isPressed = false;
                    return true;
                } else if (challengeScreenButtons.get(65).isTouchUp(screenX, screenY)) {
                    changeChallengePage(1);
                    return true;
                } else if (challengeScreenButtons.get(66).isTouchUp(screenX, screenY)) {
                    changeChallengePage(-1);
                    return true;
                }
            } else {
                if (panelScreenButtons.get(0).isTouchUp(screenX, screenY)) {
                    missionPreviewVisible = false;
                    for (SimpleButton b : challengeScreenButtons)
                        b.isPressed = false;
                    return true;
                } else if (panelScreenButtons.get(1).isTouchUp(screenX, screenY)) {
                    gameWorld.startTransition = true;
                    gameWorld.missionMode = true;
                    gameWorld.readyToPlay();
                    return true;
                }
            }
        } else if (gameWorld.stateIsSuccess()) {
            if (tryAgainButton.isTouchUp(screenX, screenY)) {

                if (adsEnabled) {
                    adTimer++;
                    if (adTimer % 4 == 0) {
                        if (AssetManager.adInterface.isWifiConnected()) {
                            AssetManager.adInterface.showInterstitialAd(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    }
                }

                gameWorld.startTransition = true;
                gameWorld.readyToPlay();

                return true;
            } else if (gameOverBackButton.isTouchUp(screenX, screenY)) {

                if (adsEnabled) {
                    adTimer++;
                    if (adTimer % 4 == 0) {
                        if (AssetManager.adInterface.isWifiConnected()) {
                            AssetManager.adInterface.showInterstitialAd(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }
                    }
                }

                gameWorld.goToMissions();
                gameWorld.startTransition = true;

                if (AssetManager.missionsCompleted < gameWorld.currentMissionNumber) {
                    AssetManager.setMissionCompleted(gameWorld.currentMissionNumber, gameWorld.getScore());
                    gameWorld.createMissionCompletedCloud();
                } else {
                    if (gameWorld.getScore() > AssetManager.getMissionScore(gameWorld.currentMissionNumber)) {
                        AssetManager.setMissionCompleted(gameWorld.currentMissionNumber, gameWorld.getScore());
                    }
                }
                return true;

            } else if (nextChallengeButton.isTouchUp(screenX, screenY)) {

                AssetManager.addOverallWallsCount(gameWorld.crushedWallsCounter);

                if (AssetManager.missionsCompleted < gameWorld.currentMissionNumber) {
                    AssetManager.setMissionCompleted(gameWorld.currentMissionNumber, gameWorld.getScore());
                } else {
                    if (gameWorld.getScore() > AssetManager.getMissionScore(gameWorld.currentMissionNumber)) {
                        AssetManager.setMissionCompleted(gameWorld.currentMissionNumber, gameWorld.getScore());
                    }
                }
                missionPreviewVisible = true;
                gameWorld.currentMissionNumber++;
                gameWorld.startTransition = true;
                gameWorld.goToMissions();

                return true;
            }
        }


        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        screenX = scaleX(screenX);
        screenY = scaleY(screenY);
        screenY = (int) gameHeight - screenY;

        if (debug == 1 && screenY > (gameHeight * 5 / 6)) {
            if (gameWorld.stateIsRunning() && gameWorld.activeWall.isActive && !gameWorld.gamePaused) {
                gameWorld.currentlyCrushing = true;
                gameWorld.activeWall.addHealth(-3);
                if (gameWorld.missionMode) gameWorld.addScore(5);
            }
        }

        if (screenY > (gameHeight * 5 / 6)) {
            if (gameWorld.stateIsRunning() && gameWorld.activeWall.isActive && !gameWorld.gamePaused && gameWorld.missionMode && gameWorld.currentMissionNumber == AssetManager.MISSION_TRY_YET_ANOTHER_WAY) {
                gameWorld.currentlyCrushing = true;
                gameWorld.activeWall.addHealth(-3);
                gameWorld.addScore(10);
                if (gameWorld.glassSoundCounter > 5) {
                    gameWorld.glassSoundCounter = 0;
                    if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                }
            }
        }

        if (gameWorld.stateIsRunning() && !gameWorld.gamePaused && gameWorld.missionMode && gameWorld.currentMissionNumber == AssetManager.MISSION_THE_VOICE_IS_NOT_ENOUGH) {
            if (gameWorld.activeWall.getWallRectangle().contains(screenX, screenY)) {
                if (gameWorld.currentPitch > gameWorld.minCorrectPitch && gameWorld.currentPitch < gameWorld.maxCorrectPitch && gameWorld.activeWall.isActive) {
                    gameWorld.activeWall.addHealth(-2);
                    gameWorld.currentlyCrushing = true;
                    if (gameWorld.glassSoundCounter > 5) {
                        gameWorld.glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else gameWorld.currentlyCrushing = false;
            }
        }

        return false;
    }

    private void checkCheatCode() {
        if (cheatCodeString.matches("123123123")) {
            AssetManager.unlockSecretPlayer();
        } else if (cheatCodeString.matches("321321")) {
            if (debug == 1) {
                debug = 0;
                AssetManager.missionsCompleted = assetLoaderMissionsCompleted;
            } else {
                debug = 1;
                assetLoaderMissionsCompleted = AssetManager.missionsCompleted;
                AssetManager.missionsCompleted = 64;
            }
        }
    }

    private void changeChallengePage(int which) {
        if (which == 1) {
            if (gameWorld.currentMissionPage < 4) gameWorld.currentMissionPage++;
            else gameWorld.currentMissionPage = 1;
        } else {
            if (gameWorld.currentMissionPage > 1) gameWorld.currentMissionPage--;
            else gameWorld.currentMissionPage = 4;
        }
    }

    private void setupAllButtons() {
        setupMainMenuButtons();
        setupOptionsScreenButtons();
        setupGameOverScreenButtons();
        setupChallengeScreenButtons();
        setupPauseScreenButtons();
    }

    private void setupMainMenuButtons() {
        freePlayButton = new com.ratowski.helpers.SimpleButton(11, 100, 120, 140, AssetManager.menuButtonsTextureRegions[0][0], AssetManager.menuButtonsTextureRegions[0][1]);
        missionsButton = new com.ratowski.helpers.SimpleButton(141, 100, 120, 140, AssetManager.menuButtonsTextureRegions[1][0], AssetManager.menuButtonsTextureRegions[1][1]);
        optionsButton = new com.ratowski.helpers.SimpleButton(9, 15, 50, 50, AssetManager.otherButtonsTextureRegions[6][0], AssetManager.otherButtonsTextureRegions[6][1]);
        helpButton = new com.ratowski.helpers.SimpleButton(77, 15, 50, 50, AssetManager.otherButtonsTextureRegions[11][0], AssetManager.otherButtonsTextureRegions[11][1]);
        statsButton = new com.ratowski.helpers.SimpleButton(145, 15, 50, 50, AssetManager.otherButtonsTextureRegions[10][0], AssetManager.otherButtonsTextureRegions[10][1]);
        exitButton = new com.ratowski.helpers.SimpleButton(213, 15, 50, 50, AssetManager.otherButtonsTextureRegions[8][0], AssetManager.otherButtonsTextureRegions[8][1]);

        menuScreenButtons = new ArrayList<SimpleButton>();
        menuScreenButtons.add(freePlayButton);
        menuScreenButtons.add(optionsButton);
        menuScreenButtons.add(missionsButton);
        menuScreenButtons.add(helpButton);
        menuScreenButtons.add(statsButton);
        menuScreenButtons.add(exitButton);
    }

    private void setupOptionsScreenButtons() {
        singer1 = new BistableButton(1, 290, 50, 50, AssetManager.numberButtonsTextureRegions[0][0], AssetManager.numberButtonsTextureRegions[0][1], AssetManager.numberButtonsTextureRegions[0][1]);
        singer2 = new BistableButton(56, 290, 50, 50, AssetManager.numberButtonsTextureRegions[1][0], AssetManager.numberButtonsTextureRegions[1][1], AssetManager.numberButtonsTextureRegions[1][1]);
        singer3 = new BistableButton(111, 290, 50, 50, AssetManager.numberButtonsTextureRegions[2][0], AssetManager.numberButtonsTextureRegions[2][1], AssetManager.numberButtonsTextureRegions[2][1]);
        singer4 = new BistableButton(166, 290, 50, 50, AssetManager.numberButtonsTextureRegions[3][0], AssetManager.numberButtonsTextureRegions[3][1], AssetManager.numberButtonsTextureRegions[3][1]);
        singer5 = new BistableButton(221, 290, 50, 50, AssetManager.numberButtonsTextureRegions[4][0], AssetManager.numberButtonsTextureRegions[4][1], AssetManager.numberButtonsTextureRegions[4][1]);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                settingsButtons[i][j] = new BistableButton(141 + 55 * j, 210 - 55 * i, 50, 50, AssetManager.otherButtonsTextureRegions[17 + j][0], AssetManager.otherButtonsTextureRegions[17 + j][1], AssetManager.otherButtonsTextureRegions[17 + j][1]);
            }
        }

        bistableButtons = new ArrayList<BistableButton>();
        bistableButtons.add(singer1);
        bistableButtons.add(singer2);
        bistableButtons.add(singer3);
        bistableButtons.add(singer4);
        bistableButtons.add(singer5);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                bistableButtons.add(settingsButtons[i][j]);
            }
        }

        bistableButtons.get(AssetManager.getSelectedSinger() - 1).isActive = true;

        if (AssetManager.soundEnabled) {
            bistableButtons.get(5).isActive = true;
        } else {
            bistableButtons.get(6).isActive = true;
        }

        if (AssetManager.vibrationEnabled) {
            bistableButtons.get(7).isActive = true;
        } else {
            bistableButtons.get(8).isActive = true;
        }

        if (AssetManager.aliensEnabled) {
            bistableButtons.get(9).isActive = true;
        } else {
            bistableButtons.get(10).isActive = true;
        }

        optionsBackButton = new com.ratowski.helpers.SimpleButton(9, 15, 50, 50, AssetManager.otherButtonsTextureRegions[8][0], AssetManager.otherButtonsTextureRegions[8][1]);
        facebookButton = new com.ratowski.helpers.SimpleButton(213, 15, 50, 50, AssetManager.otherButtonsTextureRegions[5][0], AssetManager.otherButtonsTextureRegions[5][1]);
        rateOnGooglePlayButton = new com.ratowski.helpers.SimpleButton(77, 15, 50, 50, AssetManager.otherButtonsTextureRegions[20][0], AssetManager.otherButtonsTextureRegions[20][1]);

        optionsScreenButtons = new ArrayList<SimpleButton>();
        optionsScreenButtons.add(optionsBackButton);
        optionsScreenButtons.add(facebookButton);
        optionsScreenButtons.add(rateOnGooglePlayButton);

    }

    private void setupGameOverScreenButtons() {
        tryAgainButton = new com.ratowski.helpers.SimpleButton(77, 15, 50, 50, AssetManager.otherButtonsTextureRegions[9][0], AssetManager.otherButtonsTextureRegions[9][1]);
        gameOverBackButton = new com.ratowski.helpers.SimpleButton(9, 15, 50, 50, AssetManager.otherButtonsTextureRegions[8][0], AssetManager.otherButtonsTextureRegions[8][1]);
        nextChallengeButton = new com.ratowski.helpers.SimpleButton(213, 15, 50, 50, AssetManager.otherButtonsTextureRegions[15][0], AssetManager.otherButtonsTextureRegions[15][1]);

        gameOverScreenButtons = new ArrayList<SimpleButton>();
        gameOverScreenButtons.add(gameOverBackButton);
        gameOverScreenButtons.add(tryAgainButton);
        gameOverScreenButtons.add(nextChallengeButton);
    }

    private void setupChallengeScreenButtons() {
        challengeScreenButtons = new ArrayList<SimpleButton>();
        for (int j = 3; j >= 0; j--) {
            for (int i = 0; i < 4; i++) {
                SimpleButton b1 = new com.ratowski.helpers.SimpleButton(9 + 68 * i, 120 + j * 80, 50, 50, AssetManager.numberButtonsTextureRegions[4 * (3 - j) + i][0], AssetManager.numberButtonsTextureRegions[4 * (3 - j) + i][1]);
                challengeScreenButtons.add(b1);

            }
        }

        for (int j = 3; j >= 0; j--) {
            for (int i = 0; i < 4; i++) {
                SimpleButton b1 = new com.ratowski.helpers.SimpleButton(9 + 68 * i, 120 + j * 80, 50, 50, AssetManager.numberButtonsTextureRegions[16 + 4 * (3 - j) + i][0], AssetManager.numberButtonsTextureRegions[16 + 4 * (3 - j) + i][1]);
                challengeScreenButtons.add(b1);

            }
        }

        for (int j = 3; j >= 0; j--) {
            for (int i = 0; i < 4; i++) {
                SimpleButton b1 = new com.ratowski.helpers.SimpleButton(9 + 68 * i, 120 + j * 80, 50, 50, AssetManager.numberButtonsTextureRegions[32 + 4 * (3 - j) + i][0], AssetManager.numberButtonsTextureRegions[32 + 4 * (3 - j) + i][1]);
                challengeScreenButtons.add(b1);

            }
        }

        for (int j = 3; j >= 0; j--) {
            for (int i = 0; i < 4; i++) {
                SimpleButton b1 = new com.ratowski.helpers.SimpleButton(9 + 68 * i, 120 + j * 80, 50, 50, AssetManager.numberButtonsTextureRegions[48 + 4 * (3 - j) + i][0], AssetManager.numberButtonsTextureRegions[48 + 4 * (3 - j) + i][1]);
                challengeScreenButtons.add(b1);

            }
        }

        nextChallengesButton = new com.ratowski.helpers.SimpleButton(213, 15, 50, 50, AssetManager.otherButtonsTextureRegions[16][0], AssetManager.otherButtonsTextureRegions[16][1]);
        previousChallengesButton = new com.ratowski.helpers.SimpleButton(145, 15, 50, 50, AssetManager.otherButtonsTextureRegions[0][0], AssetManager.otherButtonsTextureRegions[0][1]);

        challengeScreenButtons.add(optionsBackButton);
        challengeScreenButtons.add(nextChallengesButton);
        challengeScreenButtons.add(previousChallengesButton);

        playButtonChallenge = new com.ratowski.helpers.SimpleButton(151, gameHeight / 2 - 90, 50, 50, AssetManager.otherButtonsTextureRegions[3][0], AssetManager.otherButtonsTextureRegions[3][1]);
        backPreviewChallenge = new com.ratowski.helpers.SimpleButton(71, gameHeight / 2 - 90, 50, 50, AssetManager.otherButtonsTextureRegions[8][0], AssetManager.otherButtonsTextureRegions[8][1]);

        panelScreenButtons = new ArrayList<SimpleButton>();
        panelScreenButtons.add(backPreviewChallenge);
        panelScreenButtons.add(playButtonChallenge);
    }

    private void setupPauseScreenButtons() {
        pauseScreenButtons = new ArrayList<SimpleButton>();

        exitMenuButton = new com.ratowski.helpers.SimpleButton(71, gameHeight / 2 - 50, 50, 50, AssetManager.otherButtonsTextureRegions[13][0], AssetManager.otherButtonsTextureRegions[13][1]);
        unpauseButton = new com.ratowski.helpers.SimpleButton(151, gameHeight / 2 - 50, 50, 50, AssetManager.otherButtonsTextureRegions[14][0], AssetManager.otherButtonsTextureRegions[14][1]);

        pauseScreenButtons.add(exitMenuButton);
        pauseScreenButtons.add(unpauseButton);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private int scaleX(int screenX) {
        return (int) (screenX / screenScaleFactorX);
    }

    private int scaleY(int screenY) {
        return (int) (screenY / screenScaleFactorY);
    }

    public List<com.ratowski.helpers.SimpleButton> getMenuScreenButtons() {
        return menuScreenButtons;
    }

    public List<com.ratowski.helpers.SimpleButton> getPauseScreenButtons() {
        return pauseScreenButtons;
    }

    public List<com.ratowski.helpers.SimpleButton> getChallengeScreenButtons() {
        return challengeScreenButtons;
    }

    public List<com.ratowski.helpers.SimpleButton> getGameOverScreenButtons() {
        return gameOverScreenButtons;
    }

    public List<com.ratowski.helpers.SimpleButton> getPanelScreenButtons() {
        return panelScreenButtons;
    }

    public List<BistableButton> getBistableButtons() {
        return bistableButtons;
    }

    public List<com.ratowski.helpers.SimpleButton> getOptionsScreenButtons() {
        return optionsScreenButtons;
    }
}
