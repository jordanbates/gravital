package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jordan on 8/10/2015.
 * LoadedLevel object created by JSON file
 */
public class LoadedLevel {

    public int level;
    public String levelName;
    public float portalx;
    public float portaly;
    public int portalMinTime = 0;
    public float leftBarrier = 0;//(float) ((Math.PI) / 2.0f); //
    public float rightBarrier = (float) (Math.PI); //(float) -((Math.PI) / 2.0f);
    public boolean barrierArrows = false;
    public Array<LoadedPlanet> planets;
    public Array<LoadedWormhole> wormholes;

    public LoadedLevel() {
        //Gdx.app.log("LoadedLevel", "Class LoadedLevel has been initialized");
    }

}