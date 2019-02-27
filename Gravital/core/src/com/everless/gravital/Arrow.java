package com.everless.gravital;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;

/**
 * Created by Jordan on 8/2/2015.
 * Arrow class
 */
public class Arrow extends Sprite {
    private GameCore gameCore;
    private Ship ship;
    private Texture texture;
    private float currentHeight;
    private boolean touch = false;
    protected float leftBarrier = 0, rightBarrier = (float) (Math.PI);

    public Arrow(GameCore gameCore, Texture texture, LoadedLevel loadedLevel) {
        super(texture);
        this.gameCore = gameCore;
        this.texture = texture;
        ship = gameCore.ship;
        currentHeight = getHeight();
        setOrigin(getWidth() / 2, 0);
        setSize(getWidth(), 450);
        setPosition(GameCore.screenWidth/2 - (getWidth() / 2), Utility.trueShipSpriteHeight / 2);
        leftBarrier = loadedLevel.leftBarrier;
        rightBarrier = loadedLevel.rightBarrier;
    }

    //make sure get y and x are in pixels
    public float setRotation(float x, float y) {
        float radians = (float) Math.atan2((getY() - y), (getX() - x + getWidth()/2));
        radians += ((Math.PI) / 2.0f);
        radians = Utility.normalizeAngle(radians);

        leftBarrier += Math.PI/2;
        leftBarrier = Utility.normalizeAngle(leftBarrier);
        rightBarrier += Math.PI/2;
        rightBarrier = Utility.normalizeAngle(rightBarrier);

        if (radians > 0) { //On left side
            if (radians > leftBarrier) {
                radians = leftBarrier;
            }
        } //Else on right side
        else if (radians < rightBarrier) {
            radians = rightBarrier;
        }

        leftBarrier -= Math.PI/2;
        rightBarrier -= Math.PI/2;

        setRotation((float) Math.toDegrees(radians));
        return radians;
    }

    protected void setCurrentHeight(float distanceFromCenter) {
        float distance = distanceFromCenter;
        if (distance > getHeight()) {
            distance = getHeight();
        }
        currentHeight = distance;
    }

    protected void draw() {
        if(touch) {
            GameCore.batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), currentHeight, getScaleX(), getScaleY(),
                    getRotation(), getRegionX(), getRegionY(),
                    getRegionWidth(), (int) currentHeight, false, false);
        }
    }

    /**
     * Check to see if touchDown is within body of ship or already activated
     * If so, adjust arrow height and rotation
     */
    protected boolean touchDown(float x, float y) {
        float distanceFromShipCenter = getDistanceFromShipCenter(x, y);
        float pad = 50.0f;
        float shipX = ship.getXinPixels() - Utility.trueShipSpriteWidth - pad;
        float shipY = ship.getYinPixels() - Utility.trueShipSpriteHeight - pad;
        float shipX2 = shipX + ((Utility.trueShipSpriteWidth + pad) * 2);
        float shipY2 = shipY + ((Utility.trueShipSpriteHeight + pad) * 2);

        //Check if touchDown is within ship or already activated
        if (touch || (x > shipX && x < shipX2 && y > shipY && y < shipY2)) {
            touch = true;
            float radians = setRotation(x, y);
            setCurrentHeight(distanceFromShipCenter);
            ship.setInitialSpriteRotation(radians);
        }
        return touch;
    }

    /**
     * Coordinates from GameCore panStop
     * @return true if ship has been launched
     */
    protected boolean touchUp(float x, float y) {
        boolean result = false;
        if (touch) {
            float distanceFromShipCenter = getDistanceFromShipCenter(x, y);
            if (distanceFromShipCenter > Utility.trueShipSpriteHeight* 0.8f) {
                float distance = distanceFromShipCenter;
                if (distance > getHeight()) {
                    distance = getHeight();
                }
                float radians = setRotation(x, y);
                radians += Math.PI/2;
                ship.applyLinearImpulse(radians, distance, getHeight());
                gameCore.launchShip();
                result = true;
            }
            else {
                ship.setInitialSpriteRotation(GameCore.halfScreenWidth, GameCore.screenHeight);
            }
            touch = false;
        }
        return result;
    }

    protected float getDistanceFromShipCenter(float x, float y) {
        float shipX = ship.getXinPixels();
        float shipY = ship.getYinPixels();
        return Utility.distance(shipX, shipY, x, y);
    }
}