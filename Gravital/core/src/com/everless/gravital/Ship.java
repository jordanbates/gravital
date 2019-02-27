package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jordan on 7/28/2015.
 * Ship class
 */
public class Ship extends SpaceObject {
    private ParticleEffectWrapper particleEffectWrapper, particleEffectWrapper2;

    float virtualScreenWidth;
    float virtualScreenHeight;
    protected float xOffset = 0, yOffSet = 0;
    protected final float SHIP_PARTICLE_OFFSET = 0.2f;
    protected final float PARTICLE_EFFECT_SCALE = 1;
    protected float shipParticleOffset = SHIP_PARTICLE_OFFSET;
    protected float particleEffectScale = PARTICLE_EFFECT_SCALE;
    float height = 180;
    float width = 123.3f;

    public Ship(GameCore gameCore, Texture texture){
        super(gameCore, texture);
        //super(gameCore, texture, 123.3f, 180); //164
        //Gdx.app.log("_ _ _ height: " + height, "getHeight: " + sprite.getHeight());
        createShipBodyWrapper(0, (sprite.getHeight() / 2));
        particleEffectWrapper = new ParticleEffectWrapper(Gdx.files.internal("particle/rocket_particle_effect"), Gdx.files.internal("particle"), "fire");
        particleEffectWrapper2 = new ParticleEffectWrapper(Gdx.files.internal("particle/rocket_particle_effect"), Gdx.files.internal("particle"), "smoke");
        /*virtualScreenWidth = GameCore.virtualViewport.getVirtualWidth();
        virtualScreenHeight = GameCore.virtualViewport.getVirtualHeight();*/
    }

    protected void setSpritePosition() {
        //super.setSpritePosition();
        sprite.setPosition(Utility.metersToPixels(bodyWrapper.body.getPosition().x) - Utility.trueShipSpriteWidth/2,
                Utility.metersToPixels(bodyWrapper.body.getPosition().y) - Utility.trueShipSpriteHeight/2);

        float pX1 = Utility.getShipBottomMiddleX(this);
        //float pX2 = Utility.getShipTopMiddleX(this);
        //float pXDiff = pX2 - pX1;
        float pXDiff = Utility.getShipXDiff(this);
        float pX = pX1 + (pXDiff * shipParticleOffset);

        float pY1 = Utility.getShipBottomMiddleY(this);
        float pY2 = Utility.getShipTopMiddleY(this);
        float pYDiff = pY2 - pY1;
        float pY = pY1 + (pYDiff * shipParticleOffset);

        particleEffectWrapper.setPosition(pX, pY);
        particleEffectWrapper.setRotation(bodyWrapper.body.getAngle());
        particleEffectWrapper.scale(particleEffectScale);
        particleEffectWrapper2.setPosition(pX, pY);
        particleEffectWrapper2.setRotation(bodyWrapper.body.getAngle());
        particleEffectWrapper2.scale(particleEffectScale);
    }

    //Overriden to draw sprite dimensions based on portal changes, uses GameCore.shipSprite...
    protected void drawSprite(float delta) {
        GameCore.batch.draw(sprite, sprite.getX() + xOffset, sprite.getY() + yOffSet, sprite.getOriginX(), sprite.getOriginY(),
                GameCore.shipSpriteWidth, GameCore.shipSpriteHeight, sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation());
        particleEffectWrapper.draw(delta);
        particleEffectWrapper2.draw(delta);
    }

    /**
     * Ship sets rotation to face direction of travel
     * Not sure why - pi/2 because libgdx orients landscape?
     */
    /*protected void setSpriteRotation() {
        float radians = (float) Math.atan2(bodyWrapper.body.getLinearVelocity().y, bodyWrapper.body.getLinearVelocity().x);
        bodyWrapper.body.setTransform(getWorldCenter(), radians - ((float) Math.PI) / 2.0f);// - ((float)Math.PI)/2.0f
        super.setSpriteRotation();
    }*/

    /**
     * Sets initial rotation to face user click
     * I have no clue why I need to add pi/2 (I think bc libgdx is inherently landscape)
     */
    protected void setInitialSpriteRotation(float x, float y) {
        float radians = (float) Math.atan2((getYinPixels() - y), (getXinPixels() - x));
        bodyWrapper.body.setTransform(bodyWrapper.body.getPosition(), radians + (((float) Math.PI) / 2.0f));
        super.setSpriteRotation();
    }

    protected void setInitialSpriteRotation(float radians) {
        bodyWrapper.body.setTransform(bodyWrapper.body.getPosition(), radians);
        super.setSpriteRotation();
    }

    /**
     * @param x From world center of release
     * @param y From world center of release
     * @param distance The capped distance of release from shipCenter, to determine force of impulse
     *                 Min = 0, Max = arrow height
     */
    protected void applyLinearImpulse(float x, float y, float distance, float maxDistance) {
        //Gdx.app.log("Ship.applyLinearImpulse()", "ship mass: " + bodyWrapper.body.getMass());

        float deltaX = x - getXinPixels();
        float deltaY = y - getYinPixels();

        float scale = 0.8f; //this 0.8 works with rectangular ship body
        //scale = 0.0005f; //TEMP
        float force = scale * (distance/maxDistance);

        float angle = (float) Math.atan2(deltaY, deltaX);
        bodyWrapper.body.applyLinearImpulse(new Vector2((float) Math.cos(angle) * force,
                (float) Math.sin(angle) * force), getWorldCenter(), true);
        particleEffectWrapper.start();
        particleEffectWrapper2.start();
    }

    protected void applyLinearImpulse(float angle, float distance, float maxDistance) {
        //Gdx.app.log("Ship.applyLinearImpulse()", "ship mass: " + bodyWrapper.body.getMass());

        float scale = 0.8f; //this 0.8 works with rectangular ship body
        //scale = 0.0005f; //TEMP
        float force = scale * (distance/maxDistance);

        bodyWrapper.body.applyLinearImpulse(new Vector2((float) Math.cos(angle) * force,
                (float) Math.sin(angle) * force), getWorldCenter(), true);
        particleEffectWrapper.start();
        particleEffectWrapper2.start();
    }
}
