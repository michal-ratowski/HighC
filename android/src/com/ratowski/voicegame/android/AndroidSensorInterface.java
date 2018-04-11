package com.ratowski.voicegame.android;


import com.ratowski.helpers.SensorInterface;

public class AndroidSensorInterface implements SensorInterface {

    AndroidLauncher launcher;
    public AndroidSensorInterface(AndroidLauncher launcher)
    {
        this.launcher=launcher;
    }

    @Override
    public float[] getAccelerometerValues() {
        return launcher.getAccValues();
    }
}
