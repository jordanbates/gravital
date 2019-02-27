package com.everless.gravital;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Jordan on 7/30/2015.
 * Planet class
 */
public class Planet extends SpaceObject {
    private Sprite ring;

    public Planet(GameCore gameCore, Texture texture, Texture ringTexture, float x, float y, float radius) {
        super(gameCore, texture, radius, radius);
        //super(texture, radius * 2, radius * 2); //changed from just radius. hopefully works? fix
        createPlanetBodyWrapper(x, y);
        setValidateRadius();
        ring = new Sprite(ringTexture);
        //float randRotation = (Utility.randInt(0, 10) * Utility.randInt(1, 6)) * 0.1f;
        //bodyWrapper.body.setTransform(getWorldCenter(), randRotation);
    }

    protected void drawRingSprite(float distanceFromPlanet) {
        float diameter = distanceFromPlanet * 2;
        float radiusDiff = (sprite.getWidth() - diameter) / 2;
        float x = sprite.getX() + radiusDiff;
        float y = sprite.getY() + radiusDiff;
        GameCore.batch.draw(ring, x, y, sprite.getOriginX(), sprite.getOriginY(),
                diameter, diameter, sprite.getScaleX(), sprite.getScaleY(), 0);
    }
}
