package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jordan on 9/1/2015.
 * Comet Manager. Extends SpaceObjectManager
 */
public class CometManager extends SpaceObjectManager {
    protected Planet planet; //Planet associated with this CometManager
    protected final int MAX_COMETS = 100;
    protected Array<CometPath> cometPaths;
    public Main main;

    //public CometManager(GameCore gameCore, Ship ship, Portal portal, Planet planet) {
    public CometManager(GameCore gameCore, Planet planet) {
        super(gameCore);
        main = Main.getInstance();
        //Gdx.app.log("CometManager()", "creating CometManager _");
        this.planet = planet;
        cometPaths = new Array<>();
        parentSpaceObject = planet;
        this.gameCore = gameCore;
        spaceObjectTexture = main.assetManager.get("comet.png", Texture.class);
    }

    /**
     * return true if all comets added
     */
    public boolean addComets(LoadedPlanet loadedPlanet) {
        boolean added = true;
        for (int i = 0; i < loadedPlanet.comets.size; i++) {
            float x = loadedPlanet.comets.get(i).x + GameCore.screenWidth/2;
            float y = loadedPlanet.comets.get(i).y;
            float diameter = loadedPlanet.comets.get(i).diameter;
            boolean rotateClockwise = loadedPlanet.comets.get(i).rotateClockwise;

            //boolean addComet = addSpaceObject(x + GameCore.halfScreenWidth, Utility.AndrToLibGDXCoord_y(y), radius);
            boolean addComet = addComet(x, y, diameter, rotateClockwise);
            if(addComet) {
                added = false;
            }
        }
        return added;
    }

    /**
     * return true if comet added
     */
    public boolean addComet(float x, float y, float diameter, boolean rotateClockwise) {
        CometPath cometPath = null;
        float radius = Utility.distance(x, y, planet.getXinPixels(), planet.getYinPixels());
        for (CometPath cometPath1 : cometPaths) {
            if (Math.abs(cometPath1.radius - radius) < 15) {
                cometPath = cometPath1;
                break;
            }
        }
        if (cometPath == null) {
            cometPath = new CometPath(radius);
            //TODO: Could potentially fix off-path issues here but should probably fix the save
        }

        SpaceObject spaceObject = new Comet(gameCore, spaceObjectTexture, x - GameCore.screenWidth/2, y, diameter, parentSpaceObject, cometPath, this, rotateClockwise);
        if ((spaceObjects.size < MAX_COMETS)){// && Utility.validateCometScreenPosition(x, y, diameter/2, spaceObject, gameCore)) {
            gameCore.entityManager.createEntity(spaceObject.bodyWrapper.body, "comet");
            spaceObjects.add(spaceObject);
            planet.assignGravity(spaceObject);
            cometPath.addComet((Comet) spaceObject);
            if (!cometPaths.contains(cometPath, true)) {
                cometPaths.add(cometPath);
            }
            return true;
        }
        else {
            removeBody(spaceObject);
            removeCometPath(cometPaths.size - 1);
            return false;
        }
    }

    protected void removeCometPath(int index) {
        cometPaths.removeIndex(index);
        setValidateRadius();
    }

    protected void removeCometPath(CometPath cometPath) {
        cometPaths.removeValue(cometPath, true);
        setValidateRadius();
    }

    protected void setValidateRadius() {
        int cometPathSize = cometPaths.size;
        if (cometPathSize > 0) {
            cometPaths.sort();
            planet.validateRadius = cometPaths.get(cometPaths.size - 1).radius;
        }
        else {
            planet.validateRadius = planet.getRadius();
        }
    }

    public void startComets() {
        ////Gdx.app.log("CometManager.java", "starting comets");
        for(SpaceObject spaceObject : spaceObjects) {
            Comet comet = (Comet) spaceObject;
            //float r = spaceObject.getRadius();
            float p = parentSpaceObject.getRadius();
            float impulse = 0.9f;
            impulse = p/100f;
            //Gdx.app.log("CometManager.java", "impulse: " + impulse);
            comet.applyInitialCometLinearImpulse();
            comet.startComet();
        }
    }

    protected void updateCometSpeeds() {
        for(SpaceObject spaceObject : spaceObjects) {
            Comet comet = (Comet) spaceObject;
            comet.updateCometSpeed();
        }
    }

    public void deleteSelectedComet() {
        if(currentSelect != -1) {
            Comet comet = (Comet) spaceObjects.get(currentSelect);
            comet.removeBody();
        }
    }

    /**
     * @return true if pan is attempted on comet
     */
    public boolean pan(float x, float y) {
        boolean result = false;
        for(int i = 0; i < spaceObjects.size; i++) {
            ////Gdx.app.log("CometManager pan()", "x, y: " + x + ", " + y + ". comet x, y: " +
            //        spaceObjects.get(i).getXinPixels() + ", " + spaceObjects.get(i).getYinPixels());
            if(spaceObjects.get(i).touch || (Utility.touchOnComet(x, y, spaceObjects.get(i)) && currentTouch == -1)) {
                result = true; //At this point result is true whether object is actually moved or not
                spaceObjects.get(i).touch = true;
                currentTouch = i;
                //float r = spaceObjects.get(i).getRadius();
                float r = spaceObjects.get(i).validateRadius;
                //moveSpaceObject(i, x, y); //TEMP
                if (!handleCometScreenPositionValidation((Comet) spaceObjects.get(i), x, y)) {
                    if (!handleCometScreenPositionValidation((Comet) spaceObjects.get(i), spaceObjects.get(i).getXinPixels(), y)) {
                        handleCometScreenPositionValidation((Comet) spaceObjects.get(i), x, spaceObjects.get(i).getYinPixels());
                    }
                }
                /*if(Utility.validateCometScreenPosition(x, y, r, spaceObjects.get(i), planet, this, gameCore)) {
                    moveSpaceObject(i, x, y);
                }
                else if(Utility.validateCometScreenPosition(spaceObjects.get(i).getXinPixels(), y, r, planet, spaceObjects.get(i), this, gameCore)) {
                    moveSpaceObject(i, spaceObjects.get(i).getXinPixels(), y);
                }
                else if(Utility.validateCometScreenPosition(x, spaceObjects.get(i).getYinPixels(), r, planet, spaceObjects.get(i), this, gameCore)) {
                    moveSpaceObject(i, x, spaceObjects.get(i).getYinPixels());
                }*/
                break;
            }
            else {
                //Gdx.app.log("GameCore pan()", "no touch");
            }
        }
        return result;
    }

    /**
     * Only comet on path --> Attempt to move path
     *      Success --> done
     *      Fail --> Attempt to find another path [1]
     * Other comet(s) on path --> [1] Attempt to find another path
     *      Success --> done
     *      Fail --> Attempt to create new path
     *          Success --> done
     *          Fail --> done
     */
    protected boolean handleCometScreenPositionValidation(Comet comet, float x, float y) {
        CometPath cometPath = comet.cometPath;
        float pathRadius = Utility.distance(x, y, planet.getXinPixels(), planet.getYinPixels());
        if (cometPath.comets.size == 1) {
            //Gdx.app.log("handleCometScreenPositionValidation", "attempting to joinCometPath");
            if (Utility.joinCometPath(gameCore, this, comet, pathRadius, x, y)) {
                // TODO: Might want to upgrade this from a boolean.
                // or something
                // As to avoid doing comet validation in every function
                //Gdx.app.log("handleCometScreenPositionValidation", "joinCometPath successful");
                return true;
            }
            //Gdx.app.log("handleCometScreenPositionValidation", "attempting to moveCometPath");
            if (Utility.moveCometPath(gameCore, this, comet, pathRadius)) {
                moveSpaceObject(comet, x, y);
                comet.startingX = x - GameCore.screenWidth/2;
                comet.startingY = y;
                //Gdx.app.log("handleCometScreenPositionValidation", "moveCometPath successful");
                return true;
            }
            //Gdx.app.log("handleCometScreenPositionValidation", "attempting to createCometPath");
            if (Utility.createCometPath(gameCore, this, comet, pathRadius)) {
                moveSpaceObject(comet, x, y);
                comet.startingX = x - GameCore.screenWidth/2;
                comet.startingY = y;
                //Gdx.app.log("handleCometScreenPositionValidation", "createCometPath successful");
                return true;
            }
        }
        else if (cometPath.comets.size > 1) {
            if (Utility.joinCometPath(gameCore, this, comet, pathRadius, x, y)) {
                //Gdx.app.log("handleCometScreenPositionValidation", "joinCometPath on multiple comet path successful");
                return true;
            }
            if (Utility.createCometPath(gameCore, this, comet, pathRadius)) {
                moveSpaceObject(comet, x, y);
                comet.startingX = x - GameCore.screenWidth/2;
                comet.startingY = y;
                //Gdx.app.log("handleCometScreenPositionValidation", "createCometPath on multiple comet path successful");
                return true;
            }
        }
        else {
            //Gdx.app.log("CometManager.validateCometScreenPosition", "comet not recognized in comet path");
            //TODO: Error report or something?
        }
        return false;
    }

    protected void moveSpaceObject(int index, float x, float y) {
        spaceObjects.get(index).setDistanceFromSpaceObject();
        spaceObjects.get(index).bodyWrapper.body.setTransform(Utility.pixelsToMeters(x), Utility.pixelsToMeters(y), 0);
    }

    protected void moveSpaceObject(SpaceObject comet, float x, float y) {
        comet.setDistanceFromSpaceObject();
        comet.bodyWrapper.body.setTransform(Utility.pixelsToMeters(x), Utility.pixelsToMeters(y), 0);
    }

    public boolean select(float x, float y) {
        for(int i = 0; i < spaceObjects.size; i++) {
            if(Utility.touchOnComet(x, y, spaceObjects.get(i))) {
                unselect();
                currentSelect = i;
                return true;
            }
        }
        return false;
    }

    public void unselect() {
        currentSelect = -1;
    }

    protected void switchSelectedCometDirection() {
        if (currentSelect != -1) {
            Comet comet = (Comet) spaceObjects.get(currentSelect);
            comet.switchDirection();
        }
    }

    protected boolean isSelectedCometRotatingClockwise() {
        if (currentSelect != -1) {
            Comet comet = (Comet) spaceObjects.get(currentSelect);
            return comet.rotateClockwise;
        }
        //TODO: Error if not returned
        return true;
    }

    // Return true if there's a selected comet in this cometManager
    public boolean isCometSelected() {
        return !(currentSelect == -1);
    }
}
