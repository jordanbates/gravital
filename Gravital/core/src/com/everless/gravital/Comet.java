package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Jordan on 8/9/2015.
 * Comets
 */
public class Comet extends SpaceObject {
    private ParticleEffectWrapper particleEffectWrapper;
    private float updates = 0;
    protected CometPath cometPath;
    protected CometManager cometManager;
    protected boolean rotateClockwise = true;

    public Comet(GameCore gameCore, Texture texture, float x, float y, float diameter, SpaceObject parentPlanet, CometPath cometPath, CometManager cometManager, boolean rotateClockwise){
        super(gameCore, texture, diameter, diameter, parentPlanet);
        //Gdx.app.log("Comet", "Comet constructor");
        createCometBodyWrapper(x, y);
        startingX = x;
        startingY = y;
        setDistanceFromSpaceObject();
        setValidateRadius();
        particleEffectWrapper = new ParticleEffectWrapper(Gdx.files.internal("particle/comet_particle_effect"), Gdx.files.internal("particle"), "comet");
        this.cometPath = cometPath;
        this.cometManager = cometManager;
        this.rotateClockwise = rotateClockwise;
    }

    public void startComet() {
        particleEffectWrapper.start();
    }

    public void setSpritePosition(){
        ////Gdx.app.log("Comet", "setSpritePosition");
        super.setSpritePosition();
        float pX = Utility.midPoint(sprite.getVertices()[SpriteBatch.X1], sprite.getVertices()[SpriteBatch.X4]);
        float pY = Utility.midPoint(sprite.getVertices()[SpriteBatch.Y1], sprite.getVertices()[SpriteBatch.Y4]);
        particleEffectWrapper.setPosition(pX, pY);
        particleEffectWrapper.setRotation(bodyWrapper.body.getAngle());
        //particleEffectWrapper.start();
    }

    protected void stopParticleEffect() {
        particleEffectWrapper.stop();
    }

    public void drawSprite(float delta){
        ////Gdx.app.log("Comet", "drawSprite");
        super.drawSprite();
        particleEffectWrapper.draw(delta);
    }

    protected void updateCometSpeed() {
        updates++;
        if (updates % 10 != 0) {
            return;
        }
        float distance = Utility.distance(getXinPixels(), getYinPixels(), parentSpaceObject.getXinPixels(), parentSpaceObject.getYinPixels());
        if (distance < prevDistanceFromSpaceObject) {
            prevDistanceFromSpaceObject = distance;
            return;
        }
        prevDistanceFromSpaceObject = distance;
        float difference = distanceFromSpaceObject - distance;
        float angle = Utility.angle(getXinPixels(), getYinPixels(), parentSpaceObject.getXinPixels(), parentSpaceObject.getYinPixels());
        ////Gdx.app.log("Comet", "angle: " + angle);
        double impulse = Math.abs(difference);
        impulse = Math.sqrt(impulse);
        //impulse *= impulse; //the further it gets, the more violent it will correct
        impulse /= 1000; //scale it down
        if (difference > 0) {
            //applyLinearImpulse((float) impulse, angle);
            applyLinearImpulse(.01f, angle);
        }
        else {
            applyLinearImpulse(.01f, (float) (angle + Math.PI));
        }
    }

    protected void applyInitialCometLinearImpulse() {
        float planetDiameter = parentSpaceObject.getWidth();
        float satelliteMass = bodyWrapper.body.getMass();
        float impulse = (float) Math.sqrt(planetDiameter * 2.334 / satelliteMass);
        impulse *= 0.025;
        float angle = Utility.angle(getXinPixels(), getYinPixels(), parentSpaceObject.getXinPixels(), parentSpaceObject.getYinPixels());
        applyLinearImpulse(impulse, angle);
    }

    // Takes care of ALL comet removal: entity, body, index, comet path, currentSelect
    public void removeBody() {
        cometPath.removeComet(this);
        if (cometPath.comets.size == 0) {
            cometManager.removeCometPath(cometPath);
        }
        cometManager.spaceObjects.removeValue(this, true);
        cometManager.currentSelect = -1;
        super.removeBodyEntity();
    }

    protected void switchDirection() {
        rotateClockwise = !rotateClockwise;
    }
}
