package com.everless.gravital;

/**
 * Created by Jordan on 10/31/2016.
 * Interface for dealing with ads
 */
public interface AdInterface {
    void showImageInterstitial();
    void loadImageInterstitial();
    void showVideoInterstitial();
    void loadVideoInterstitial();
    void loadOrShowVideoInterstitial();
}
