package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordan on 7/29/2015.
 * Abstract class for GameObjects
 */
public abstract class SpaceObject {
    GameCore gameCore;
    protected Sprite sprite;
    protected BodyWrapper bodyWrapper;
    protected float validateRadius; //radius of object + any orbiting satellites
    private List<SpaceObject> spaceObjectsAffectedByGravity = new ArrayList<>();
    public boolean touch = false;
    protected float distanceFromSpaceObject;
    protected float prevDistanceFromSpaceObject;
    protected SpaceObject parentSpaceObject;
    public float startingX, startingY;
    private Vector2 previousPosition;
    private Vector2 position;
    private float previousAngle;
    private float angle;
    protected Sprite selectSprite;
    //protected final float SELECTED_RATIO = 1.685f; //ratio of selected sprite size to planet size sprite
    protected final float SELECTED_RATIO = 1.5075f; //ratio of selected sprite size to planet size sprite
    //protected final float SELECTED_RATIO = 1.69f; //ratio of selected sprite size to planet size sprite
    protected final float SHIP_SCALE_WIDTH = 0.7f;
    protected final float SHIP_SCALE_HEIGHT = 0.85f;
    protected String color;
    protected boolean changeRadius = false;
    public Main main;

    public SpaceObject(GameCore gameCore, Texture texture) {
        main = Main.getInstance();
        this.gameCore = gameCore;
        sprite = new Sprite(texture);
        //setSelectSpriteSize();
        //TODO: This is used only for ship and shipPieces
        // so don't need select sprite? should prob be a better way to do this
    }

    public SpaceObject(GameCore gameCore, Texture texture, float width, float height) {
        main = Main.getInstance();
        this.gameCore = gameCore;
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        setSelectSpriteSize();
    }

    public SpaceObject(GameCore gameCore, Texture texture, float width, float height, SpaceObject parentSpaceObject) {
        main = Main.getInstance();
        this.gameCore = gameCore;
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        this.parentSpaceObject = parentSpaceObject;
        setSelectSpriteSize();
    }

    private void setSelectSpriteSize()
    {
        selectSprite = new Sprite(main.assetManager.get("planets/planet_selected.png", Texture.class));
        selectSprite.setSize(sprite.getWidth() * SELECTED_RATIO, sprite.getHeight() * SELECTED_RATIO);
        //selectSprite.setSize(40, 300);
        ////Gdx.app.log("setSelectSpriteSize", "sprite.getWidth: " + sprite.getWidth() + ", selectSprite.getWidth: " + selectSprite.getWidth());
    }

    protected void setValidateRadius() {
        validateRadius = getRadius();
    }

    public void setDistanceFromSpaceObject() {
        distanceFromSpaceObject = Utility.distance(getXinPixels(), getYinPixels(), parentSpaceObject.getXinPixels(), parentSpaceObject.getYinPixels());
        prevDistanceFromSpaceObject = distanceFromSpaceObject;
    }

    public void changeSprite(Texture texture) {
        float prevWidth = sprite.getWidth();
        float prevHeight = sprite.getHeight();
        sprite = new Sprite(texture);
        sprite.setSize(prevWidth, prevHeight);
    }

    /**
     * @param x is based off mid screen
     * @param y is bottom screen
     */
    protected void createBodyWrapper(float x, float y) {
        bodyWrapper = new BodyWrapper(sprite, GameCore.screenWidth/2 + x, y);
    }

    /**
     * @param x is based off mid screen
     * @param y is bottom screen
     */
    protected void createShipBodyWrapper(float x, float y) {
        //bodyWrapper = new BodyWrapper(sprite, GameCore.halfScreenWidth + x, y);
        Utility.trueShipSpriteWidth = sprite.getWidth();
        Utility.trueShipSpriteHeight = sprite.getHeight();
        bodyWrapper = new BodyWrapper(sprite, GameCore.screenWidth/2 + x, y, sprite.getWidth() * SHIP_SCALE_WIDTH, sprite.getHeight() * SHIP_SCALE_HEIGHT);
        bodyWrapper.createShipBody();
        gameCore.entityManager.createEntity(bodyWrapper.body, "ship");
        //bodyWrapper.setUserData("ship");
        Entity shipEntity = (Entity) bodyWrapper.body.getUserData();
        //Gdx.app.log("createShipBodyWrapper", "ship entity: " + shipEntity.type + ", " + shipEntity);
    }

    /**
     * @param x is based off mid screen
     * @param y is bottom screen
     */
    protected void createPlanetBodyWrapper(float x, float y) {
        bodyWrapper = new BodyWrapper(sprite, GameCore.screenWidth/2 + x, y);
        bodyWrapper.createPlanetBody();
        gameCore.entityManager.createEntity(bodyWrapper.body, "planet");
    }

    protected void recreatePlanetBodyWrapper() {
        Entity entity = (Entity) bodyWrapper.body.getUserData();
        gameCore.entityManager.removeEntity(entity);
        gameCore.removeEntities();
        createPlanetBodyWrapper(bodyWrapper.x - GameCore.screenWidth/2, bodyWrapper.y);
    }

    /**
     * @param x is based off mid screen
     * @param y is bottom screen
     */
    protected void createCometBodyWrapper(float x, float y) {
        bodyWrapper = new BodyWrapper(sprite, GameCore.screenWidth/2 + x, y);
        bodyWrapper.createCometBody();
        //bodyWrapper.setUserData("comet");
        gameCore.entityManager.createEntity(bodyWrapper.body, "comet");
    }

    /*public void copyCurrentPosition() {
        if (bodyWrapper.body != null) {
            if (bodyWrapper.body.getType() == BodyDef.BodyType.DynamicBody && bodyWrapper.body.isActive()) {
                previousPosition.x = bodyWrapper.body.getPosition().x;
                previousPosition.y = bodyWrapper.body.getPosition().y;
                previousAngle = bodyWrapper.body.getAngle();
            }
        }
    }

    public void interpolateCurrentPosition(float alpha) {
        if (bodyWrapper.body != null) {
            if (bodyWrapper.body.getType() == BodyDef.BodyType.DynamicBody && bodyWrapper.body.isActive()) {
                //---- interpolate: currentState*alpha + previousState * ( 1.0 - alpha ); ------------------
                position.x = bodyWrapper.body.getPosition().x * alpha + previousPosition.x * (1.0f - alpha);
                position.y = bodyWrapper.body.getPosition().y * alpha + previousPosition.y * (1.0f - alpha);
                angle = bodyWrapper.body.getAngle() * alpha + previousAngle * (1.0f - alpha);
            }
        }
        copyCurrentPosition();
    }*/

    protected void setSpritePosition() {
        sprite.setPosition(Utility.metersToPixels(bodyWrapper.body.getPosition().x) - bodyWrapper.halfBodyWidth,
                Utility.metersToPixels(bodyWrapper.body.getPosition().y) - bodyWrapper.halfBodyHeight);
    }

    protected void setSpriteRotation() {
        sprite.setRotation((float) Math.toDegrees(bodyWrapper.body.getAngle()));
    }

    protected void setSpriteRotationTowardsTravel() {
        float radians = (float) Math.atan2(bodyWrapper.body.getLinearVelocity().y, bodyWrapper.body.getLinearVelocity().x);
        bodyWrapper.body.setTransform(getWorldCenter(), radians - ((float) Math.PI) / 2.0f);
        setSpriteRotation();
    }

    protected void drawSprite() {
        GameCore.batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(), sprite.getOriginY(),
                sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation());
        sprite.draw(GameCore.batch);
    }

    //MUST explicitly specify all of these parameters
    protected void drawSprite(float delta) {
        GameCore.batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(), sprite.getOriginY(),
                sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation());
    }

    protected void drawSelectSprite() {
        float radiusDiff = (sprite.getWidth() - selectSprite.getWidth()) / 2;
        radiusDiff = Math.abs(radiusDiff);
        float x = sprite.getX();
        float y = sprite.getY();

        // Horrible way to do this. Not sure why they're different/off
        if (sprite.getWidth() < 35) {
            x -= radiusDiff;
            y += radiusDiff;
        }
        else {
            x -= radiusDiff;
            y -= radiusDiff;
        }

        GameCore.batch.draw(selectSprite, x, y, sprite.getOriginX(), sprite.getOriginY(),
                selectSprite.getWidth(), selectSprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation());
    }

    protected void assignGravity(SpaceObject spaceObject) {
        if (!spaceObjectsAffectedByGravity.contains(spaceObject)) {
            spaceObjectsAffectedByGravity.add(spaceObject);
        }
    }

    protected void applyGravity() {
        for (SpaceObject spaceObject:spaceObjectsAffectedByGravity) {
            float scalar = 2.334f;
            if(spaceObject instanceof Ship) {
                if (gameCore.shipInMotion) {
                    scalar = 2f;//0.1f; Good for normal rect ship
                    int additionalPlanets = gameCore.planetManager.spaceObjects.size - 1;
                    float gravityDiff = additionalPlanets * .08f;
                    float newGravity = scalar - (scalar * gravityDiff);
                    float minimumGravity = scalar * .25f;
                    scalar = Math.max(newGravity, minimumGravity);
                }
                else {
                    continue; //No gravity to ship until it's launched
                }
            }
            Vector2 objectWorldCenter = spaceObject.getWorldCenter();
            Vector2 planetWorldCenter = getWorldCenter();
            float planetDiameter = Utility.pixelsToMeters(getWidth());
            float distance = Utility.distance(planetWorldCenter.x, planetWorldCenter.y, objectWorldCenter.x, objectWorldCenter.y);
            float xDistance =  planetWorldCenter.x - objectWorldCenter.x;
            float yDistance =  planetWorldCenter.y - objectWorldCenter.y;
            float x = (float) ((xDistance * planetDiameter * scalar) / (distance*distance));
            float y = (float) ((yDistance * planetDiameter * scalar) / (distance*distance));
            Vector2 gravity = new Vector2(x, y);
            spaceObject.bodyWrapper.body.applyForceToCenter(gravity, true);
        }
    }

    protected float getXinPixels() {
        Vector2 worldCenter = getWorldCenter();
        return Utility.metersToPixels(worldCenter.x);
    }

    protected float getYinPixels() {
        Vector2 worldCenter = getWorldCenter();
        return Utility.metersToPixels(worldCenter.y);
    }

    protected Vector2 getWorldCenter() {
        return bodyWrapper.body.getWorldCenter();
    }

    protected float getWidth() {
        return bodyWrapper.bodyWidth;
    }

    protected float getHeight() {
        return bodyWrapper.bodyHeight;
    }

    protected float getRadius() {
        return bodyWrapper.halfBodyWidth;
    }

    protected boolean onScreen() {
        float spriteX = getXinPixels();
        float spriteY = getYinPixels();
        return spriteX >= 0 && spriteX <= GameCore.screenWidth && spriteY >= 0 && spriteY <= GameCore.screenHeight;
    }

    /**
     * @return true if the touch is on the object
     */
    public boolean touchOnObject(float x, float y, SpaceObject spaceObject) {
        float posX = spaceObject.getXinPixels();
        float posY = spaceObject.getYinPixels();
        float halfWidth = spaceObject.getWidth()/2;
        float halfHeight = spaceObject.getHeight()/2;
        float bottom = posX - halfHeight;
        float top = posX + halfHeight;
        float left = posY - halfWidth;
        float right = posY + halfWidth;
        return x > bottom && x < top && y > left && y < right;
    }

    // As a general rule, do not call this directly, do it through SpaceObjectManager
    public void removeBodyEntity() {
        Entity entity = (Entity) bodyWrapper.body.getUserData();
        entity.die();
    }

    protected void applyLinearImpulse(float impulse, float radians) {
        radians += Math.PI/2;
        if (this instanceof Comet) {
            Comet comet = (Comet) this;
            if (!comet.rotateClockwise) {
                radians += Math.PI;
            }
        }
        bodyWrapper.body.applyLinearImpulse(new Vector2((float) Math.cos(radians) * impulse,
                (float) Math.sin(radians) * impulse), getWorldCenter(), true);
    }

    // For planets only atm
    // This will enact any changes to planet radius
    protected void setRadius() {
        if (changeRadius) {
            recreatePlanetBodyWrapper();
            changeRadius = false;
        }
    }

    protected void setTemporaryRadius(float radius) {
        float prevRadius = getRadius(); //call before resetting body size
        float diameter = radius * 2;
        sprite.setSize(diameter, diameter);
        selectSprite.setSize(diameter * SELECTED_RATIO, diameter * SELECTED_RATIO);
        validateRadius += radius - prevRadius;
        bodyWrapper.setTemporaryRadius(radius);
        changeRadius = true;
    }

}
