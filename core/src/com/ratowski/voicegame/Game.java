package com.ratowski.voicegame;

import com.badlogic.gdx.ApplicationListener;
import com.ratowski.helpers.AdInterface;
import com.ratowski.helpers.AssetManager;
import com.ratowski.helpers.InternetInterface;
import com.ratowski.helpers.SensorInterface;
import com.ratowski.helpers.SoundHandler;
import com.ratowski.screens.SplashScreen;

public class Game extends com.badlogic.gdx.Game implements ApplicationListener {

	private InternetInterface internetInterface;
	private SensorInterface sensorInterface;
	private AdInterface adInterface;

	public Game(InternetInterface internetInterface, SensorInterface sensorInterface, AdInterface adInterface)
	{
		this.internetInterface = internetInterface;
		this.sensorInterface = sensorInterface;
		this.adInterface = adInterface;
	}

	@Override
	public void create () {
		AssetManager.loadAssets(internetInterface, sensorInterface, adInterface);
		SoundHandler.setupHandler();
		SoundHandler.startHandler();
		setScreen(new SplashScreen(this));
		System.out.flush();
	}

	@Override
	public void dispose() {
		super.dispose();
		AssetManager.dispose();
		SoundHandler.dispose();
	}


}
