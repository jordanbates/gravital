package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Jordan on 8/25/2016.
 * Provides logic and drawing for timer
 */
public class Timer extends Sprite {
    protected GameCore gameCore;
    protected final float TIME = 10.0f;
    protected float minimumTime = 0;
    protected float min = 10;
    protected float time = TIME;
    protected ShapeRenderer shapeRenderer;
    protected float screenWidth, screenHeight, screenWidthAdjusted, screenHeightAdjusted;
    protected float colorHeight;

    public Timer(GameCore gameCore) {
        super(Main.getInstance().assetManager.get("timer.png", Texture.class));
        this.gameCore = gameCore;
        shapeRenderer = gameCore.shapeRenderer;
        screenWidth = GameCore.screenWidth;
        screenHeight = GameCore.screenHeight;
        screenWidthAdjusted = GameCore.screenWidthAdjusted;
        screenHeightAdjusted = GameCore.screenHeightAdjusted;
        float width = GameCore.screenWidth * .9f;
        float height = (width * getHeight()) / getWidth();
        super.setSize(width, height);
        super.setPosition((screenWidth - width) / 2, screenHeight - (height * 1.75f));
        colorHeight = Utility.projectY(height * .93f);
    }

    public void draw(float delta) {
        updateTimer(delta);
        GameCore.batch.begin();
        super.draw(GameCore.batch);
        GameCore.batch.end();
    }

    private void updateTimer(float delta) {
        //if((gameCore.isGameStateRunOrCreateRun()) && gameCore.shipInMotion) {
        if(gameCore.isGameStateRunOrCreateRun()) {
            //if(GameCore.shipExists && !GameCore.shipInPortal) {
            if(GameCore.shipExists && !GameCore.shipInPortal && gameCore.shipInMotion) {
                time -= delta;
            }
            if(time <= 0) {
                if(gameCore.endAction == null) {
                    gameCore.endAction = EnumEndAction.RESTART;
                    gameCore.fadeOut();
                    /*Toast toast = new Toast("Ship ran out of time", gameCore.HUDStage);
                    toast.setPositionBottom();
                    toast.startToast();*/
                    Main.getInstance().delayedToasts.add(new Toast("Ship ran out of oxygen", gameCore.HUDStage, Toast.TOAST_SHORT, Toast.TOAST_SMALL));
                }
            }
            else {
                float scale = time / TIME;
                //float min = Math.max(0, ((time - min) / (TIME - min)));
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                //shapeRenderer.setColor(120f/255f, 22f/255f, 26f/255f, 0); //dark red

                /*Vector2 sizeVec = Utility.project(getWidth(), getHeight());
                float width = sizeVec.x;
                float height = sizeVec.y * .93f;*/
                //float timerWidth = screenWidthAdjusted * .8f; //previously worked
                float timerWidth = screenWidthAdjusted * .86f; //.855f
                float x = (screenWidthAdjusted - timerWidth) / 2;
                float y = screenHeightAdjusted - colorHeight * 1.85f;// * 0.75f;
                //posX = 55;
                //height -= 5;

                //Draw timer
                //shapeRenderer.setColor(164f/255f, 16f/255f, 38f/255f, 0); //med red
                //shapeRenderer.setColor(66f/255f, 111f/255f, 182f/255f, 0); //light blue
                Vector3 colorOffset = new Vector3(98, -95, -144);
                float newScale;
                if (scale > .5f) {
                    newScale = 1;
                }
                else if (scale >= .25) {
                    newScale = (scale - .25f) * 4;
                }
                else {
                    newScale = 0;
                }
                colorOffset = colorOffset.scl(1 - newScale);
                shapeRenderer.setColor((66f + colorOffset.x)/255f, (111f + colorOffset.y)/255f, (182f + colorOffset.z)/255f, 0); //light blue -> red
                shapeRenderer.rect(x, y, timerWidth * scale, colorHeight);

                shapeRenderer.end();
            }
        }
    }

    protected boolean pastMinTime() {
        return time < min;
    }

    //0 to 1: how much of min time is finished
    protected float getMinimumTimeScale() {
        float timePassed = TIME - time;
        return timePassed / minimumTime;
    }

    protected void setMinimumTime(int time) {
        minimumTime = time;
        min = TIME - minimumTime;
    }
}
