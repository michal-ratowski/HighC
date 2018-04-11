package com.ratowski.voicegame.android;

import android.content.Context;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ratowski.helpers.AdInterface;
import com.ratowski.voicegame.Game;
import com.tomekfasola.highc.R;

import com.facebook.FacebookSdk;


public class AndroidLauncher extends AndroidApplication implements SensorEventListener, AdInterface {

	private GestureLibrary gLibrary;
	private AndroidInternetInterface androidInternetInterface;
	private AndroidSensorInterface androidSensorInterface;
	CallbackManager callbackManager;
	ShareDialog shareDialog;
	private SensorManager senSensorManager;
	private Sensor senAccelerometer;
	public float x,y,z,last_x,last_y,last_z,speed;
	InterstitialAd interstitialAd;
	String full_ad_id = "ca-app-pub-8038466836438559/9526413629";
	private long lastUpdate = 0;

	@Override
	protected void onCreate (Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		FacebookSdk.sdkInitialize(getApplicationContext());
		androidInternetInterface=new AndroidInternetInterface(this);
		androidSensorInterface=new AndroidSensorInterface(this);
		callbackManager = CallbackManager.Factory.create();
		shareDialog = new ShareDialog(this);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();

		gLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!gLibrary.load()) {finish();}

		View gameView = initializeForView(new Game(androidInternetInterface,androidSensorInterface, this), cfg);
		setupAds();

		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		setContentView(layout);

	}

	public void shareOnFB(){
		if (ShareDialog.canShow(ShareLinkContent.class)) {
			ShareLinkContent linkContent = new ShareLinkContent.Builder()
					.setContentTitle("High C")
					.setContentDescription("HIGH C AWESOME GAME")
					.setImageUrl(Uri.parse("https://lh3.googleusercontent.com/RXj1mP2hU1A1l0ArMbmwnnwvocTcuyF7nDBlyv5OQH7KivUgxeR0ZxBYexe2adazv4sT=w300-rw"))
					.build();
			shareDialog.show(linkContent);
		}
	}

	// ACCELEROMETER

	public float[] getAccValues() {
		float[] values = new float[4];
		values[0]=x;
		values[1]=y;
		values[2]=z;
		values[3]=speed;
		return values;
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {

		Sensor mySensor = sensorEvent.sensor;
		if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			x = sensorEvent.values[0];
			y = sensorEvent.values[1];
			z = sensorEvent.values[2];

			long curTime = System.currentTimeMillis();

			if ((curTime - lastUpdate) > 100) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;

				speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

				last_x = x;
				last_y = y;
				last_z = z;
			}

		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
	}

	protected void onPause() {
		super.onPause();
		senSensorManager.unregisterListener(this);
	}

	protected void onResume() {
		super.onResume();
		senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void setupAds() {
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(full_ad_id);
		AdRequest.Builder builder = new AdRequest.Builder();
		AdRequest ad = builder.build();
		interstitialAd.loadAd(ad);
	}

	@Override
	public void reloadAd(){
		runOnUiThread(new Runnable() {
			public void run() {
				AdRequest.Builder builder = new AdRequest.Builder();
				AdRequest ad = builder.build();
				interstitialAd.loadAd(ad);
			}
		});
	}

	@Override
	public void showInterstitialAd(final Runnable then) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (then != null) {
					interstitialAd.setAdListener(new AdListener() {
						@Override
						public void onAdClosed() {
							Gdx.app.postRunnable(then);
							AdRequest.Builder builder = new AdRequest.Builder();
							AdRequest ad = builder.build();
							interstitialAd.loadAd(ad);
						}
					});
				}
				interstitialAd.show();
			}
		});
	}

	@Override
	public boolean isWifiConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo ni2 = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return ((ni != null && ni.isConnected()) || (ni2 != null && ni2.isConnected()));
	}
}
