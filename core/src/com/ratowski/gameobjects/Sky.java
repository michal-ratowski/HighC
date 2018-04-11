package com.ratowski.gameobjects;

public class Sky extends Scrollable {

    public Sky(float x, float y, int width, int height, float scrollSpeed) {
        super(x, y, width, height, scrollSpeed);
        //System.out.println("Sky created! "+scrollSpeed+", "+x+", "+y);
    }

    public void onRestart(float x, float scrollSpeed) {
        position.x = x;
        velocity.x = scrollSpeed;
    }

}