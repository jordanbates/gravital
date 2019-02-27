package com.everless.gravital;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Jordan on 8/13/2015.
 * off screen trackers extends sprite
 */
public class OffScreenTracker extends Sprite {
    private Ship ship;
    private float screenWidth;
    private float screenHeight;
    private float width;
    private float height;
    private Direction direction;

    public OffScreenTracker(GameCore gameCore, Texture texture) {
        super(texture);
        super.setSize(100, 100);
        ship = gameCore.ship;
        screenWidth = GameCore.screenWidth;
        screenHeight = GameCore.screenHeight;
        width = super.getWidth();
        height = super.getHeight();
        setOrigin(width/2, height/2);
        direction = Direction.RIGHT;
        setPosition(5000, 5000);
    }

    public enum Direction {
        RIGHT(0),
        UP_RIGHT(45),
        UP(90),
        UP_LEFT(135),
        LEFT(180),
        DOWN_LEFT(225),
        DOWN(270),
        DOWN_RIGHT(315);

        private final int angle;
        Direction(int angle) {
            this.angle = angle;
        }

        public int getAngle() { return angle; }
        }

    protected void setSpritePosition(){
        //virtualviewport is NOT NECESSARILY the size of screen. use gdx instead ~~~!!!!
        float shipX = ship.getXinPixels();
        float shipY = ship.getYinPixels();
        ////Gdx.app.log(TAG, "ship: " + shipX + ", " + shipY);
        float x = shipX;
        float y = shipY;

        float xDif = 0.0f;
        float yDif = 0.0f;

        float distance = Math.max(Math.abs(screenWidth - x), Math.abs(screenHeight - y));
        ////Gdx.app.log("Distance", "" + distance);
        distance = (distance / 1000) + .5f;
        float alpha = distance > 1.0f ? .50f : distance;

        if(shipX > screenWidth - width) {
            x = screenWidth - width;
            xDif = Math.abs(shipX - screenWidth - width);
            if(shipY > screenHeight - height) {
                y = screenHeight - height;
                direction = Direction.UP_RIGHT;
            }
            else if(shipY < 0) {
                y = 0;
                direction = Direction.DOWN_RIGHT;
            }
            else {
                y = shipY;
                direction = Direction.RIGHT;
            }
        }
        else if(shipX < 0) {
            x = 0;
            if(shipY > screenHeight - height) {
                y = screenHeight - height;
                direction = Direction.UP_LEFT;
            }
            else if(shipY < 0) {
                y = 0;
                direction = Direction.DOWN_LEFT;
            }
            else {
                y = shipY;
                direction = Direction.LEFT;
            }
        }
        else if(shipY > screenHeight - height) {
            y = screenHeight - height;
            x = shipX;
            direction = Direction.UP;
        }
        else if(shipY < 0) {
            y = 0;
            x = shipX;
            direction = Direction.DOWN;
        }
        super.setPosition(x, y);
        ////Gdx.app.log("SetPosition", "x, y: " + x + ", " + y);
    }

    protected void setSpriteRotation() {
        setRotation(direction.getAngle());
    }

    public void draw() {
        //draw(GameCore.batch, alpha);
        draw(GameCore.batch);
    }
}
