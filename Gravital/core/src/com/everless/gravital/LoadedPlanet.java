package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jordan on 8/10/2015.
 * Planet object created by JSON file
 */
public class LoadedPlanet {

    //public String image;
    public float x;
    public float y;
    public float diameter;
    public String color = "red";
    public Array<LoadedComet> comets;

    public LoadedPlanet() {
        //Gdx.app.log("LoadedPlanet", "Class LoadedPlanet has been initialized");
    }
}
