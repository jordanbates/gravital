package com.everless.gravital;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jordan on 1/24/2015.
 * Portal
 * Consists of multiple sprites
 */
public class Portal {

    private GameCore gameCore;
    public Vector2 position;
    public float meterDiameter;
    public float meterRadius;
    public float pixelDiameter = 210;
    public float pixelRadius;
    private float shortWidth = 100.6f;
    private float widthDiff = pixelDiameter - shortWidth;
    private float halfWidthDiff = widthDiff / 2;
    private float gradientWidth = 150;
    private float gradientDiff = pixelDiameter - gradientWidth;
    private float halfGradientDiff = gradientDiff / 2;
    private float closestDistToPortalCenter;
    private Ship ship;
    private boolean touch;
    private int rotation = 0;
    protected Array<Sprite> sprites = new Array<>();
    Sprite gradientSprite;
    protected boolean shrink = false;
    protected boolean phase1 = false;
    protected float shrinkScale = 1;
    private boolean shipHasEnteredPortal = false;
    protected boolean selected = false;
    private boolean minTimeOn = false;
    protected int minTime = 0;
    private Vector3 colorOffset, orange, white;
    public Main main;

    public Portal(GameCore gameCore, LoadedLevel loadedLevel) {
        main = Main.getInstance();
        this.gameCore = gameCore;
        ship = gameCore.ship;
        minTime = loadedLevel.portalMinTime;
        if (minTime > 0) {
            minTimeOn = true;
            gameCore.timer.setMinimumTime(minTime);
        }

        orange = new Vector3(247, 146, 30);
        white = new Vector3(255, 255, 255);
        colorOffset = orange.sub(white);
        //colorOffset = colorOffset.scl(1/255f);

        Sprite sprite1 = new Sprite(main.assetManager.get("portal/portal_white_temp.png", Texture.class));
        Sprite sprite1_ = new Sprite(main.assetManager.get("portal/portal_white_temp.png", Texture.class));
        Sprite sprite2 = new Sprite(main.assetManager.get("portal/portal_white_temp.png", Texture.class));
        Sprite sprite2_ = new Sprite(main.assetManager.get("portal/portal_white_temp.png", Texture.class));
        Sprite sprite3 = new Sprite(main.assetManager.get("portal/portal_white_temp.png", Texture.class));
        Sprite sprite3_ = new Sprite(main.assetManager.get("portal/portal_white_temp.png", Texture.class));
        Sprite sprite4 = new Sprite(main.assetManager.get("portal/portal_white_temp.png", Texture.class));
        Sprite sprite4_ = new Sprite(main.assetManager.get("portal/portal_white_temp.png", Texture.class));
        sprite1.setAlpha(.75f);
        sprite1_.setAlpha(.75f);
        sprite2.setAlpha(.8f);
        sprite2_.setAlpha(.7f);
        sprite3.setAlpha(.6f);
        sprite3_.setAlpha(.5f);
        sprite4.setAlpha(.45f);
        sprite4_.setAlpha(.4f);

        gradientSprite = new Sprite(main.assetManager.get("portal/portal_gradient.png", Texture.class));
        sprite1_.setRotation(90);
        sprite2.setRotation(135);
        sprite2_.setRotation(225);
        sprite3.setRotation(90);
        sprite3_.setRotation(180);
        sprite4.setRotation(-45);
        sprite4_.setRotation(45);
        sprites.add(sprite1);
        sprites.add(sprite1_);
        sprites.add(sprite2);
        sprites.add(sprite2_);
        sprites.add(sprite3);
        sprites.add(sprite3_);
        sprites.add(sprite4);
        sprites.add(sprite4_);

        pixelRadius = pixelDiameter / 2;
        meterDiameter = Utility.pixelsToMeters(pixelDiameter);
        meterRadius =  meterDiameter / 2;
        closestDistToPortalCenter = meterRadius;

        position = new Vector2(GameCore.screenWidth/2 + loadedLevel.portalx - pixelRadius,
                loadedLevel.portaly - pixelRadius);

        for(Sprite sprite: sprites) {
            sprite.setSize(shortWidth, pixelDiameter);
            sprite.setOriginCenter();
            sprite.setPosition(position.x + halfWidthDiff, position.y);
        }

        gradientSprite.setSize(gradientWidth, gradientWidth);
        gradientSprite.setOriginCenter();
        gradientSprite.setPosition(position.x + halfGradientDiff, position.y + halfGradientDiff);
    }

    protected void setPosition() {
        for(Sprite sprite: sprites) {
            sprite.setPosition(position.x + halfWidthDiff, position.y);
            gradientSprite.setPosition(position.x + halfGradientDiff, position.y + halfGradientDiff);
        }
    }

    protected void incrementRotation() {
        rotation += 2;
        for(int i = 0; i < sprites.size; i++) {
            //Want each sprite to rotate at different rates
            float offSet = 1;
            if(i >= 4) {
                offSet = -offSet/2;
            }
            sprites.get(i).setRotation(sprites.get(i).getRotation() + offSet);
        }
    }

    protected void setScale() {
        if (shrink) {
            if (!phase1) {
                
            }
            if (shrinkScale > 0) {
                shrinkScale -= .005;
                gradientSprite.setScale(shrinkScale);
                sprites.get(sprites.size - 1).setScale(shrinkScale);
            }
            //setShrinkScale();
        }
        for(int i = 0; i < sprites.size; i++) {
            if (i >=6) {
                sprites.get(i).setScale(shrinkScale);
                return;
            }
            int j;
            if (i % 2 == 0) {
                j = i;
            }
            else {
                j = i - 1;
            }
            float scale = sprites.get(j).getRotation();
            if (j==4) {
                scale *= 2;
            }
            scale %= 360;
            ////Gdx.app.log("initial scale (should be 0-360)", "scale: " + scale);
            if (scale < 0) {
                scale += 360;
            }
            if (scale < 180) {
                //scale -= 360;
                scale = (360 - scale);
            }
            scale /= 360;
            scale *= shrinkScale;
            sprites.get(i).setScale(scale);
        }
    }

    private void setShrinkScale() {
        for (Sprite sprite : sprites) {
            sprite.setScale(sprite.getScaleX() * shrinkScale);
        }
    }

    public void draw() {
        if (minTimeOn) {
            Vector3 vec = new Vector3(247, 146, 30); //create orange vector
            ////Gdx.app.log("Portal.draw()", "entering draw()");
            ////Gdx.app.log("Portal.draw()", "orange vector: " + vec);
            //.9686,
            vec.sub(white); //subtract white from orange to get offset
            ////Gdx.app.log("Portal.draw()", "orange - white: " + vec);
            float minTimeScale = gameCore.timer.getMinimumTimeScale();
            if (minTimeScale > 1) {
                ////Gdx.app.log("Portal.draw()", "done with minTimeScale: " + minTimeScale);
                for (Sprite sprite : sprites) {
                    sprite.setColor(1, 1, 1, sprite.getColor().a);
                }
                minTimeOn = false;
            }
            else {
                vec.scl(minTimeScale); //scale according to min time
                ////Gdx.app.log("Portal.draw()", "scaled vector according to minTimeScale: " + vec);

                Vector3 currentColor = new Vector3(247, 146, 30); //start again with orange vector
                currentColor.sub(vec); //subtract offset from orange
                ////Gdx.app.log("Portal.draw()", "orange - vec: " + currentColor);
                currentColor.scl(1 / 255f); //scale
                ////Gdx.app.log("Portal.draw()", "scaled 1/255: " + currentColor);

            /*Vector3 scaledOffset = colorOffset.scl(gameCore.timer.getMinimumTimeScale());
            Vector3 currentColor = orange.sub(scaledOffset);
            currentColor = currentColor.scl(1/255f);*/
                ////Gdx.app.log("Portal.draw()", "currentColor: " + currentColor);
                for (Sprite sprite : sprites) {
                    sprite.setColor(currentColor.x, currentColor.y, currentColor.z, sprite.getColor().a);
                }
            }
        }

        //please fix this

        /*for(Sprite sprite: sprites) {
            sprite.draw(GameCore.batch);
        }*/

        gradientSprite.draw(GameCore.batch);
        sprites.get(6).draw(GameCore.batch);
        sprites.get(7).draw(GameCore.batch);
        for(int i = 0; i < sprites.size; i++) {
            if (i<6) {
                sprites.get(i).draw(GameCore.batch);
            }
        }
    }

    protected void resizeShip() {
        Vector2 portalWorldCenter = new Vector2(Utility.pixelsToMeters(position.x) + meterRadius, Utility.pixelsToMeters(position.y) + meterRadius);
        //Get the coordinate of the top middle of the ship
        float xTopMid = Utility.pixelsToMeters(Utility.getShipTopMiddleX(ship));
        float yTopMid = Utility.pixelsToMeters(Utility.getShipTopMiddleY(ship));
        float distanceFromPortal = Utility.distance(portalWorldCenter.x, portalWorldCenter.y, xTopMid, yTopMid);
        if(distanceFromPortal < meterRadius) {
            shipHasEnteredPortal = true;
            if(distanceFromPortal < closestDistToPortalCenter) {
                closestDistToPortalCenter = distanceFromPortal;
                float ratio = distanceFromPortal / meterRadius;
                ratio = Math.min(ratio, 1);
                Vector2 linearVelocity = ship.bodyWrapper.body.getLinearVelocity();
                if (linearVelocity.x > 1.0 || linearVelocity.y > 1.0) {
                    ship.bodyWrapper.body.setLinearDamping((1.0f - ratio) * 30); //starts looking worse/less after 30 because ?
                }
                float newWidthHeight = Utility.trueShipSpriteWidth * ratio;
                float newWidth = Math.max(newWidthHeight, Utility.trueShipSpriteWidth*.3f);
                //GameCore.shipSpriteWidth = newWidthHeight;
                GameCore.shipSpriteWidth = newWidth;
                GameCore.shipSpriteHeight = newWidthHeight; //Seems to look better than getHeight
                float xDiffRatio = Utility.getShipXHorizontalDiff(ship) / Utility.trueShipSpriteWidth;
                // will be 1 when upright. 0 when sideways. -1 upside down
                float yDiffRatio = Utility.getShipXDiff(ship) / Utility.trueShipSpriteHeight;
                // 1 right. -1 left. 0 up/down
                float missingShipWidth = Utility.trueShipSpriteWidth - newWidth;
                float missingShipHeight = Utility.trueShipSpriteHeight - newWidthHeight;
                ship.xOffset = xDiffRatio * missingShipWidth/2;
                ship.yOffSet = -yDiffRatio * missingShipWidth/2;
                ship.xOffset += yDiffRatio * missingShipHeight;
                ship.yOffSet += xDiffRatio * missingShipHeight;
                //ship.shipParticleOffset = Math.min(1.2f - (newWidthHeight / Utility.trueShipSpriteHeight), 1);
                ship.shipParticleOffset = 1.0f - (newWidthHeight / Utility.trueShipSpriteHeight);
                ship.particleEffectScale = Math.max(newWidthHeight / Utility.trueShipSpriteHeight, 0.7f);
                //ship.stopParticleEffect = true;
            }
            else {
                success();
            }
        }
        else if(shipHasEnteredPortal && GameCore.shipSpriteWidth != Utility.trueShipSpriteWidth){
            success();
        }
    }

    private void success() {
        if (!GameCore.shipInPortal) {
            if (gameCore.shipSuccess()) {
                shrink = true;
            }
        }
    }

    /*protected void dispose() {
        portalAtlas.dispose();
    }*/

    /**
     * @return true if portal is affected
     */
    public boolean pan(float x, float y) {
        boolean result = false;
        float portalRadiusPixels = Utility.metersToPixels(meterRadius);
        float portalTouchX = x - portalRadiusPixels;
        float portalTouchY = y - portalRadiusPixels;
        if(touch || Utility.touchOnObject(portalTouchX, portalTouchY, this)) {
            touch = true;
            result = true;
            if(Utility.validatePortalScreenPosition(x, y, pixelRadius, gameCore)) {
                position.set(portalTouchX, portalTouchY);
                setPosition();
            }
            else if(Utility.validatePortalScreenPosition(position.x + portalRadiusPixels, y, pixelRadius, gameCore)) {
                position.set(position.x, portalTouchY);
                setPosition();
            }
            else if(Utility.validatePortalScreenPosition(x, position.y + portalRadiusPixels, pixelRadius, gameCore)) {
                position.set(portalTouchX, position.y);
                setPosition();
            }
        }
        return result;
    }

    public boolean panStop() {
        boolean result = touch;
        touch = false;
        return result;
    }

    protected boolean touchOnPortal(float x, float y) {
        return Utility.touchOnObject(x, y, this);
    }

    protected void select() {
        selected = true;
    }

    protected boolean select(float x, float y) {
        //boolean select = Utility.touchOnObject(x, y, this);
        //TODO: see whats up with needing to account for pixelRadius
        boolean select = Utility.touchOnObject(x - pixelRadius, y - pixelRadius, this);
        if (select) {
            select();
        }
        return select;
    }

    protected void unselect() {
        selected = false;
    }

    protected void setMinimumTime(int time) {
        gameCore.timer.setMinimumTime(time);
        minTime = time;
        minTimeOn = true;
    }

    protected void minTimeOff() {
        gameCore.timer.setMinimumTime(0);
        minTime = 0;
        minTimeOn = false;
        for (Sprite sprite : sprites) {
            sprite.setColor(1, 1, 1, sprite.getColor().a);
        }
    }
}
