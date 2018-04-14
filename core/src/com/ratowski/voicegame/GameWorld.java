package com.ratowski.voicegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;
import com.ratowski.accessors.Value;
import com.ratowski.accessors.ValueAccessor;
import com.ratowski.gameobjects.Alien;
import com.ratowski.gameobjects.Monit;
import com.ratowski.gameobjects.Player;
import com.ratowski.gameobjects.ScrollHandler;
import com.ratowski.gameobjects.Wall;
import com.ratowski.helpers.AssetManager;
import com.ratowski.helpers.InputHandler;
import com.ratowski.helpers.SoundHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class GameWorld {

    public enum GameState {MENU, MISSIONS, STATS, HELP, READY_TO_PLAY, RUNNING, GAMEOVER, HIGHSCORE, OPTIONS, SUCCESS}
    public GameState currentGameState;

    float[] accelerometerValues;

    public Player player;
    public ArrayList<Alien>[] alienRows;
    public ArrayList<Monit> monits = new ArrayList<Monit>();
    private ScrollHandler scrollHandler;
    public Wall activeWall;
    public int currentVolume;
    boolean previousWiFiState;
    boolean blackoutTaskStarted = false;

    public int score = 0;
    public int crushedWallsCounter = 0;
    private float runTime = 0;
    private float gameHeight;
    public float voiceEffectLevel = 0;
    public int currentPitch = 0;
    public String noteString = "-";
    public int currentSingerNumber;
    public int currentMissionNumber = 1;
    public int midPointY;
    public double minCorrectPitch, maxCorrectPitch;
    public float accelerometerDeltaZ = 0;

    public int currentHelpPage = 1;
    public int currentMissionPage = 1;
    public int glassSoundCounter = 0;

    public TweenManager blackoutTweenManager;
    public Value blackAlphaValue = new Value();

    public boolean missionMode = false;
    public boolean continuousWalls = false;
    public boolean gamePaused = false;
    public boolean currentlyCrushing = false;
    public boolean startTransition = false;
    public boolean starsAlreadyDrawnArray[] = new boolean[5];
    public boolean starsScheduledToDrawArray[] = new boolean[5];
    public boolean allStarsDrawn = false;


    public int continuousWallsMissions[] = {4, 8, 10, 12, 16, 17, 20, 24, 27, 28, 31, 32, 36, 39, 40, 42, 44, 48, 52, 53, 56, 60, 61, 64};

    public GameWorld(int midPointY, float gameHeight) {
        currentGameState = GameState.MENU;
        player = new Player(24, gameHeight / 8, 62, 104, this);
        this.gameHeight = gameHeight;
        currentSingerNumber = AssetManager.getSelectedSinger();
        scrollHandler = new ScrollHandler(this, gameHeight);
        this.midPointY = midPointY;
        activeWall = scrollHandler.getActiveWall();
        createAlienRows();
        randomizeAliens();
        previousWiFiState = AssetManager.adInterface.isWifiConnected();
    }

    public void updateWorld(float delta) {
        runTime += delta;
        updateAccelerometerValues();
        reloadAd();
        removeObsoleteParticleEffects();
        processGameSounds();
        updateCurrentGameState(delta);
        updateAliens();
    }

    private void updateGameOverState(float delta) {
        player.updateGameOver(delta);
        scrollHandler.updateNotMenuState(delta, runTime);
        updateBlackout();
    }

    private void updateMenuState(float delta) {
        scrollHandler.updateMenuState(delta, runTime);
        playThemeMusic();
    }

    public void updateRunningState(float delta) {
        delta = stabilizeFramerate(delta);
        increaseFreePlayWallSpeed();
        setLastWallForMission13();
        player.update();
        scrollHandler.updateNotMenuState(delta, runTime);
        activeWall = scrollHandler.getActiveWall();

        // currentPitch

        if (!gamePaused) {
            currentPitch = SoundHandler.getDominantFrequency();
            setCorrectPitchBoundaries();


            if (!(missionMode && (currentMissionNumber == 2 || currentMissionNumber == 5 || currentMissionNumber == 9 || currentMissionNumber == 14 || currentMissionNumber == 22 || currentMissionNumber == 23
                    || currentMissionNumber == 26 || currentMissionNumber == 33 || currentMissionNumber == 35 || currentMissionNumber == 37 || currentMissionNumber == 41
                    || currentMissionNumber == 43 || currentMissionNumber == 50 || currentMissionNumber == 51 || currentMissionNumber == 58 || currentMissionNumber == 62))) {

                if (currentPitch > minCorrectPitch && currentPitch < maxCorrectPitch && activeWall.isActive) {
                    activeWall.addHealth(-2);
                    currentlyCrushing = true;
                    playCrushingSound();
                } else {
                    currentlyCrushing = false;
                }
            }

            noteString = getClosestNoteString();

            if (missionMode && currentMissionNumber == AssetManager.MISSION_SOMEONE_HAS_TO_LOOK) {
                if (currentPitch > minCorrectPitch && currentPitch < maxCorrectPitch && activeWall.isActive && AssetManager.aliensEnabled) {
                    activeWall.addHealth(-2);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;
            }



            else if (missionMode && currentMissionNumber == AssetManager.MISSION_SILENCE_IS_GOLDEN) {
                if (currentVolume < -80 && activeWall.isActive) {
                    activeWall.addHealth(-2);
                    addScore(10);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;
            } else if (missionMode && currentMissionNumber == AssetManager.MISSION_SHAKE_IT_BABY) {
                if (accelerometerDeltaZ > 1500 && activeWall.isActive) {
                    activeWall.addHealth(-2);
                    addScore(10);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;
            } else if (missionMode && currentMissionNumber == AssetManager.MISSION_GOOD_ACOUSTICS) {
                if (currentPitch > minCorrectPitch && currentPitch < maxCorrectPitch && activeWall.isActive) {
                    activeWall.addHealth(-10);
                    addScore(50);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;
            } else if (missionMode && currentMissionNumber == AssetManager.MISSION_HEAR_ME_ROAR) {
                if (currentPitch > minCorrectPitch && currentPitch < maxCorrectPitch && activeWall.isActive && currentVolume > -50) {
                    activeWall.addHealth(-2);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;
            } else if (missionMode && currentMissionNumber == AssetManager.MISSION_HEAR_THEM_CRUSH) {
                if (currentPitch > minCorrectPitch && currentPitch < maxCorrectPitch && activeWall.isActive && AssetManager.soundEnabled) {
                    activeWall.addHealth(-2);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;
            } else if (missionMode && currentMissionNumber == AssetManager.MISSION_HUMMING_GENTLY) {
                if (currentPitch > minCorrectPitch && currentPitch < maxCorrectPitch && activeWall.isActive && currentVolume < -60) {
                    activeWall.addHealth(-2);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;
            } else if (missionMode && currentMissionNumber == AssetManager.MISSION_RENDEZVOUZ_AT_MIDNIGHT) {
                if (currentPitch > minCorrectPitch && currentPitch < maxCorrectPitch && activeWall.isActive && getCurrentHour() == 0) {
                    activeWall.addHealth(-2);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;
            } else if (missionMode && currentMissionNumber == AssetManager.MISSION_SHAKE_IT_REAL_GOOD) {
                if (currentPitch > minCorrectPitch && currentPitch < maxCorrectPitch && activeWall.isActive && accelerometerDeltaZ > 1000) {
                    activeWall.addHealth(-2);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;
            } else if (missionMode && currentMissionNumber == AssetManager.MISSION_THIRD_TIMES_A_CHARM) {
                if (currentPitch > minCorrectPitch && currentPitch < maxCorrectPitch && activeWall.isActive && AssetManager.mission43Failures >= 2) {
                    activeWall.addHealth(-2);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;
            } else if (missionMode && currentMissionNumber == AssetManager.MISSION_HEAR_ME_ROARER) {
                if (currentPitch > minCorrectPitch && currentPitch < maxCorrectPitch && activeWall.isActive && currentVolume > -50) {
                    activeWall.addHealth(-2);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;

            } else if (missionMode && currentMissionNumber == AssetManager.MISSION_TWIST_AND_SHOUT) {
                if (currentPitch > minCorrectPitch && currentPitch < maxCorrectPitch && activeWall.isActive && accelerometerDeltaZ > 1000 && currentVolume > -50) {
                    activeWall.addHealth(-2);
                    currentlyCrushing = true;
                    if (glassSoundCounter > 3) {
                        glassSoundCounter = 0;
                        if (AssetManager.soundEnabled) AssetManager.glassMusic.play();
                    }
                } else currentlyCrushing = false;
            }
        }

        // dodawanie obu efektow

        if (currentlyCrushing == true && activeWall.wallHealth > 0 && !gamePaused) {

            addCrushingParticleEffect();

            if (!((missionMode && (currentMissionNumber == 5 || currentMissionNumber == 14 || currentMissionNumber == 23)))) {
                addCorrectPitchParticleEffect();
            }

            if (missionMode && currentMissionNumber != 5 && currentMissionNumber != 14 && currentMissionNumber != 22 && currentMissionNumber != 23) {
                if (Math.abs(currentPitch - activeWall.wallPitch) < 0.0561 * activeWall.wallPitch) {
                    int howmuch = (int) (10 - ((float) (Math.abs(currentPitch - activeWall.wallPitch)) / activeWall.wallPitch) * 178.17);
                    addScore(howmuch);
                    scrollHandler.currentWallScore += howmuch;
                }
            }
        }

        // koniec gry

        if (player.isAlive() && scrollHandler.collides(player)) {
            if (missionMode && currentMissionNumber == 7 && scrollHandler.currentWallNumber == 7) {
                scrollHandler.stopScrolling();
                success();
            } else {
                gameOver();
                if (!missionMode) {
                    if (score > AssetManager.freePlayHighScore) {
                        AssetManager.setHighScore(score);
                        currentGameState = GameState.HIGHSCORE;
                    }
                }
            }
        }
    }

    public int getScore() {
        return score;
    }

    public void addScore(int increment) {
        score += increment;
    }

    public Player getPlayer() {
        return player;
    }

    public ScrollHandler getScrollHandler() {
        return scrollHandler;
    }

    public void start() {
        currentGameState = GameState.RUNNING;
    }

    public void menu() {
        currentGameState = GameState.MENU;
        if (AssetManager.soundEnabled) {
            AssetManager.themeMusic.play();
        }
    }

    public void success() {

        if (missionMode) {
            if (AssetManager.missionsCompleted < currentMissionNumber) {
                AssetManager.setMissionCompleted(currentMissionNumber, score);
            } else {
                if (score > AssetManager.getMissionScore(currentMissionNumber)) {
                    AssetManager.setMissionCompleted(currentMissionNumber, score);
                }
            }
        }

        setStarsToBeDrawn();


        prepareSuccessTransition();

        for(int i=0;i<3;i++){
            for (Alien alien : alienRows[i]) {
                alien.setState(Alien.State.SUCCESS);
            }
        }

        currentGameState = GameState.SUCCESS;

        if (AssetManager.soundEnabled) {
            AssetManager.successMusic.play();
        }


    }

    public void readyToPlay() {

        blackoutTaskStarted = false;


        resetStars();

        randomizeAliens();
        restartWorld();
        currentGameState = GameState.READY_TO_PLAY;
        currentPitch = 0;
    }

    public void goToOptions() {
        currentGameState = GameState.OPTIONS;
    }

    public void goToStats() {
        currentGameState = GameState.STATS;
    }

    public void goToHelp() {
        currentGameState = GameState.HELP;
    }

    public void goToMissions() {
        if (scrollHandler.isStopped) {
            scrollHandler.startScrolling();
        }
        currentGameState = GameState.MISSIONS;
    }

    public void restartWorld() {

        if (missionMode) {
            continuousWalls = false;
            for (int i = 0; i < continuousWallsMissions.length; i++) {
                if (currentMissionNumber == continuousWallsMissions[i]) continuousWalls = true;
            }
        }

        score = 0;
        crushedWallsCounter = 0;
        player.onRestart();
        scrollHandler.onRestart();

        for(int i=0;i<3;i++){
            for (int j = 0; j < alienRows[i].size(); j++){
                alienRows[i].get(j).reset();
            }
        }
        currentGameState = GameState.READY_TO_PLAY;
    }

    public boolean stateIsReadyToPlay() {
        return currentGameState == GameState.READY_TO_PLAY;
    }

    public boolean stateIsOptions() {
        return currentGameState == GameState.OPTIONS;
    }

    public boolean stateIsChallenge() {
        return currentGameState == GameState.MISSIONS;
    }

    public boolean stateIsGameOver() {
        return currentGameState == GameState.GAMEOVER;
    }

    public boolean stateIsSuccess() {
        return currentGameState == GameState.SUCCESS;
    }

    public boolean gameEnded() {
        return currentGameState == GameState.SUCCESS || currentGameState == GameState.GAMEOVER;
    }

    public boolean stateIsHighScore() {
        return currentGameState == GameState.HIGHSCORE;
    }

    public boolean stateIsMenu() {
        return currentGameState == GameState.MENU;
    }

    public boolean stateIsRunning() {
        return currentGameState == GameState.RUNNING;
    }

    public boolean stateIsStats() {
        return currentGameState == GameState.STATS;
    }

    public boolean stateIsHelp() {
        return currentGameState == GameState.HELP;
    }

    public void setStarsToBeDrawn() {
        for (int i = 0; i < 5; i++) {
            if (score >= 0.1 * (i + 1) * AssetManager.missionMaxScores[currentMissionNumber]) {
                starsScheduledToDrawArray[i] = true;
            }
        }
    }


    public void addCrushingParticleEffect() {

        Random random = new Random();
        int effectType = random.nextInt(2);

        if (effectType == 0) {
            ParticleEffectPool.PooledEffect effect = AssetManager.crushingParticleEffectPool.obtain();
            effect.setPosition(activeWall.getWallRectangle().getX(), activeWall.getWallRectangle().getY() + gameHeight / 25);

            float color = ((float) scrollHandler.getActiveWall().wallPitch - 95) * 255 / 1020;
            float[] colors = {color / 255.0f, 1, 1};
            effect.findEmitter("Untitled").getTint().setColors(colors);
            AssetManager.glassParticleEffects.add(effect);
        } else {
            ParticleEffectPool.PooledEffect effect = AssetManager.crushingParticleEffectPool2.obtain();
            effect.setPosition(activeWall.getWallRectangle().getX(), activeWall.getWallRectangle().getY() + gameHeight / 25);

            float color = ((float) scrollHandler.getActiveWall().wallPitch - 95) * 255 / 1020;
            float[] colors = {color / 255.0f, 1, 1};
            effect.findEmitter("Untitled").getTint().setColors(colors);
            AssetManager.glassParticleEffects.add(effect);
        }
    }

    public void addCorrectPitchParticleEffect() {
        ParticleEffectPool.PooledEffect effect = AssetManager.correctPitchParticleEffectPool.obtain();
        effect.setPosition(8, voiceEffectLevel);
        AssetManager.correctPitchParticleEffects.add(effect);
    }

    public void addStarParticleEffect(int x, int y) {
        ParticleEffectPool.PooledEffect effect = AssetManager.starParticleEffectPool.obtain();
        effect.setPosition(x, y);
        AssetManager.starParticleEffects.add(effect);
    }

    public void createAlienRows() {
        for (int i = 0; i < 3; i++) {
            alienRows[i] = new ArrayList<Alien>();
        }
    }

    public void randomizeAliens() {
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            alienRows[i].clear();
            if (random.nextInt(2) == 0) {
                alienRows[i].add(new Alien(random.nextInt(81) + 108, gameHeight / 6 + 40 * i, 80, 100, this));
            } else {
                if (random.nextBoolean()) {
                    alienRows[i].add(new Alien(108, gameHeight / 6 + 40 * i, 80, 100, this));
                }
                if (random.nextBoolean()) {
                    alienRows[i].add(new Alien(188, gameHeight / 6 + 40 * i, 80, 100, this));
                }
            }
        }
    }

    public void activateAliens() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < alienRows[i].size(); j++) {
                Random random = new Random();
                alienRows[i].get(j).coolTimeThreshold = random.nextInt(40) + 80;
                alienRows[i].get(j).waitTimeThreshold = random.nextInt(10) + 1;
                alienRows[i].get(j).setState(Alien.State.BEFORE_COOL);
            }
        }
    }

    public void deactivateAliens() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < alienRows[i].size(); j++) {
                Random random = new Random();
                alienRows[i].get(j).waitTimeThreshold = random.nextInt(10) + 1;
                alienRows[i].get(j).setState(Alien.State.BEFORE_MEH);
            }
        }
    }

    private void updateCurrentGameState(float delta) {
        switch (currentGameState) {
            case READY_TO_PLAY:
                updateMenuState(delta);
                break;
            case MISSIONS:
                updateMenuState(delta);
                break;
            case OPTIONS:
                updateMenuState(delta);
                break;
            case STATS:
                updateMenuState(delta);
                break;
            case HELP:
                updateMenuState(delta);
                break;
            case MENU:
                updateMenuState(delta);
                break;
            case RUNNING:
                updateRunningState(delta);
                break;
            case GAMEOVER:
                updateGameOverState(delta);
                break;
            case SUCCESS:
                updateGameOverState(delta);
                break;
            case HIGHSCORE:
                updateGameOverState(delta);
                break;
            default:
                break;
        }
    }

    public String getClosestNoteString() {
        int index = 0;
        if (currentPitch <= 0) {
            return "";
        } else {
            float distance = 1000;
            for (int i = 0; i < AssetManager.noteNames.length - 1; i++) {
                if (Math.abs(currentPitch - AssetManager.noteFrequencies[i]) < distance) {
                    distance = (float) Math.abs(currentPitch - AssetManager.noteFrequencies[i]);
                    index = i;
                }
            }
            return AssetManager.noteNames[index];
        }
    }

    public void setCurrentSingerNumber(int singerNumber) {
        for (int i = 0; i < 5; i++) {
            ((InputHandler) Gdx.input.getInputProcessor()).getBistableButtons().get(i).isActive = false;
        }
        ((InputHandler) Gdx.input.getInputProcessor()).getBistableButtons().get(singerNumber - 1).isActive = true;
        currentSingerNumber = singerNumber;
    }

    public void createMonit(TextureRegion texture, float growth, float time) {
        int width = 100;
        Monit monit = new Monit(136 - width / 2, gameHeight / 2 - 5, width, 11, texture, growth, time, 0);
        monits.add(monit);
    }

    public void createMissionCompletedCloud() {
        int width = 100;
        Monit monit = new Monit(136 - width / 2, gameHeight * 0.4f, width, 100, AssetManager.starTextureRegion, 2, 1, 0);
        monits.add(monit);
    }

    public void gameOver() {
        prepareSuccessTransition();
        createMonit(AssetManager.monitsTextureRegions[6], 3, 2);
        player.die();
        scrollHandler.gameOverStopShowingWalls = true;
        currentGameState = GameState.GAMEOVER;
        AssetManager.playMusic(AssetManager.gameOverMusic);
        AssetManager.vibrate(600);
        updateHighScores();
        updateWallFailureNumbers();
    }

    public int getCurrentHour() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    private void removeObsoleteParticleEffects() {
        for (int i = AssetManager.glassParticleEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = AssetManager.glassParticleEffects.get(i);
            if (effect.findEmitter("Untitled").getPercentComplete() >= 0.5) {
                effect.free();
                AssetManager.glassParticleEffects.removeIndex(i);
            }
        }

        for (int i = AssetManager.correctPitchParticleEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = AssetManager.correctPitchParticleEffects.get(i);
            if (effect.findEmitter("good").getPercentComplete() >= 1) {
                effect.free();
                AssetManager.correctPitchParticleEffects.removeIndex(i);
            }
        }
    }

    private void setCorrectPitchBoundaries() {
        if (missionMode && sinusoidalWallMission()) {
            minCorrectPitch = activeWall.wallPitch - 0.056 * activeWall.wallPitch;
            maxCorrectPitch = activeWall.wallPitch + 0.056 * activeWall.wallPitch;
        } else if (!missionMode && currentSingerNumber == 5) {
            minCorrectPitch = AssetManager.noteFrequencies[activeWall.noteNumber - 2];
            maxCorrectPitch = AssetManager.noteFrequencies[activeWall.noteNumber + 2];
        } else {
            minCorrectPitch = AssetManager.noteFrequencies[activeWall.noteNumber - 1];
            maxCorrectPitch = AssetManager.noteFrequencies[activeWall.noteNumber + 1];
        }
    }

    private void reloadAd() {
        if (AssetManager.adInterface.isWifiConnected()) {
            if (!previousWiFiState) {
                AssetManager.adInterface.reloadAd();
                previousWiFiState = true;
            }
        }
    }

    private void updateAccelerometerValues() {
        accelerometerValues = AssetManager.sensorInterface.getAccelerometerValues();
        accelerometerDeltaZ = accelerometerValues[3];
    }

    public void prepareSuccessTransition() {
        blackAlphaValue.setValue(0);
        Tween.registerAccessor(Value.class, new ValueAccessor());
        blackoutTweenManager = new TweenManager();
        Tween.to(blackAlphaValue, -1, 0.4f).target(0.6f).ease(TweenEquations.easeOutQuad).start(blackoutTweenManager);
    }

    private float stabilizeFramerate(float delta) {
        if (delta > .15f) {
            delta = .15f;
        }
        return delta;
    }

    private void playStarMusic(int i) {
        if (AssetManager.soundEnabled) {
            AssetManager.starMusic[i].play();
        }
    }

    private void playCrushingSound() {
        if (AssetManager.soundEnabled) {
            if (!AssetManager.glassMusic.isPlaying()) {
                AssetManager.glassMusic.play();
            } else {
                if (!(AssetManager.glassMusic.getPosition() > 0.2)) {
                    AssetManager.glassMusic.play();
                }
            }
        }
    }

    private void processGameSounds() {
        if (!AssetManager.soundEnabled && (AssetManager.themeMusic.isPlaying())) {
            AssetManager.themeMusic.stop();
        }
        if (AssetManager.themeMusic.isPlaying() && stateIsReadyToPlay()) {
            AssetManager.themeMusic.stop();
        }
        glassSoundCounter++;
        currentVolume = SoundHandler.getSoundVolume();
    }

    private void updateAliens() {
        if(!gamePaused){
            for (int i=0;i<3;i++){
                for (int j = 0; j < alienRows[i].size(); j++) {
                    alienRows[i].get(j).update();
                }
            }
        }
    }

    private void playThemeMusic() {
        if (!stateIsReadyToPlay() && AssetManager.soundEnabled && !AssetManager.themeMusic.isPlaying()) {
            AssetManager.themeMusic.play();
        }
    }

    private void updateBlackout() {
        if (blackAlphaValue.getValue() >= 0.55 && !blackoutTaskStarted) {
            blackoutTaskStarted = true;
            scheduleStarDisplayTasks();
        }
    }

    private void increaseFreePlayWallSpeed() {
        if (!missionMode) {
            for (int i = 1; i < 15; i++) {
                if (score == 5 * i) {
                    ScrollHandler.WALL_SCROLL_SPEED = -(20 + 10 * i);
                }
            }
        }
    }

    private void scheduleStarDisplayTasks() {

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (starsScheduledToDrawArray[0]) {
                    playStarMusic(1);
                    starsAlreadyDrawnArray[0] = true;
                    addStarParticleEffect(55, midPointY + 14);
                } else {
                    allStarsDrawn = true;
                }
            }
        }, 0);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (starsScheduledToDrawArray[1]) {
                    playStarMusic(2);
                    starsAlreadyDrawnArray[1] = true;
                    addStarParticleEffect(95, midPointY + 14);
                } else {
                    allStarsDrawn = true;
                }
            }
        }, 0.4f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (starsScheduledToDrawArray[2]) {
                    playStarMusic(3);
                    starsAlreadyDrawnArray[2] = true;
                    addStarParticleEffect(135, midPointY + 14);
                } else {
                    allStarsDrawn = true;
                }
            }
        }, 0.8f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (starsScheduledToDrawArray[3]) {
                    playStarMusic(4);
                    starsAlreadyDrawnArray[3] = true;
                    addStarParticleEffect(175, midPointY + 14);
                } else {
                    allStarsDrawn = true;
                }
            }
        }, 1.2f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (starsScheduledToDrawArray[4]) {
                    playStarMusic(5);
                    starsAlreadyDrawnArray[4] = true;
                    addStarParticleEffect(215, midPointY + 14);
                }
                allStarsDrawn = true;
            }
        }, 1.6f);
    }

    private void resetStars() {
        // Reset stars' booleans
        for (int i = 0; i < 5; i++) {
            starsAlreadyDrawnArray[i] = false;
            starsScheduledToDrawArray[i] = false;
        }
        allStarsDrawn = false;

        // Reset stars' particle effects
        for (int i = AssetManager.starParticleEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = AssetManager.starParticleEffects.get(i);
            effect.free();
            AssetManager.starParticleEffects.removeIndex(i);
        }
    }

    private void updateHighScores() {
        if (!missionMode) {
            AssetManager.addOverallWallsCount(score);
            AssetManager.addFreePlayWalls(score);
            for (int i = 0; i < 5; i++) {
                if (score >= AssetManager.freePlayStarThresholdScoresArray[i]) {
                    starsScheduledToDrawArray[i] = true;
                }
            }
        } else {
            AssetManager.addOverallWallsCount(crushedWallsCounter);
            for (int i = 0; i < 5; i++) {
                if (score >= 0.1 * (i + 1) * AssetManager.missionMaxScores[currentMissionNumber]) {
                    starsScheduledToDrawArray[i] = true;
                }
            }
        }
    }

    private void updateWallFailureNumbers() {
        if (missionMode && currentMissionNumber == 43) {
            AssetManager.addMission43Failure();
        }
        AssetManager.addWallFailure(activeWall.noteNumber);
    }

    public boolean voiceLevelMission() {
        if (currentMissionNumber == AssetManager.MISSION_SILENCE_IS_GOLDEN ||
                currentMissionNumber == AssetManager.MISSION_HEAR_ME_ROAR ||
                currentMissionNumber == AssetManager.MISSION_HUMMING_GENTLY ||
                currentMissionNumber == AssetManager.MISSION_HEAR_ME_ROAR ||
                currentMissionNumber == AssetManager.MISSION_TWIST_AND_SHOUT) {
            return true;
        } else {
            return false;
        }
    }

    public boolean sinusoidalWallMission() {
        if (currentMissionNumber == AssetManager.MISSION_TRIGONOMETRY_101 ||
                currentMissionNumber == AssetManager.MISSION_TRIGONOMETRY_102 ||
                currentMissionNumber == AssetManager.MISSION_TRIGONOMETRY_EXAM) {
            return true;
        } else {
            return false;
        }
    }

    private void setLastWallForMission13() {
        if (missionMode && currentMissionNumber == AssetManager.MISSION_DONT_COUNT_YOUR_CHICKENS) {
            if (scrollHandler.currentWallNumber == AssetManager.MISSION_13_FAST_WALL_NUMBER) {
                ScrollHandler.WALL_SCROLL_SPEED = -150;
                activeWall.setVelocity(AssetManager.MISSION_13_FAST_WALL_VELOCITY);
            }

        }
    }


}
