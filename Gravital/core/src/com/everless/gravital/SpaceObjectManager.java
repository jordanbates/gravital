package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jordan on 9/1/2015.
 * For managing multiple SpaceObjects
 */
public abstract class SpaceObjectManager {
    protected GameCore gameCore;
    public Array<SpaceObject> spaceObjects = new Array<>();
    protected int currentTouch = -1;
    protected Ship ship;
    protected Portal portal;
    protected Texture spaceObjectTexture;
    protected int currentSelect = -1;
    protected SpaceObject parentSpaceObject;
    public Array<CometManager> cometManagers;
    protected String lastAction = "start";

    public SpaceObjectManager(GameCore gameCore) {
        this.gameCore = gameCore;
        ship = gameCore.ship;
        portal = gameCore.portal;
        spaceObjects = new Array<>();
        currentSelect = -1;
    }

    public void setSpritePosition() {
        for(int i = 0; i < spaceObjects.size; i++) {
            spaceObjects.get(i).setSpritePosition();
            spaceObjects.get(i).setSpriteRotation();
        }
    }

    public void setSpriteRotationTowardsTravel() {
        for (SpaceObject spaceObject : spaceObjects) {
            spaceObject.setSpriteRotationTowardsTravel();
        }
    }

    public void assignGravities(SpaceObject spaceObject) {
        for(SpaceObject spaceObject_2: spaceObjects) {
            spaceObject_2.assignGravity(spaceObject);
        }
    }

    public void applyGravities() {
        for(SpaceObject spaceObject: spaceObjects) {
            spaceObject.applyGravity();
        }
    }

    public void draw(float delta) {
        if (currentSelect != -1) {
            spaceObjects.get(currentSelect).drawSelectSprite();
        }
        for(SpaceObject spaceObject: spaceObjects) {
            spaceObject.drawSprite(delta);
        }
    }

    protected void dispose() {
        int i = 0;
        for(SpaceObject spaceObject: spaceObjects) {
            removePlanetBody(spaceObject, i);
            i++;
        }
        spaceObjectTexture.dispose();
        //do something. planet manager isn't using this? do dispose in PM
    }

    /**
     * @return true if a planet has been selected
     */
    public boolean select(float x, float y) {
        for(int i = 0; i < spaceObjects.size; i++) {
            if(Utility.touchOnObject(x, y, spaceObjects.get(i))) {
                unselect();
                currentSelect = i;
                return true;
            }
        }
        //currentSelect = -1;
        return false;
    }

    public boolean selectComet(float x, float y) {
        for(int i = 0; i < cometManagers.size; i++) {
            if (cometManagers.get(i).select(x, y)) {
                return true;
            }
        }
        return false;
    }

    public void unselect() {
        currentSelect = -1;
    }

    public void unselectComet() {
        for (CometManager cometManager : cometManagers) {
            cometManager.unselect();
        }
    }

    public void deleteSelectedPlanet() {
        if(currentSelect != -1) {
            removePlanetBody(spaceObjects.get(currentSelect), currentSelect);
            spaceObjects.removeIndex(currentSelect);
            currentSelect = -1;
        }
    }

    public void deleteSelectedComet() {
        for (CometManager cometManager : cometManagers) {
            cometManager.deleteSelectedComet();
        }
    }

    /**
     * @return true if pan is attempted on planet
     */
    public boolean pan(float x, float y) {
        boolean result = false;
        for(int i = 0; i < spaceObjects.size; i++) {
            if(spaceObjects.get(i).touch || (currentTouch == -1 && Utility.touchOnObject(x, y, spaceObjects.get(i)))) {
                result = true; //At this point result is true whether planet is actually moved or not
                spaceObjects.get(i).touch = true;
                currentTouch = i;
                float r = spaceObjects.get(i).getRadius();
                float v = spaceObjects.get(i).validateRadius;
                if(Utility.validatePlanetScreenPosition(x, y, r, v, spaceObjects.get(i), gameCore)) {
                    moveSpaceObject(i, x, y);
                }
                else if(Utility.validatePlanetScreenPosition(spaceObjects.get(i).getXinPixels(), y, r, v, spaceObjects.get(i), gameCore)) {
                    moveSpaceObject(i, spaceObjects.get(i).getXinPixels(), y);
                }
                else if(Utility.validatePlanetScreenPosition(x, spaceObjects.get(i).getYinPixels(), r, v, spaceObjects.get(i), gameCore)) {
                    moveSpaceObject(i, x, spaceObjects.get(i).getYinPixels());
                }
                break;
            }
        }
        lastAction = "pan";
        return result;
    }

    protected void moveSpaceObject(int index, float x, float y) {
        float previousX = spaceObjects.get(index).getXinPixels();
        float previousY = spaceObjects.get(index).getYinPixels();
        spaceObjects.get(index).bodyWrapper.body.setTransform(Utility.pixelsToMeters(x), Utility.pixelsToMeters(y), 0);
        //Move comets with planet
        if (cometManagers != null) {
            float xDiff = x - previousX;
            float yDiff = y - previousY;
            for (SpaceObject comet : cometManagers.get(index).spaceObjects) {
                //float cometX = x - comet.distanceFromSpaceObject;
                float cometX = comet.getXinPixels() + xDiff;
                float cometY = comet.getYinPixels() + yDiff;
                comet.bodyWrapper.body.setTransform(Utility.pixelsToMeters(cometX), Utility.pixelsToMeters(cometY), 0);
                comet.startingX = cometX - GameCore.screenWidth/2;
                comet.startingY = cometY;
            }
        }
    }

    public boolean touchStop() {
        boolean result = false;
        if (currentTouch != -1) {
            result = true;
            spaceObjects.get(currentTouch).setRadius();
            spaceObjects.get(currentTouch).touch = false;
            currentTouch = -1;
        }
        if (cometManagers != null) {
            for (CometManager cometManager : cometManagers) {
                cometManager.touchStop();
            }
        }
        return result;
    }

    /**
     * For detecting when a zoom is started
     * @param initialPointer1 first touch down vector
     * @param initialPointer2 second
     * @return true if zoom is started; which atm it always will be
     */
    public boolean zoom(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        float x1 = initialPointer1.x;
        float y1 = initialPointer1.y;
        float x2 = initialPointer2.x;
        float y2 = initialPointer2.y;
        //float initialX = Utility.midPoint(x1, x2);
        //float initialY = Utility.midPoint(y1, y2);
        float initialSize = Utility.distance(x1, y1, x2, y2);
        //Reusing these variables
        //could potentially move some of this stuff into if in true case?
        x1 = pointer1.x;
        y1 = pointer1.y;
        x2 = pointer2.x;
        y2 = pointer2.y;
        float x = Utility.midPoint(x1, x2);
        float y = Utility.midPoint(y1, y2);
        float size = Utility.distance(x1, y1, x2, y2);
        float changeInSize = size - initialSize;
        float scale = 100;
        boolean result = false;
        for(int i = 0; i < spaceObjects.size; i++) {
            if(spaceObjects.get(i).touch || (currentTouch == -1 && Utility.touchOnObject(x, y, spaceObjects.get(i)))) {
                result = true;
                spaceObjects.get(i).touch = true;
                currentTouch = i;
                float radius = spaceObjects.get(i).getRadius();
                float v = spaceObjects.get(i).validateRadius;
                radius += changeInSize / scale;
                //if(validateScreenPosition(x, y, radius, spaceObjects.get(i))) {
                if(radius > 40 && radius < 300 && Utility.validatePlanetScreenPosition(x, y, radius, v, spaceObjects.get(i), gameCore)) {
                    //spaceObjects.get(i).bodyWrapper.setRadius(radius); done in setRadius method
                    spaceObjects.get(i).setTemporaryRadius(radius);
                    //Gdx.app.log("zoom: ", "new radius: " + radius);
                }
                break;
            }
        }
        /*if(!result) { idk why this is even here
            float radius = spaceObjects.get(currentTouch).getRadius();
            radius += changeInSize / scale;
            //if(validateScreenPosition(x, y, radius, spaceObjects.get(currentTouch))) {
            if(Utility.validateCometScreenPosition(x, y, radius, spaceObjects.get(currentTouch), gameCore)) {
                spaceObjects.get(currentTouch).setRadius(radius);
                //Gdx.app.log("zoom: ", "new radius: " + radius);
            }
        }*/
        lastAction = "zoom";
        return result;
    }

    public void removePlanetBody(SpaceObject planet, int index) {
        // this is calling
        if (cometManagers != null) {
            ////Gdx.app.log("removeBody", "comeManagers != null");
            CometManager cometManager = cometManagers.get(index);
            for (SpaceObject spaceObject : cometManager.spaceObjects) {
                ////Gdx.app.log("removeBody", "looping");
                Comet comet = (Comet) spaceObject;
                comet.removeBody();
                // Might want to consider just calling removeBodyEntity
                // Really no reason to worry about cometPaths/indexes/managers other than consistency
            }
            cometManagers.removeIndex(index);
        }
        planet.removeBodyEntity();
    }

    public void removeBody(SpaceObject spaceObjectToDelete) {
        spaceObjectToDelete.removeBodyEntity();
    }

    protected SpaceObject getSelectedSpaceObject() {
        return spaceObjects.get(currentSelect);
    }
}

