package com.ratowski.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.ratowski.voicegame.GameRenderer;
import com.ratowski.voicegame.GameWorld;
import com.ratowski.helpers.InputHandler;

public class GameScreen implements Screen {

    private GameWorld gameWorld;
    private GameRenderer gameRenderer;
    private float runTime = 0;

    public GameScreen() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float gameWidth = 272;
        float gameHeight = screenHeight / (screenWidth / gameWidth);
        int midPointY = (int) (gameHeight / 2);

        gameWorld = new GameWorld(midPointY, gameHeight);
        Gdx.input.setInputProcessor(new InputHandler(gameWorld, screenWidth / gameWidth, screenHeight / gameHeight, gameHeight));
        Gdx.input.setCatchBackKey(true);
        gameRenderer = new GameRenderer(gameWorld, (int) gameHeight, midPointY);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        runTime += delta;
        gameWorld.updateWorld(delta);
        gameRenderer.render(delta, runTime);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}