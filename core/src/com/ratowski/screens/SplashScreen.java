package com.ratowski.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ratowski.accessors.SpriteAccessor;
import com.ratowski.helpers.AssetManager;
import com.ratowski.voicegame.Game;

public class SplashScreen implements Screen {

    private TweenManager tweenManager;
    private SpriteBatch spriteBatch;
    private Sprite logoSprite;
    private Game game;

    public SplashScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        logoSprite = new Sprite(AssetManager.splashScreenLogoTexture);
        logoSprite.setColor(1, 1, 1, 0);

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float desiredWidth = width * 0.8f;
        float scale = desiredWidth / logoSprite.getWidth();

        logoSprite.setSize(logoSprite.getWidth() * scale, logoSprite.getHeight() * scale);
        logoSprite.setPosition((width / 2) - (logoSprite.getWidth() / 2), (height / 2) - (logoSprite.getHeight() / 2));
        setupLogoTween();
        spriteBatch = new SpriteBatch();
    }

    private void setupLogoTween() {
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        tweenManager = new TweenManager();
        TweenCallback tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                game.setScreen(new GameScreen());
            }
        };
        Tween.to(logoSprite, SpriteAccessor.ALPHA, 1f).target(1).delay(0.5f).ease(TweenEquations.easeInOutQuad).repeatYoyo(1, .9f).setCallback(tweenCallback).setCallbackTriggers(TweenCallback.COMPLETE).start(tweenManager);
    }

    @Override
    public void render(float delta) {
        tweenManager.update(delta);
        Gdx.gl.glClearColor(0,0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        logoSprite.draw(spriteBatch);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}