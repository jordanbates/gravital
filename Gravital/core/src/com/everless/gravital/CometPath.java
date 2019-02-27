package com.everless.gravital;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Jordan on 6/8/2016.
 * Simple structure to hold and compare comet path information
 */
public class CometPath implements Comparable<CometPath>{
    protected float radius = 0;
    protected Array<Comet> comets = new Array<>();

    public CometPath(float radius, Comet comet){
        this.radius = radius;
        comets.add(comet);
    }

    public CometPath(float radius){
        this.radius = radius;
    }

    protected void addComet(Comet comet) {
        comets.add(comet);
        comet.cometPath = this;
    }

    protected void removeComet(Comet comet) {
        comets.removeValue(comet, true);
    }

    @Override
    public int compareTo(CometPath cometPath)
    {
        return Float.compare(radius, cometPath.radius);
        //return radius.compareTo(cometPath.radius);
    }
}
