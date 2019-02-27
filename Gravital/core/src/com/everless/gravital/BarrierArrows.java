package com.everless.gravital;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Jordan on 6/15/2016.
 * These surround the ship launch site and dictate in which directions the ship may be launched
 */
public class BarrierArrows {
    private Sprite leftArrow;
    private Sprite rightArrow;
    private Arrow arrow;
    private boolean on = false;
    protected boolean touch = false;
    private boolean leftTouch = false, rightTouch = false;
    public Main main;

    //Notes:    create this class after creating arrow class
    //          draw this before ship

    public BarrierArrows(GameCore gameCore, LoadedLevel loadedLevel) {
        main = Main.getInstance();
        this.arrow = gameCore.arrow;
        leftArrow = new Sprite(main.assetManager.get("barrier_arrow.png", Texture.class));
        rightArrow = new Sprite(main.assetManager.get("barrier_arrow.png", Texture.class));

        leftArrow.setSize(70, Utility.MODEL_HALF_SCREEN_WIDTH - 20);
        rightArrow.setSize(70, Utility.MODEL_HALF_SCREEN_WIDTH - 20);
        leftArrow.setOrigin(leftArrow.getWidth() / 2, 0);
        rightArrow.setOrigin(rightArrow.getWidth() / 2, 0);
        leftArrow.setPosition(GameCore.screenWidth/2 - (leftArrow.getWidth() / 2), Utility.trueShipSpriteHeight / 2);
        rightArrow.setPosition(GameCore.screenWidth/2 - (rightArrow.getWidth() / 2), Utility.trueShipSpriteHeight / 2);
        leftArrow.setRotation((float) Math.toDegrees(loadedLevel.leftBarrier + Math.PI/2));
        rightArrow.setRotation((float) Math.toDegrees(loadedLevel.rightBarrier + Math.PI/2));
        on = loadedLevel.barrierArrows;
    }

    public boolean getBarrierArrowOn() {
        return on;
    }

    //make sure get y and x are in pixels
    public void setRotation(float x, float y) {
        if (leftTouch) {
            float radians = (float) Math.atan2((leftArrow.getY() - y), (leftArrow.getX() - x + leftArrow.getWidth() / 2));
            radians += ((Math.PI) / 2.0f);
            radians = Utility.normalizeAngle(radians);
            float degrees = (float) Math.toDegrees(radians);
            if (degrees < 0) {
                if (degrees > -90) {
                    degrees = 0;
                }
                else {
                    degrees = 95;
                }
            }
            else if (degrees > 95) {
                degrees = 95;
            }
            leftArrow.setRotation(degrees);
            radians = (float) Math.toRadians(degrees);
            radians -= ((Math.PI) / 2.0f);
            arrow.leftBarrier = radians;
        }
        else if (rightTouch) {
            float radians = (float) Math.atan2((rightArrow.getY() - y), (rightArrow.getX() - x + rightArrow.getWidth() / 2));
            radians += ((Math.PI) / 2.0f);
            radians = Utility.normalizeAngle(radians);
            float degrees = (float) Math.toDegrees(radians);
            if (degrees > 0) {
                if (degrees < 90) {
                    degrees = 0;
                }
                else {
                    degrees = -95;
                }
            }
            else if (degrees < -95) {
                degrees = -95;
            }
            rightArrow.setRotation(degrees);
            radians = (float) Math.toRadians(degrees);
            radians -= ((Math.PI) / 2.0f);
            arrow.rightBarrier = radians;
        }
    }

    //create: true if in create mode
    protected void draw(boolean create) {
        if (on) {
            rightArrow.setAlpha(1);
            leftArrow.setAlpha(1);

            leftArrow.draw(GameCore.batch);
            rightArrow.draw(GameCore.batch);
        }
        else if (create) {
            //TODO: this more efficiently
            rightArrow.setAlpha(0.3f);
            leftArrow.setAlpha(0.3f);

            leftArrow.draw(GameCore.batch);
            rightArrow.draw(GameCore.batch);
        }
    }

    protected boolean touchDown(float x, float y) {
        if ((!rightTouch) && (leftTouch || leftArrow.getBoundingRectangle().contains(x, y))) {
            touch = true;
            leftTouch = true;
            setRotation(x, y);
        }
        else if (rightTouch || rightArrow.getBoundingRectangle().contains(x, y)) {
            touch = true;
            rightTouch = true;
            setRotation(x, y);
        }
        return touch;
    }

    protected boolean touchUp() {
        boolean result = touch;
        touch = false;
        leftTouch = false;
        rightTouch = false;
        return result;
    }

    protected boolean tap(float x, float y) {
        boolean tapped = false;
        if (leftArrow.getBoundingRectangle().contains(x, y) || rightArrow.getBoundingRectangle().contains(x, y)) {
            tapped = true;
            toggle();
        }
        return tapped;
    }

    protected void toggle() {
        on = !on;
    }
}
