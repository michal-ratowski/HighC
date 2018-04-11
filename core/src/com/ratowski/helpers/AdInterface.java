package com.ratowski.helpers;

public interface AdInterface {
    void reloadAd();
    boolean isWifiConnected();
    void showInterstitialAd(Runnable then);
}
