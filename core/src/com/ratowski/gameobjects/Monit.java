package com.ratowski.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ratowski.accessors.Value;
import com.ratowski.accessors.ValueAccessor;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class Monit {

    float x,y;
    int width,height;
    Value alpha = new Value();
    Value scale = new Value();
    private TweenManager manager;
    TextureRegion monit;
    public boolean finished=false;

    public Monit(float x, float y, int width, int height, TextureRegion monit, float growth, float time, float delay) {

        this.x=x;
        this.y=y;
        this.height=height;
        this.width=width;
        this.monit=monit;

        alpha.setValue(1);
        scale.setValue(0);
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        Tween.to(alpha, -1, time).target(0).delay(delay).ease(TweenEquations.easeOutQuad).start(manager);
        Tween.to(scale, -1, time).target(growth).delay(delay).ease(TweenEquations.easeOutQuad).start(manager);

    }


    public void draw(float delta, SpriteBatch batcher) {
        if (alpha.getValue() > 0) {
            manager.update(delta);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            Color c = batcher.getColor();
            batcher.setColor(c.r, c.g, c.b, alpha.getValue());
            batcher.draw(monit, x - scale.getValue() * width / 2, y - scale.getValue() * height / 2, width * (scale.getValue() + 1), height * (scale.getValue() + 1));
            batcher.setColor(c.r,c.g,c.b,1);
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
        else finished=true;
    }

}
