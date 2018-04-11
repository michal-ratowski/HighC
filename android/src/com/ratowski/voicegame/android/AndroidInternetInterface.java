package com.ratowski.voicegame.android;

import android.content.Intent;
import android.net.Uri;

import com.ratowski.helpers.InternetInterface;

public class AndroidInternetInterface implements InternetInterface {

    AndroidLauncher launcher;

    public AndroidInternetInterface(AndroidLauncher launcher)
    {
        this.launcher=launcher;
    }

    @Override
    public void goToFacebook() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/1030683970323064"));
            launcher.startActivity(intent);
        } catch(Exception e) {
            launcher.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/highcgame")));
        }
    }

    @Override
    public void shareOnFacebook() {
        launcher.shareOnFB();
    }


    @Override
    public void rateOnGooglePlay() {
        String str ="https://play.google.com/store/apps/details?id=com.tomekfasola.highc";
        launcher.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
    }
}
