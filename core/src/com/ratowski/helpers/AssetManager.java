package com.ratowski.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class AssetManager {

    // Interfaces
    public static InternetInterface internetInterface;
    public static SensorInterface sensorInterface;
    public static AdInterface adInterface;

    // Textures
    public static Texture metalTexture, metalTexture2, skyTexture, starsTexture;
    public static Texture freePlayGameOverScoreboardTexture, successScoreboardTexture, missionModeGameOverScoreboardTexture;
    public static Texture pausePanelTexture, challengePanelTexture, playerNamesTexture, helpTexture, optionsTexture;
    public static Texture numbersButtonsTexture, otherButtonsTexture, menuButtonsTexture;
    public static Texture endGameMonitsTexture, highScoreMonitTexture;
    public static Texture blackRectangleTexture, colorDotsTexture;
    public static Texture gameLogoTexture, splashScreenLogoTexture;
    public static Texture lucianoTexture, joseTexture, mariaTexture, joanTexture, secretPlayerTexture, alienTexture;

    // Texture Regions
    public static TextureRegion[][] menuButtonsTextureRegions = new TextureRegion[2][2];
    public static TextureRegion[][] otherButtonsTextureRegions = new TextureRegion[21][2];
    public static TextureRegion[][] numberButtonsTextureRegions = new TextureRegion[64][2];
    public static TextureRegion[] helpPagesTextureRegions = new TextureRegion[4];
    public static TextureRegion[] monitsTextureRegions = new TextureRegion[7];
    public static TextureRegion blackColorTextureRegion, pinkColorTextureRegion, greenColorTextureRegion, fakeBlackTextureRegion;
    public static TextureRegion metalTextureRegion, metalTextureRegion2, skyTextureRegion;
    public static TextureRegion alienTextureRegion[] = new TextureRegion[15];
    public static TextureRegion highScoreTextureRegion[] = new TextureRegion[2];
    public static TextureRegion singersTextureRegion[][] = new TextureRegion[6][7];
    public static TextureRegion playerNamesTextureRegion[] = new TextureRegion[6];
    public static TextureRegion splashScreenLogoTextureRegion, gameLogoTextureRegion;
    public static TextureRegion gameOverScoreboardTextureRegion, successScoreboardTextureRegion;
    public static TextureRegion starTextureRegion, noStarTextureRegion;

    // Game World Variables
    public static int freePlayStarThresholdScoresArray[] = new int[]{5, 10, 15, 20, 25};
    public static double[] noteFrequencies = new double[43];
    public static String[] noteNames = new String[43];

    // Stats
    public static int selectedSinger = 1;
    public static int[] singerGamesPlayedArray = new int[6];
    public static int favouriteSingerIndex = -1;
    public static int favouriteSingerGamesPlayed = 0;
    public static int[] wallFailures = new int[43];
    public static int worstWallIndex = -1;
    public static int worstWallScore = 0;
    public static int overallWallsDestroyed = 0;
    public static int freePlayWallsDestroyed = 0;
    public static int freePlayHighScore = 0;
    public static int gamesPlayed = 0;
    public static int missionsCompleted = 0;
    public static int mission43Failures = 0;
    public static int bestMissionIndex = -1;
    public static float bestMissionScore = 0;
    public static boolean secretPlayerUnlocked = false;

    // Fonts
    public static BitmapFont numberFont, smallWhiteFont, smallYellowFont, scoreFont;

    // Missions' parameters
    public static int missionTempos[] = new int[65];
    public static int missionSuccesses[] = new int[65];
    public static int missionNotesPitchArray[][] = new int[65][];
    public static int missionNotesHealthArray[][] = new int[65][];
    public static int missionNotesNewHealthArray[] = new int[65];
    public static int missionNotesIntervalArray[][] = new int[65][];
    public static int[] missionHighScores = new int[65];
    public static int[] missionMaxScores = new int[65];
    public static String missionNames[] = new String[65];
    public static int[] notOneWallAtATimeMissionNumbers = new int[24];
    public static int missionsWithPredefinedWalls[] = {4, 7, 8, 10, 12, 16, 17, 20, 24, 28, 31, 32, 36, 40, 42, 44, 48, 52, 53, 56, 60, 64};

    // Camera changes missions' numbers
    public static final int MISSION_LIGHT_TURBULENCE = 3;
    public static final int MISSION_FASTEN_YOUR_SEATBELTS = 21;
    public static final int MISSION_METEOR_SHOWER = 38;
    public static final int MISSION_SOMETHING_WRONG = 11;
    public static final int MISSION_CANT_GET_IT_RIGHT = 29;
    public static final int MISSION_GRANDFATHERS_CLOCK = 34;
    public static final int MISSION_DIZZY_MISS_LIZZY = 49;
    public static final int MISSION_YOU_DONT_KNOW_WHEN = 54;

    // Camera filter missions' names
    public static final int MISSION_ENERGY_SAVING = 19;
    public static final int MISSION_ROSE_COLORED_GLASSES = 25;
    public static final int MISSION_THINK_FAST_IN_GREEN = 47;
    public static final int MISSION_LIGHTS_OUT = 57;

    // Accelerometer missions' names
    public static final int MISSION_SHAKE_IT_BABY = 14;
    public static final int MISSION_SHAKE_IT_REAL_GOOD = 41;

    // Voice level missions' names
    public static final int MISSION_SILENCE_IS_GOLDEN = 9;
    public static final int MISSION_HEAR_ME_ROAR = 26;
    public static final int MISSION_HUMMING_GENTLY = 35;
    public static final int MISSION_HEAR_ME_ROARER = 51;

    // Accelerometer & voice level mission name
    public static final int MISSION_TWIST_AND_SHOUT = 62;

    // Touch screen mission names'
    public static final int MISSION_THE_VOICE_IS_NOT_ENOUGH = 58;
    public static final int MISSION_TRY_YET_ANOTHER_WAY = 23;

    // Sinusoidal missions' names
    public static final int MISSION_TRIGONOMETRY_101 = 30;
    public static final int MISSION_TRIGONOMETRY_102 = 45;
    public static final int MISSION_TRIGONOMETRY_EXAM = 59;

    // Other specific conditions missions' names
    public static final int MISSION_SOMEONE_HAS_TO_LOOK = 2;
    public static final int MISSION_THE_LUCKY_NUMBER = 7;
    public static final int MISSION_DONT_COUNT_YOUR_CHICKENS = 13;
    public static final int MISSION_GOOD_ACOUSTICS = 22;
    public static final int MISSION_HEAR_THEM_CRUSH = 33;
    public static final int MISSION_RENDEZVOUZ_AT_MIDNIGHT = 37;
    public static final int MISSION_THIRD_TIMES_A_CHARM = 43;

    // Specific walls
    public static final int MISSION_13_FAST_WALL_NUMBER = 10;
    public static final int MISSION_13_FAST_WALL_VELOCITY = -150;

    // Animations
    public static Animation highScoreAnimation;
    public static Animation alienAnimation[] = new Animation[2];
    public static Animation singersAnimation[] = new Animation[6];

    // Particle Effects
    public static ParticleEffect effect, effect2, effect3, effect4;
    public static ParticleEffectPool crushingParticleEffectPool, crushingParticleEffectPool2;
    public static ParticleEffectPool correctPitchParticleEffectPool, starParticleEffectPool;
    public static Array<ParticleEffectPool.PooledEffect> glassParticleEffects = new Array();
    public static Array<ParticleEffectPool.PooledEffect> correctPitchParticleEffects = new Array();
    public static Array<ParticleEffectPool.PooledEffect> starParticleEffects = new Array();

    // Sounds
    public static Sound clickSound;
    public static Music[] starMusic = new Music[6];
    public static Music glassMusic;
    public static Music themeMusic, gameOverMusic, successMusic;

    // User preferences
    public static Preferences userPreferences;
    public static boolean vibrationEnabled = true;
    public static boolean soundEnabled = false;
    public static boolean aliensEnabled = false;

    // Button enums

    public static void loadAssets(InternetInterface internetInterface, SensorInterface sensorInterface, AdInterface adInterface) {

        loadInterfaces(internetInterface, sensorInterface, adInterface);
        loadUserPreferences();
        loadStats();
        updateStats();
        loadTextures();
        createTextureRegions();
        createAnimations();
        createParticleEffects();
        loadFonts();
        loadSounds();

        try {
            loadMissionNotes();
            loadMissionNames();
            loadMissionSettings();
            loadNoteFrequencies();
            loadNoteNames();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadMissionScores();
    }

    public static void dispose() {
        disposeTextures();
        disposeSounds();
    }


    public static void loadUserPreferences() {

        userPreferences = Gdx.app.getPreferences("Player");

        if (!userPreferences.contains("highScore")) {
            freePlayHighScore = 0;
            userPreferences.putInteger("highScore", 0);
            userPreferences.flush();
        } else {
            freePlayHighScore = userPreferences.getInteger("highScore");
        }

        if (!userPreferences.contains("currentSinger")) {
            selectedSinger = 1;
            userPreferences.putInteger("currentSinger", 1);
            userPreferences.flush();
        } else {
            selectedSinger = userPreferences.getInteger("currentSinger");
        }

        if (!userPreferences.contains("overallwalls")) {
            overallWallsDestroyed = 0;
            userPreferences.putInteger("overallwalls", 0);
            userPreferences.flush();
        } else {
            overallWallsDestroyed = userPreferences.getInteger("overallwalls");
        }

        if (!userPreferences.contains("freePlayWalls")) {
            freePlayWallsDestroyed = 0;
            userPreferences.putInteger("freePlayWalls", 0);
            userPreferences.flush();
        } else {
            freePlayWallsDestroyed = userPreferences.getInteger("freePlayWalls");
        }

        if (!userPreferences.contains("missionsCompleted")) {
            missionsCompleted = 0;
            userPreferences.putInteger("missionsCompleted", 0);
            userPreferences.flush();
        } else {
            missionsCompleted = userPreferences.getInteger("missionsCompleted");
        }

        if (!userPreferences.contains("mission43Failures")) {
            mission43Failures = 0;
            userPreferences.putInteger("mission43Failures", 0);
            userPreferences.flush();
        } else {
            mission43Failures = userPreferences.getInteger("mission43Failures");
        }

        if (!userPreferences.contains("gamesPlayed")) {
            gamesPlayed = 0;
            userPreferences.putInteger("gamesPlayed", 0);
            userPreferences.flush();
        } else {
            gamesPlayed = userPreferences.getInteger("gamesPlayed");
        }

        if (!userPreferences.contains("vibrationEnabled")) {
            userPreferences.putBoolean("vibrationEnabled", true);
            userPreferences.flush();
            vibrationEnabled = true;
        } else {
            vibrationEnabled = userPreferences.getBoolean("vibrationEnabled");
        }

        if (!userPreferences.contains("soundEnabled")) {
            userPreferences.putBoolean("soundEnabled", false);
            userPreferences.flush();
            soundEnabled = false;
        } else {
            soundEnabled = userPreferences.getBoolean("soundEnabled");
        }

        if (!userPreferences.contains("aliensEnabled")) {
            userPreferences.putBoolean("aliensEnabled", false);
            userPreferences.flush();
            aliensEnabled = false;
        } else {
            aliensEnabled = userPreferences.getBoolean("aliensEnabled");
        }

        if (userPreferences.contains("secretPlayer")) {
            secretPlayerUnlocked = true;
        }

    }

    public static void unlockSecretPlayer() {
        secretPlayerUnlocked = true;
        userPreferences.putBoolean("secretPlayer", true);
        userPreferences.flush();
    }

    public static void addOverallWallsCount(int walls) {
        overallWallsDestroyed += walls;
        userPreferences.putInteger("overallwalls", overallWallsDestroyed);
        userPreferences.flush();
    }

    public static void addFreePlayWalls(int walls) {
        freePlayWallsDestroyed += walls;
        userPreferences.putInteger("freeplaywalls", freePlayWallsDestroyed);
        userPreferences.flush();
    }

    public static void addMission43Failure() {
        mission43Failures++;
        userPreferences.putInteger("mission43Failures", mission43Failures);
        userPreferences.flush();
    }

    public static void addGamesPlayed() {
        gamesPlayed++;
        userPreferences.putInteger("gamesplayed", gamesPlayed);
        userPreferences.flush();
    }

    public static void addWallFailure(int index) {
        wallFailures[index]++;
        String string = "wallfail" + index;
        userPreferences.putInteger(string, wallFailures[index]);
        userPreferences.flush();
    }

    public static void addSingerTimesPlayed(int index) {
        singerGamesPlayedArray[index]++;
        String string = "singerPlayed" + index;
        userPreferences.putInteger(string, singerGamesPlayedArray[index]);
        userPreferences.flush();
    }

    public static void setHighScore(int val) {
        freePlayHighScore = val;
        userPreferences.putInteger("highScore", freePlayHighScore);
        userPreferences.flush();
    }

    public static int getMissionScore(int num) {
        return missionHighScores[num];
    }

    public static void setMissionCompleted(int missionNumber, int score) {

        if (score > missionHighScores[missionNumber]) {
            missionHighScores[missionNumber] = score;
            String preferenceName = "missionMode" + missionNumber;
            userPreferences.putInteger(preferenceName, score);
            userPreferences.flush();
        }

        if (missionsCompleted < missionNumber) {
            missionsCompleted = missionNumber;
            userPreferences.putInteger("missionsCompleted", missionNumber);
            userPreferences.flush();
        }

        if (missionsCompleted == 64) secretPlayerUnlocked = true;
    }

    public static void setSelectedSinger(int val) {
        selectedSinger = val;
        userPreferences.putInteger("selectedSinger", val);
        userPreferences.flush();
    }

    public static void setVibrationEnabled(boolean vib) {
        vibrationEnabled = vib;
        userPreferences.putBoolean("vibrationEnabled", vib);
        userPreferences.flush();
    }

    public static void setSoundEnabled(boolean snd) {
        soundEnabled = snd;
        userPreferences.putBoolean("soundEnabled", snd);
        userPreferences.flush();
    }

    public static void setAliensEnabled(boolean bool) {
        aliensEnabled = bool;
        userPreferences.putBoolean("aliensEnabled", bool);
        userPreferences.flush();
    }

    public static int getSelectedSinger() {
        return selectedSinger;
    }

    public static void loadMissionNotes() throws IOException {

        ArrayList lines = new ArrayList();
        BufferedReader reader = loadTextFile("data/missionNotes.txt");

        while (true) {
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                break;
            }
            if (!line.startsWith("!")) {
                lines.add(line);
            }
        }

        for (int i = 0; i < 16; i++) {
            String line = (String) lines.get(i * 3);
            String notes[] = line.split(",");
            int notesLength = notes.length;

            missionNotesPitchArray[(i + 1) * 4] = new int[notesLength];
            missionNotesHealthArray[(i + 1) * 4] = new int[notesLength];
            missionNotesIntervalArray[(i + 1) * 4] = new int[100];

            for (int j = 0; j < notesLength; j++) {
                missionNotesPitchArray[(i + 1) * 4][j] = Integer.parseInt(notes[j]);
            }

            line = (String) lines.get(i * 3 + 1);
            notes = line.split(",");

            for (int j = 0; j < notesLength; j++) {
                missionNotesHealthArray[(i + 1) * 4][j] = Integer.parseInt(notes[j]);
            }

            line = (String) lines.get(i * 3 + 2);
            notes = line.split(",");

            for (int j = 0; j < notesLength; j++) {
                missionNotesIntervalArray[(i + 1) * 4][j] = Integer.parseInt(notes[j]);
            }
        }

        String line = (String) lines.get(48);
        String notes[] = line.split(",");
        int notesLength = notes.length;
        missionNotesPitchArray[17] = new int[notesLength];
        missionNotesHealthArray[17] = new int[notesLength];
        missionNotesIntervalArray[17] = new int[100];
        for (int j = 0; j < notesLength; j++)
            missionNotesPitchArray[17][j] = Integer.parseInt(notes[j]);
        line = (String) lines.get(49);
        notes = line.split(",");
        for (int j = 0; j < notesLength; j++)
            missionNotesHealthArray[17][j] = Integer.parseInt(notes[j]);
        line = (String) lines.get(50);
        notes = line.split(",");
        for (int j = 0; j < notesLength; j++)
            missionNotesIntervalArray[17][j] = Integer.parseInt(notes[j]);

        line = (String) lines.get(51);
        notes = line.split(",");
        notesLength = notes.length;
        missionNotesPitchArray[27] = new int[notesLength];
        missionNotesHealthArray[27] = new int[notesLength];
        missionNotesIntervalArray[27] = new int[100];
        for (int j = 0; j < notesLength; j++)
            missionNotesPitchArray[27][j] = Integer.parseInt(notes[j]);
        line = (String) lines.get(52);
        notes = line.split(",");
        for (int j = 0; j < notesLength; j++)
            missionNotesHealthArray[27][j] = Integer.parseInt(notes[j]);
        line = (String) lines.get(53);
        notes = line.split(",");
        for (int j = 0; j < notesLength; j++)
            missionNotesIntervalArray[27][j] = Integer.parseInt(notes[j]);

        line = (String) lines.get(54);
        notes = line.split(",");
        notesLength = notes.length;
        missionNotesPitchArray[42] = new int[notesLength];
        missionNotesHealthArray[42] = new int[notesLength];
        missionNotesIntervalArray[42] = new int[100];
        for (int j = 0; j < notesLength; j++)
            missionNotesPitchArray[42][j] = Integer.parseInt(notes[j]);
        line = (String) lines.get(55);
        notes = line.split(",");
        for (int j = 0; j < notesLength; j++)
            missionNotesHealthArray[42][j] = Integer.parseInt(notes[j]);
        line = (String) lines.get(56);
        notes = line.split(",");
        for (int j = 0; j < notesLength; j++)
            missionNotesIntervalArray[42][j] = Integer.parseInt(notes[j]);

        line = (String) lines.get(57);
        notes = line.split(",");
        notesLength = notes.length;
        missionNotesPitchArray[53] = new int[notesLength];
        missionNotesHealthArray[53] = new int[notesLength];
        missionNotesIntervalArray[53] = new int[100];
        for (int j = 0; j < notesLength; j++)
            missionNotesPitchArray[53][j] = Integer.parseInt(notes[j]);
        line = (String) lines.get(58);
        notes = line.split(",");
        for (int j = 0; j < notesLength; j++)
            missionNotesHealthArray[53][j] = Integer.parseInt(notes[j]);
        line = (String) lines.get(59);
        notes = line.split(",");
        for (int j = 0; j < notesLength; j++)
            missionNotesIntervalArray[53][j] = Integer.parseInt(notes[j]);


        line = (String) lines.get(60);
        notes = line.split(",");
        notesLength = notes.length;
        missionNotesPitchArray[61] = new int[notesLength];
        missionNotesHealthArray[61] = new int[notesLength];
        missionNotesIntervalArray[61] = new int[100];

        for (int j = 0; j < notesLength; j++) {
            Random random = new Random();
            missionNotesPitchArray[61][j] = random.nextInt(20) + 2;
        }

        line = (String) lines.get(61);
        notes = line.split(",");
        for (int j = 0; j < notesLength; j++)
            missionNotesHealthArray[61][j] = Integer.parseInt(notes[j]);
        line = (String) lines.get(62);
        notes = line.split(",");
        for (int j = 0; j < notesLength; j++) {
            missionNotesIntervalArray[61][j] = Integer.parseInt(notes[j]);
        }

        line = (String) lines.get(63);
        notes = line.split(",");
        notesLength = notes.length;
        missionNotesPitchArray[39] = new int[notesLength];
        missionNotesHealthArray[39] = new int[notesLength];
        missionNotesIntervalArray[39] = new int[100];

        for (int j = 0; j < notesLength; j++) {
            Random random = new Random();
            missionNotesPitchArray[39][j] = random.nextInt(20) + 2;
        }

        for (int j = 0; j < notesLength; j++) {
            Random random = new Random();
            missionNotesHealthArray[39][j] = random.nextInt(5) * 10 + 20;
        }

        line = (String) lines.get(65);
        notes = line.split(",");
        for (int j = 0; j < notesLength; j++)
            missionNotesIntervalArray[39][j] = Integer.parseInt(notes[j]);

        loadSingleWallMissionNotes(10, 2000);
        loadSingleWallMissionNotes(31, 4000);
    }

    private static void loadSingleWallMissionNotes(int missionNumber, int wallHealth) {
        missionNotesPitchArray[missionNumber] = new int[1];
        missionNotesHealthArray[missionNumber] = new int[1];
        missionNotesIntervalArray[missionNumber] = new int[1];
        missionNotesPitchArray[missionNumber][0] = 10;
        missionNotesHealthArray[missionNumber][0] = wallHealth;
        missionNotesIntervalArray[missionNumber][0] = 10;
    }

    public static void loadMissionNames() throws IOException {

        ArrayList lines = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(Gdx.files.internal("data/missionNames.txt").read()), 2048);

        while (true) {
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                break;
            }
            if (!line.startsWith("!")) {
                lines.add(line);
            }
        }

        for (int i = 0; i < 64; i++) {
            String line = (String) lines.get(i);
            missionNames[i] = line;
        }
    }

    public static void loadNoteFrequencies() throws IOException {
        BufferedReader reader = loadTextFile("data/noteFrequencies.txt");
        String line = reader.readLine();
        String[] noteFrequenciesStrings = line.split(",");

        for (int i = 0; i < noteFrequenciesStrings.length; i++) {
            noteFrequencies[i] = Double.parseDouble(noteFrequenciesStrings[i]);
        }
        reader.close();
    }

    public static void loadNoteNames() throws IOException {
        BufferedReader reader = loadTextFile("data/noteNames.txt");
        String line = reader.readLine();
        noteNames = line.split(",");
        reader.close();
    }

    public static void loadMissionSettings() throws IOException {
        BufferedReader reader = loadTextFile("data/continuousMissionNumbers.txt");
        String line = reader.readLine();
        String[] missionNumberStrings = line.split(",");

        for (int i = 0; i < missionNumberStrings.length; i++) {
            notOneWallAtATimeMissionNumbers[i] = Integer.parseInt(missionNumberStrings[i]);
        }
        reader.close();


        missionTempos = new int[]{0, -20, -20, -20, -30, -30, -30, -30, -40, -40, -40, -40, -40, -20, -40, -20, -40, -30, -5, -40, -40, -40, -50, -70, -50, -50, -40, -20, -40, -40, -30, -40, -40, -60, -50, -30, -50, -70, -50, -30, -50, -40, -80, -70, -40, -50, -90, -90, -40, -40, -70, -60, -50, -90, -70, -90, -50, -30, -50, -50, -50, -60, -70, -100, -20};
        missionMaxScores = new int[]{0, 750, 1600, 2000, 0, 700, 2500, 1500, 0, 1250, 10000, 2500, 0, 2500, 1500, 250, 0, 0, 2500, 2000, 0, 2500, 4000, 2000, 0, 4500, 2500, 0, 0, 3000, 3000, 20000, 0, 3500, 2500, 1600, 0, 3000, 2500, 0, 0, 2000, 0, 3000, 0, 3000, 1500, 1500, 0, 3750, 2500, 2000, 0, 0, 2500, 4500, 0, 2000, 3000, 4500, 0, 0, 2500, 4500, 0};
        missionSuccesses = new int[]{0, 5, 8, 10, 0, 5, 10, 6, 0, 5, 1, 10, 0, 10, 5, 1, 0, 0, 10, 10, 0, 10, 10, 5, 0, 15, 10, 0, 0, 10, 10, 1, 0, 10, 10, 8, 0, 10, 10, 0, 0, 10, 0, 10, 0, 10, 10, 10, 0, 10, 10, 10, 0, 0, 10, 15, 0, 10, 10, 15, 0, 0, 10, 15, 0};
        missionNotesNewHealthArray = new int[]{0, 30, 40, 40, 0, 70, 50, 50, 0, 50, 2000, 50, 0, 50, 60, 50, 0, 0, 50, 40, 0, 50, 80, 80, 0, 60, 50, 0, 0, 60, 60, 4000, 0, 70, 50, 40, 0, 60, 50, 0, 0, 40, 0, 60, 0, 60, 30, 30, 0, 50, 50, 40, 0, 0, 50, 60, 0, 40, 60, 60, 0, 0, 50, 60, 0};

        for (int i = 1; i < 17; i++) {
            int currentMissionMaxScore = 0;

            for (int j = 0; j < missionNotesPitchArray[4 * i].length; j++) {
                currentMissionMaxScore += missionNotesHealthArray[4 * i][j] * 5;
            }

            missionMaxScores[4 * i] = currentMissionMaxScore;
        }

        int strangeMissions[] = new int[]{17, 27, 39, 42, 53, 61};

        for (int i = 0; i < strangeMissions.length; i++) {
            for (int j = 0; j < missionNotesPitchArray[strangeMissions[i]].length; j++) {
                missionMaxScores[strangeMissions[i]] += missionNotesHealthArray[strangeMissions[i]][j] * 5;
            }
        }
    }

    public static void loadMissionScores() {
        for (int i = 1; i < 65; i++) {
            String string = "missionMode" + i;
            if (userPreferences.contains(string)) {
                missionHighScores[i] = userPreferences.getInteger(string);
            }
        }
    }

    public static void loadStats() {
        String string;

        for (int i = 0; i < 43; i++) {
            string = "wallfail" + i;
            if (userPreferences.contains(string)) {
                wallFailures[i] = userPreferences.getInteger(string);
            } else {
                wallFailures[i] = 0;
            }
        }

        for (int i = 1; i < 6; i++) {
            string = "singerPlayed" + i;
            if (userPreferences.contains(string)) {
                singerGamesPlayedArray[i] = userPreferences.getInteger(string);
            } else {
                singerGamesPlayedArray[i] = 0;
            }
        }

    }

    public static void updateStats() {
        for (int i = 0; i < 43; i++) {
            if (wallFailures[i] > worstWallScore) {
                worstWallScore = wallFailures[i];
                worstWallIndex = i;
            }
        }

        for (int i = 1; i < 6; i++) {
            if (singerGamesPlayedArray[i] > favouriteSingerGamesPlayed) {
                favouriteSingerGamesPlayed = singerGamesPlayedArray[i];
                favouriteSingerIndex = i;
            }
        }

        for (int i = 1; i < 65; i++) {
            if ((float) missionHighScores[i] / missionMaxScores[i] > bestMissionScore) {
                bestMissionScore = (float) missionHighScores[i] / missionMaxScores[i];
                bestMissionIndex = i;
            }
        }
    }

    private static BufferedReader loadTextFile(String filePath) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Gdx.files.internal(filePath).read()), 2048);
        return reader;
    }

    private static void loadTextures() {
        freePlayGameOverScoreboardTexture = loadTexture("data/board.png");
        successScoreboardTexture = loadTexture("data/board2.png");
        missionModeGameOverScoreboardTexture = loadTexture("data/board3.png");
        helpTexture = loadTexture("data/help_all.png");
        optionsTexture = loadTexture("data/options.png");
        numbersButtonsTexture = loadTexture("data/buttons_numbers.png");
        otherButtonsTexture = loadTexture("data/buttons_other.png");
        menuButtonsTexture = loadTexture("data/buttons_menu.png");
        metalTexture = loadTexture("data/metal4.png");
        metalTexture2 = loadTexture("data/metal5.png");
        starsTexture = loadTexture("data/stars.png");
        endGameMonitsTexture = loadTexture("data/monits.png");
        alienTexture = loadTexture("data/alien2.png");
        highScoreMonitTexture = loadTexture("data/highscore.png");
        blackRectangleTexture = loadTexture("data/black.png");
        skyTexture = loadTexture("data/skyTexture.png");
        playerNamesTexture = loadTexture("data/players.png");
        splashScreenLogoTexture = loadTexture("data/logo_new.png");
        gameLogoTexture = loadTexture("data/logoZ.png");
        colorDotsTexture = loadTexture("data/colordots.png");
        lucianoTexture = loadTexture("data/luciano.png");
        joseTexture = loadTexture("data/jose.png");
        joanTexture = loadTexture("data/joan.png");
        mariaTexture = loadTexture("data/maria.png");
        secretPlayerTexture = loadTexture("data/seba.png");
        pausePanelTexture = loadTexture("data/pause.png");
        challengePanelTexture = loadTexture("data/challenge.png");
    }

    private static Texture loadTexture(String filePath) {
        Texture texture = new Texture(Gdx.files.internal(filePath));
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        return texture;
    }

    private static void createTextureRegions() {
        for (int i = 0; i < 4; i++) {
            helpPagesTextureRegions[i] = new TextureRegion(helpTexture, i * 210, 0, 210, 350);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                alienTextureRegion[i * 4 + j] = new TextureRegion(alienTexture, j * 40, i * 50, 40, 50);
            }
        }

        for (int i = 0; i < 2; i++) {
            highScoreTextureRegion[i] = new TextureRegion(highScoreMonitTexture, 0, i * 26, 200, 26);
        }

        blackColorTextureRegion = new TextureRegion(blackRectangleTexture);

        for (int i = 1; i < 6; i++) {
            playerNamesTextureRegion[i] = new TextureRegion(playerNamesTexture, 0, 70 * (i - 1), 230, 70);
        }

        fakeBlackTextureRegion = new TextureRegion(blackColorTextureRegion, 5, 5, 1, 1);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                menuButtonsTextureRegions[i][j] = new TextureRegion(menuButtonsTexture, 120 * i, 140 * j, 120, 140);
            }
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 2; j++) {
                numberButtonsTextureRegions[i][j] = new TextureRegion(numbersButtonsTexture, i * 50, j * 50, 50, 50);
                numberButtonsTextureRegions[i + 16][j] = new TextureRegion(numbersButtonsTexture, i * 50, 100 + j * 50, 50, 50);
                numberButtonsTextureRegions[i + 32][j] = new TextureRegion(numbersButtonsTexture, i * 50, 200 + j * 50, 50, 50);
                numberButtonsTextureRegions[i + 48][j] = new TextureRegion(numbersButtonsTexture, i * 50, 300 + j * 50, 50, 50);
            }
        }

        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 2; j++) {
                otherButtonsTextureRegions[i][j] = new TextureRegion(otherButtonsTexture, i * 50, j * 50, 50, 50);
            }
        }

        splashScreenLogoTextureRegion = new TextureRegion(splashScreenLogoTexture, 0, 0, 500, 500);
        gameLogoTextureRegion = new TextureRegion(gameLogoTexture);
        pinkColorTextureRegion = new TextureRegion(colorDotsTexture, 0, 0, 1, 1);
        greenColorTextureRegion = new TextureRegion(colorDotsTexture, 1, 0, 1, 1);


        for (int i = 0; i < 6; i++) {
            singersTextureRegion[1][i] = new TextureRegion(lucianoTexture, i * 31, 0, 31, 52);
        }
        singersTextureRegion[1][6] = new TextureRegion(lucianoTexture, 187, 0, 31, 52);


        for (int i = 0; i < 7; i++) {
            singersTextureRegion[2][i] = new TextureRegion(joseTexture, i * 31, 0, 31, 52);
        }


        for (int i = 0; i < 7; i++) {
            singersTextureRegion[3][i] = new TextureRegion(joanTexture, i * 31, 0, 31, 52);
        }

        for (int i = 0; i < 7; i++) {
            singersTextureRegion[4][i] = new TextureRegion(mariaTexture, i * 31, 0, 31, 52);
        }


        singersTextureRegion[5][0] = new TextureRegion(secretPlayerTexture, 0, 0, 72, 107);
        singersTextureRegion[5][1] = new TextureRegion(secretPlayerTexture, 72, 0, 72, 107);
        singersTextureRegion[5][2] = singersTextureRegion[5][1];
        singersTextureRegion[5][3] = new TextureRegion(secretPlayerTexture, 144, 0, 72, 107);
        singersTextureRegion[5][4] = singersTextureRegion[5][3];
        singersTextureRegion[5][5] = new TextureRegion(secretPlayerTexture, 216, 0, 72, 107);
        singersTextureRegion[5][6] = singersTextureRegion[5][0];

        gameOverScoreboardTextureRegion = new TextureRegion(freePlayGameOverScoreboardTexture);
        successScoreboardTextureRegion = new TextureRegion(successScoreboardTexture);
        starTextureRegion = new TextureRegion(starsTexture, 0, 0, 10, 10);
        noStarTextureRegion = new TextureRegion(starsTexture, 10, 0, 10, 10);

        metalTextureRegion = new TextureRegion(metalTexture);
        metalTextureRegion2 = new TextureRegion(metalTexture2);
        skyTextureRegion = new TextureRegion(skyTexture);

        for (int i = 0; i < 7; i++) {
            monitsTextureRegions[i] = new TextureRegion(endGameMonitsTexture, 0, i * 32, 301, 32);
        }
    }

    private static void createAnimations() {
        highScoreAnimation = new Animation(0.6f, highScoreTextureRegion);
        highScoreAnimation.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] alienOK = {alienTextureRegion[1], alienTextureRegion[2], alienTextureRegion[3]};
        alienAnimation[0] = new Animation(0.08f, alienOK);
        alienAnimation[0].setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        TextureRegion[] alienCOOL = {alienTextureRegion[5], alienTextureRegion[6], alienTextureRegion[7]};
        alienAnimation[1] = new Animation(0.08f, alienCOOL);
        alienAnimation[1].setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        for (int i = 1; i < 6; i++) {
            singersAnimation[i] = new Animation(0.05f, singersTextureRegion[i][3], singersTextureRegion[i][4]);
            singersAnimation[i].setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        }
    }

    private static void createParticleEffects() {
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("data/effects/shatter2.p"), Gdx.files.internal("data/effects/"));
        crushingParticleEffectPool = new ParticleEffectPool(effect, 1, 1);

        effect2 = new ParticleEffect();
        effect2.load(Gdx.files.internal("data/effects/good.p"), Gdx.files.internal("data/effects/"));
        correctPitchParticleEffectPool = new ParticleEffectPool(effect2, 1, 1);

        effect3 = new ParticleEffect();
        effect3.load(Gdx.files.internal("data/effects/shatter3.p"), Gdx.files.internal("data/effects/"));
        crushingParticleEffectPool2 = new ParticleEffectPool(effect3, 1, 1);

        effect4 = new ParticleEffect();
        effect4.load(Gdx.files.internal("data/effects/starEffect.p"), Gdx.files.internal("data/effects/"));
        starParticleEffectPool = new ParticleEffectPool(effect4, 1, 1);
    }


    private static void loadFonts() {
        numberFont = new BitmapFont(Gdx.files.internal("data/numberfont.fnt"));
        smallWhiteFont = new BitmapFont(Gdx.files.internal("data/sm.fnt"));
        smallYellowFont = new BitmapFont(Gdx.files.internal("data/sm2.fnt"));
        scoreFont = new BitmapFont(Gdx.files.internal("data/scoreFont.fnt"));
    }

    private static void loadSounds() {
        clickSound = Gdx.audio.newSound(Gdx.files.internal("data/click2.wav"));
        glassMusic = loadMusicFile("data/break.wav", false);
        themeMusic = loadMusicFile("data/theme.mp3", true);
        successMusic = loadMusicFile("data/win.mp3", false);
        gameOverMusic = loadMusicFile("data/gameover.mp3", false);

        for (int i = 1; i <= 5; i++) {
            starMusic[i] = loadMusicFile("data/star" + i + ".mp3", false);
        }
    }

    private static Music loadMusicFile(String filePath, boolean looping) {
        Music music = Gdx.audio.newMusic(Gdx.files.internal(filePath));
        music.setLooping(looping);
        return music;
    }

    private static void loadInterfaces(InternetInterface internetInterface, SensorInterface sensorInterface, AdInterface adInterface) {
        AssetManager.internetInterface = internetInterface;
        AssetManager.sensorInterface = sensorInterface;
        AssetManager.adInterface = adInterface;
    }

    public static void playMusic(Music music){
        if (soundEnabled) {
            music.play();
        }
    }

    public static void vibrate(int milliseconds){
        if (vibrationEnabled) {
            Gdx.input.vibrate(milliseconds);
        }
    }

    private static void disposeTextures() {
        freePlayGameOverScoreboardTexture.dispose();
        successScoreboardTexture.dispose();
        missionModeGameOverScoreboardTexture.dispose();
        helpTexture.dispose();
        optionsTexture.dispose();
        numbersButtonsTexture.dispose();
        otherButtonsTexture.dispose();
        menuButtonsTexture.dispose();
        metalTexture.dispose();
        metalTexture2.dispose();
        starsTexture.dispose();
        endGameMonitsTexture.dispose();
        alienTexture.dispose();
        highScoreMonitTexture.dispose();
        blackRectangleTexture.dispose();
        skyTexture.dispose();
        playerNamesTexture.dispose();
        splashScreenLogoTexture.dispose();
        gameLogoTexture.dispose();
        colorDotsTexture.dispose();
        lucianoTexture.dispose();
        joseTexture.dispose();
        joanTexture.dispose();
        mariaTexture.dispose();
        secretPlayerTexture.dispose();
        pausePanelTexture.dispose();
        challengePanelTexture.dispose();
    }

    private static void disposeSounds() {
        clickSound.dispose();
        glassMusic.dispose();
        themeMusic.dispose();
        gameOverMusic.dispose();
        successMusic.dispose();

        for (int i = 1; i < 6; i++) {
            starMusic[i].dispose();
        }
    }

}