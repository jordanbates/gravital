package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jordan on 5/26/2016
 */
public class Wormhole {

    private WormholeManager wormholeManager;
    private GameCore gameCore;
    public Vector2 position;
    public float meterDiameter;
    public float meterRadius;
    public float pixelDiameter = 170;
    public float pixelRadius;
    private float closestDistToWormholeCenter;
    private Ship ship;
    protected boolean touch;
    private float rotation = 0;
    protected Array<Sprite> sprites = new Array<>();
    protected Sprite selectSprite;
    //protected final float SELECTED_RATIO = 1.685f; //ratio of selected sprite size to planet size sprite
    protected final float SELECTED_RATIO = 1.6f;
    protected String color;
    protected boolean multi;
    protected boolean usedByShip = false;
    protected boolean blink = false;

    public Wormhole(GameCore gameCore, Vector2 position, Texture texture) {
        this.gameCore = gameCore;
        if (gameCore.wormholeManager != null) {
            wormholeManager = gameCore.wormholeManager;
        }
        ship = gameCore.ship;

        Sprite wormhole = new Sprite(texture);
        rotation = Utility.randInt(0, 359);
        sprites.add(wormhole);

        pixelRadius = pixelDiameter / 2;
        meterDiameter = Utility.pixelsToMeters(pixelDiameter);
        meterRadius =  meterDiameter / 2;
        closestDistToWormholeCenter = meterRadius;

        this.position = position;
        this.position.x -= pixelRadius;
        this.position.y -= pixelRadius;

        for(Sprite sprite: sprites) {
            sprite.setSize(pixelDiameter, pixelDiameter);
            sprite.setOriginCenter();
            sprite.setPosition(position.x, position.y);
        }

        setSelectSpriteSize();
    }

    private void setSelectSpriteSize()
    {
        selectSprite = new Sprite(Main.getInstance().assetManager.get("planets/planet_selected.png", Texture.class));
        selectSprite.setSize(pixelDiameter * SELECTED_RATIO, pixelDiameter * SELECTED_RATIO);
    }

    //Return true if wormholeManager is set
    protected boolean setWormholeManager() {
        if (wormholeManager == null) {
            if (gameCore.wormholeManager != null) {
                wormholeManager = gameCore.wormholeManager;
                return true;
            }
            return false;
        }
        return true;
    }

    protected void setPosition() {
        for(Sprite sprite: sprites) {
            sprite.setPosition(position.x, position.y);
        }
    }

    protected void incrementRotation() {
        rotation += 1.75;
        sprites.get(0).setRotation(rotation);
        if (blink) {
            //make portal fade in and out
            sprites.get(0).setAlpha((float) Math.max(Math.abs(Math.sin(rotation / 40)), 0.1));
        }
    }

    public void draw() {
        sprites.get(0).draw(GameCore.batch);
    }

    protected void drawSelectSprite() {
        float radiusDiff = (pixelDiameter - selectSprite.getWidth()) / 2;
        float x = position.x + radiusDiff;
        float y = position.y + radiusDiff;
        GameCore.batch.draw(selectSprite, x, y, selectSprite.getWidth(), selectSprite.getHeight());
    }

    /**
     * @return true when time to switch portals
     */
    protected boolean resizeShip() {
        boolean switchPortals = false;
        Vector2 wormholeWorldCenter = new Vector2(Utility.pixelsToMeters(position.x) + meterRadius, Utility.pixelsToMeters(position.y) + meterRadius);
        float xTopMid = Utility.pixelsToMeters(Utility.getShipTopMiddleX(ship));
        float yTopMid = Utility.pixelsToMeters(Utility.getShipTopMiddleY(ship));
        float distanceFromWormhole = Utility.distance(wormholeWorldCenter.x, wormholeWorldCenter.y, xTopMid, yTopMid);
        float halfShipHeightMeters = Utility.pixelsToMeters(Utility.trueShipSpriteHeight / 2);
        if (wormholeManager.wormholeShipStatus == EnumWormholeShipStatus.EXITING_WORMHOLE) {
            distanceFromWormhole = Math.max(distanceFromWormhole - halfShipHeightMeters, 0);
        }
        if(distanceFromWormhole < meterRadius || (wormholeManager.wormholeShipStatus == EnumWormholeShipStatus.EXITING_WORMHOLE && distanceFromWormhole < meterRadius + halfShipHeightMeters)) {
            /*//Gdx.app.log("resizeShip()", "distanceFromWormhole < meterRadius: " + distanceFromWormhole + " < " + meterRadius);
            //Gdx.app.log("resizeShip()", "ship: " + ship.getXinPixels() + ", " + ship.getYinPixels());
            //Gdx.app.log("resizeShip()", "wormhole: " + position.x + ", " + position.y);*/
            if (wormholeManager.wormholeShipStatus == EnumWormholeShipStatus.OUTSIDE_OF_WORMHOLE) {
                //Gdx.app.log("ship is OUTSIDE", "ship is being set to ENTERING");
                wormholeManager.wormholeShipStatus = EnumWormholeShipStatus.ENTERING_WORMHOLE;
                wormholeManager.setCurrentWormhole(this);
                closestDistToWormholeCenter = meterRadius;
            }
            float ratio = distanceFromWormhole / meterRadius;
            ratio = Math.min(ratio, 1);
            float newHeight = Utility.trueShipSpriteHeight * ratio;
            float newWidth = Utility.trueShipSpriteWidth * ratio;
            GameCore.shipSpriteWidth = newWidth;
            GameCore.shipSpriteHeight = newHeight;
            float xDiffRatio = Utility.getShipXHorizontalDiff(ship) / Utility.trueShipSpriteWidth; // will be 1 when upright. 0 when sideways. -1 upside down
            float yDiffRatio = Utility.getShipXDiff(ship) / Utility.trueShipSpriteHeight; // 1 right. -1 left. 0 up/down
            float missingShipWidth = Utility.trueShipSpriteWidth - newWidth; //float missingShipHeight = ship.getHeight() - newWidthHeight;
            float missingShipHeight = Utility.trueShipSpriteHeight - newHeight;
            ship.xOffset = xDiffRatio * missingShipWidth/2;
            ship.yOffSet = -yDiffRatio * missingShipWidth/2;
            ship.xOffset += yDiffRatio * missingShipHeight;
            ship.yOffSet += xDiffRatio * missingShipHeight;
            ship.shipParticleOffset = 1.0f - (newHeight / Utility.trueShipSpriteHeight);
            ship.particleEffectScale = Math.max(newHeight / Utility.trueShipSpriteHeight, 0.7f);
            //ship.stopParticleEffect = true;
            if(distanceFromWormhole > closestDistToWormholeCenter) {
                ////Gdx.app.log("distanceFromWormhole > closestDistToWormholeCenter", "true ------------------------------------");
                switchPortals = true;
                usedByShip = true;
                wormholeManager.setCorrespondingWormholeUsedByShip(this);
            }
            closestDistToWormholeCenter = distanceFromWormhole;
        }
        else if(wormholeManager.isCurrentWormhole(this)){
        //else if(wormholeManager.isCurrentWormhole(this) && GameCore.shipSpriteWidth != Utility.trueShipSpriteWidth){
            //Gdx.app.log("in else if", "- - - - - - - - - - - - - - - - - - - - - - - - - -");
            if (wormholeManager.wormholeShipStatus == EnumWormholeShipStatus.EXITING_WORMHOLE) {
                //Gdx.app.log("distanceFromWormhole <  && LEAVING", "distanceFromWormhole: " + distanceFromWormhole + ", meterRadius: " + meterRadius);
                GameCore.shipSpriteWidth = Utility.trueShipSpriteWidth;
                GameCore.shipSpriteHeight = Utility.trueShipSpriteHeight;
                ship.xOffset = 0;
                ship.yOffSet = 0;
                ship.shipParticleOffset = ship.SHIP_PARTICLE_OFFSET;
                ship.particleEffectScale = ship.PARTICLE_EFFECT_SCALE;
                closestDistToWormholeCenter = meterRadius;
                wormholeManager.outsideWormhole(this);
            }
        }
        /*else {
            boolean a = wormholeManager.isCurrentWormhole(this);
            boolean b = GameCore.shipSpriteWidth != Utility.trueShipSpriteWidth;
            //Gdx.app.log("in last else", "- - - - - - - - - - - - - - - a, b: " + a + b);
        }*/
        return switchPortals;
    }

    protected void dispose() {
        for (Sprite sprite : sprites) {
            sprite.getTexture().dispose();
        }
    }

    /**
     * @return true if wormhole is affected
     */
    //TODO: Fix context stuff
    public boolean pan(float x, float y) {
        boolean result = false;
        float wormholeRadiusPixels = Utility.metersToPixels(meterRadius);
        float wormholeTouchX = x - wormholeRadiusPixels;
        float wormholeTouchY = y - wormholeRadiusPixels;
        if(touch || Utility.touchOnObject(wormholeTouchX, wormholeTouchY, this)) {
            touch = true;
            result = true;
            if(Utility.validateWormholeScreenPosition(x, y, pixelRadius, this, gameCore)) {
                position.set(wormholeTouchX, wormholeTouchY);
                setPosition();
            }
            else if(Utility.validateWormholeScreenPosition(position.x + wormholeRadiusPixels, y, pixelRadius, this, gameCore)) {
                position.set(position.x, wormholeTouchY);
                setPosition();
            }
            else if(Utility.validateWormholeScreenPosition(x, position.y + wormholeRadiusPixels, pixelRadius, this, gameCore)) {
                position.set(wormholeTouchX, position.y);
                setPosition();
            }
        }
        return result;
    }

    public void panStop() {
        touch = false;
    }

    protected boolean touchOnWormhole(float x, float y) {
        return Utility.touchOnObject(x, y, this);
    }
}
