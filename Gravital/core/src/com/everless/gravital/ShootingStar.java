package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Jordan on 8/29/2015.
 * Shooting Star animation
 */
public class ShootingStar {
    private Animation shootingStarAnimation;
    private TextureAtlas shootingStarAtlas;
    public float meterDiameter;
    public float meterRadius;
    public float pixelDiameter;
    public float pixelRadius;
    public float timePassed = 0.0f;
    public float x = 500.0f;
    public float y = 500.0f;
    public float radius = 300.0f;
    public float degrees = 0.0f;
    public boolean active = false;

    public ShootingStar() {
        GameCore.shootingStarActive = true;

        //shootingStarAtlas = new TextureAtlas("shooting_star/shooting_star.atlas");
        shootingStarAtlas = Main.getInstance().assetManager.get("shooting_star/shooting_star.atlas", TextureAtlas.class);
        TextureRegion[] anim = new TextureRegion[24];

        anim[0] = (shootingStarAtlas.findRegion("1"));
        anim[1] = (shootingStarAtlas.findRegion("2"));
        anim[2] = (shootingStarAtlas.findRegion("3"));
        anim[3] = (shootingStarAtlas.findRegion("4"));
        anim[4] = (shootingStarAtlas.findRegion("5"));
        anim[5] = (shootingStarAtlas.findRegion("6"));
        anim[6] = (shootingStarAtlas.findRegion("7"));
        anim[7] = (shootingStarAtlas.findRegion("8"));
        anim[8] = (shootingStarAtlas.findRegion("9"));
        anim[9] = (shootingStarAtlas.findRegion("10"));
        anim[10] = (shootingStarAtlas.findRegion("11"));
        anim[11] = (shootingStarAtlas.findRegion("12"));
        anim[12] = (shootingStarAtlas.findRegion("13"));
        anim[13] = (shootingStarAtlas.findRegion("14"));
        anim[14] = (shootingStarAtlas.findRegion("15"));
        anim[15] = (shootingStarAtlas.findRegion("16"));
        anim[16] = (shootingStarAtlas.findRegion("17"));
        anim[17] = (shootingStarAtlas.findRegion("18"));
        anim[18] = (shootingStarAtlas.findRegion("19"));
        anim[19] = (shootingStarAtlas.findRegion("20"));
        anim[20] = (shootingStarAtlas.findRegion("21"));
        anim[21] = (shootingStarAtlas.findRegion("22"));
        anim[22] = (shootingStarAtlas.findRegion("23"));
        anim[23] = (shootingStarAtlas.findRegion("24"));

        //shootingStarAtlas = new TextureAtlas("shooting_star/shooting_star.atlas");
        //shootingStarAnimation = new Animation(1/6f, shootingStarAtlas.getRegions());

        shootingStarAnimation = new Animation(0.05f, anim);

        meterDiameter = Utility.pixelsToMeters(shootingStarAtlas.findRegion("1").originalWidth);
        meterRadius = meterDiameter / 2;
        pixelDiameter = Utility.metersToPixels(meterDiameter);
        pixelRadius = Utility.metersToPixels(meterRadius);
    }

    public void draw() {
        if(!shootingStarAnimation.isAnimationFinished(timePassed)) {
            timePassed += Gdx.graphics.getDeltaTime();
            //batch.draw(yourtexture, 0, 0, cam.viewportWidth, cam.viewportHeight);
            //GameCore.batch.draw(shootingStarAnimation.getKeyFrame(timePassed, false), x, y);
            GameCore.batch.draw(shootingStarAnimation.getKeyFrame(timePassed, false),
                    x, y, 0, 0, radius, radius,
                    1, 1, degrees);
        }
        else {
            timePassed = 0.0f;
            active = false;
        }
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
