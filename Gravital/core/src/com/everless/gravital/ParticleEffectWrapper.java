package com.everless.gravital;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;

/**
 * Created by Jordan on 8/10/2015.
 * Particle Effect Wrapper Class
 */
public class ParticleEffectWrapper {

    private ParticleEffect rocketParticleEffect;
    private ParticleEmitter particleEmitter;

    public ParticleEffectWrapper(FileHandle effectFile, FileHandle imagesDir, String emitter) {
        rocketParticleEffect = new ParticleEffect();
        rocketParticleEffect.load(effectFile, imagesDir);
        //rocketParticleEffect.setPosition(50, 0);
        particleEmitter = rocketParticleEffect.findEmitter(emitter);
        //particleEmitter.setContinuous(true); //this will make it go always, regardless of start
        //rocketParticleEffect.findEmitter("Untitled").setContinuous(true);
    }

    protected void setPosition(float x, float y) {
        rocketParticleEffect.setPosition(x, y);
    }

    protected void setRotation(float angle) {
        float degrees = (float) (Math.toDegrees(angle - Math.PI/2));
        particleEmitter.getAngle().setHigh(degrees);
        particleEmitter.getAngle().setLow(degrees);
    }

    protected void start() {
        particleEmitter.setContinuous(true);
        //rocketParticleEffect.start();
    }

    protected void stop() {
        particleEmitter.setContinuous(false);
    }

    protected void scale(float scale) {
        rocketParticleEffect.scaleEffect(scale);
    }

    protected void draw(float delta) {
        rocketParticleEffect.draw(GameCore.batch, delta);
    }
}
