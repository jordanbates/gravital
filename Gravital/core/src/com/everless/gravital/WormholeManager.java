package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jordan on 5/30/2016.
 * Manager for all wormholes
 */
public class WormholeManager {
    public Main main;
    protected GameCore gameCore;
    public Array<Wormhole> wormholes = new Array<>();
    //multis MUST be passed through the pass the level
    //public Array<Wormhole> multiWormholes = new Array<>();
    public Array<Texture> textures = new Array<>(); //populate with all textures
    private Texture multiColoredTexture;
    protected int currentTouch = -1;
    protected int currentSelect = -1;
    private int currentWormhole = -1; //Index of current wormhole. -1 if none.
    protected Ship ship;
    protected Portal portal;
    protected final int MAX_WORMHOLES = 14;
    protected float diameter = 170;
    protected EnumWormholeShipStatus wormholeShipStatus = EnumWormholeShipStatus.OUTSIDE_OF_WORMHOLE;

    public WormholeManager(GameCore gameCore, LoadedLevel loadedLevel) {
        main = Main.getInstance();
        this.gameCore = gameCore;
        ship = gameCore.ship;
        portal = gameCore.portal;
        wormholes = new Array<>();
        //multiWormholes = new Array<>();
        textures = new Array<>();

        Texture texture1 = main.assetManager.get("wormhole/wormhole_blue.png", Texture.class);
        Texture texture2 = main.assetManager.get("wormhole/wormhole_red.png", Texture.class);
        Texture texture3 = main.assetManager.get("wormhole/wormhole_orange.png", Texture.class);
        textures.add(texture1);
        textures.add(texture2);
        textures.add(texture3);
        multiColoredTexture = main.assetManager.get("wormhole/wormhole_multi.png", Texture.class);

        if (loadedLevel.wormholes != null) {
            for (int i = 0; i < loadedLevel.wormholes.size; i++) {
                float x = loadedLevel.wormholes.get(i).x;
                float y = loadedLevel.wormholes.get(i).y;
                float x2 = loadedLevel.wormholes.get(i).x2;
                float y2 = loadedLevel.wormholes.get(i).y2;
                boolean multi = loadedLevel.wormholes.get(i).multi;
                //Gdx.app.log("Loading in wormholes", "- - - - - - - - - - - - - Multi: " + multi);
                if (!addWormholePair(x, y, x2, y2, multi)) {
                    //there is a serious problem
                    //TODO: do something
                    //Gdx.app.log("WormholeManager", "could not load wormhole pair ! ! ! ! ! ! !");
                }
            }
        }
    }

    /**
     * return true if wormhole pair added
     */
    public boolean addWormholePair(float x, float y, float x2, float y2, boolean multi) {
        x += GameCore.screenWidth/2;
        x2 += GameCore.screenWidth/2;
        int index1 = -1;
        int index2 = -1;
        Texture wormhole1Texture, wormhole2Texture;
        if (multi) {
            wormhole1Texture = multiColoredTexture;
            wormhole2Texture = multiColoredTexture;
        }
        else {
            index1 = wormholes.size % 3;
            index2 = Math.min((wormholes.size + 1) % 3, textures.size - 1);
            wormhole1Texture = textures.get(index1);
            wormhole2Texture = textures.get(index2);
        }
        Wormhole wormhole1 = new Wormhole(gameCore, new Vector2(x, y), wormhole1Texture);
        Wormhole wormhole2 = new Wormhole(gameCore, new Vector2(x2, y2), wormhole2Texture);
        //Gdx.app.log("addWormholePair", "attempting to add wormhole pair. x: " + x + ", y: " + y);
        if ((wormholes.size + 1 < MAX_WORMHOLES) && Utility.validateWormholeScreenPosition(wormhole1, gameCore)
                && Utility.validateWormholeScreenPosition(wormhole2, gameCore)) {
            wormhole1.multi = multi;
            wormhole2.multi = multi;
            if (multi) {
                wormhole1.color = "multi";
                wormhole2.color = "multi";
                /*multiWormholes.add(wormhole1);
                multiWormholes.add(wormhole2);*/
            }
            else {
                if (index1 == 0) {
                    wormhole1.color = "red";
                } else if (index1 == 1) {
                    wormhole1.color = "blue";
                } else {
                    wormhole1.color = "orange";
                }

                if (index2 == 0) {
                    wormhole2.color = "red";
                } else if (index2 == 1) {
                    wormhole2.color = "blue";
                } else {
                    wormhole2.color = "orange";
                }
            }
            wormholes.add(wormhole1);
            wormholes.add(wormhole2);
            return true;
        } else {
            //Gdx.app.log("WormholeManager", "addWormholePair: Failed to add wormhole - removing");
            return false;
        }
    }

    /**
     * return true if wormhole pair added
     */
    public boolean addWormholePair(float x, float y, boolean multi) {

        int index1 = wormholes.size % 3;
        int index2 = Math.min((wormholes.size + 1) % 3, textures.size - 1);
        Texture wormhole1Texture = textures.get(index1);
        Texture wormhole2Texture = textures.get(index2);
        if (multi) {
            wormhole1Texture = multiColoredTexture;
            wormhole2Texture = multiColoredTexture;
        }
        Wormhole wormhole1 = new Wormhole(gameCore, new Vector2(x, y), wormhole1Texture);
        if (!((wormholes.size < MAX_WORMHOLES - 1) && Utility.validateWormholeScreenPosition(wormhole1, gameCore))) {
            //Gdx.app.log("PlanetManager", "addSpaceObject: Failed to add wormhole - removing");
            return false;
        }

        Wormhole wormhole2 = new Wormhole(gameCore, new Vector2(x, y - 200), wormhole2Texture);
        if (Utility.validateWormholeScreenPosition(wormhole2, gameCore)) {
            addWormholesToArray(wormhole1, wormhole2, multi);
            return true;
        }

        wormhole2 = new Wormhole(gameCore, new Vector2(x, y + 200), wormhole2Texture);
        if (Utility.validateWormholeScreenPosition(wormhole2, gameCore)) {
            addWormholesToArray(wormhole1, wormhole2, multi);
            return true;
        }

        wormhole2 = new Wormhole(gameCore, new Vector2(x - 200, y), wormhole2Texture);
        if (Utility.validateWormholeScreenPosition(wormhole2, gameCore)) {
            addWormholesToArray(wormhole1, wormhole2, multi);
            return true;
        }

        wormhole2 = new Wormhole(gameCore, new Vector2(x + 200, y), wormhole2Texture);
        if (Utility.validateWormholeScreenPosition(wormhole2, gameCore)) {
            addWormholesToArray(wormhole1, wormhole2, multi);
            return true;
        }

        return false;
    }

    private void addWormholesToArray(Wormhole wormhole1, Wormhole wormhole2, boolean multi) {
        wormhole1.multi = multi;
        wormhole2.multi = multi;
        wormholes.add(wormhole1);
        wormholes.add(wormhole2);
    }

    public boolean setWormholeManager() {
        boolean result = true;
        for (Wormhole wormhole : wormholes) {
            if (!wormhole.setWormholeManager()) {
                result = false;
            }
        }
        return result;
    }

    public void update() {
        for (int i = 0; i < wormholes.size; i++) {
            wormholes.get(i).setPosition();
            wormholes.get(i).incrementRotation();
            if((currentWormhole == i || currentWormhole == -1) && wormholes.get(i).resizeShip()) {
                if (currentWormhole == i && wormholeShipStatus == EnumWormholeShipStatus.ENTERING_WORMHOLE) {
                    switchPortals(i);
                    break;
                }
            }
        }
    }

    private void switchPortals(int index) {
        //Gdx.app.log("switchPortals()", "switching portals. ship ENTERING --> LEAVING");
        wormholeShipStatus = EnumWormholeShipStatus.EXITING_WORMHOLE;
        Wormhole prevWormhole = wormholes.get(index);
        prevWormhole.usedByShip = true;
        //Gdx.app.log("switchPortals()", "currentWormhole: " + currentWormhole + ", index: " + index);
        Wormhole newWormhole;
        index = getCorrespondingWormholePairIndex(index);
        newWormhole = wormholes.get(index);
        currentWormhole = index;
        float deltaX = newWormhole.position.x - prevWormhole.position.x;
        float deltaY = newWormhole.position.y - prevWormhole.position.y;
        ship.bodyWrapper.body.setTransform(
                Utility.pixelsToMeters(ship.getXinPixels() + deltaX),
                Utility.pixelsToMeters(ship.getYinPixels() + deltaY),
                ship.bodyWrapper.body.getAngle());
        //Gdx.app.log("switchPortals()", "new Ship coords: " + ship.getXinPixels() + ", " + ship.getYinPixels() + ", wormhole: " + newWormhole.position);
    }

    private int getCorrespondingWormholePairIndex(int index) {
        if ( (index & 1) == 0 ) {
            index += 1;
        } else {
            index -= 1;
        }
        return index;
    }

    //Maybe a better way to do this instead of having to loop?
    protected void setCurrentWormhole(Wormhole wormhole) {
        for (int i = 0; i < wormholes.size; i++) {
            if (wormhole == wormholes.get(i)) {
                currentWormhole = i;
                break;
            }
        }
    }

    protected int getWormholeIndex(Wormhole wormhole) {
        for (int i = 0; i < wormholes.size; i++) {
            if (wormhole == wormholes.get(i)) {
                return i;
            }
        }
        return -1;
    }

    protected void setCorrespondingWormholeUsedByShip(Wormhole wormhole) {
        wormholes.get(getCorrespondingWormholePairIndex(getWormholeIndex(wormhole))).usedByShip = true;
    }

    protected Wormhole getCurrentWormhole() {
        return currentWormhole == -1 ? null : wormholes.get(currentWormhole);
    }

    protected boolean isCurrentWormhole(Wormhole wormhole) {
        return getCurrentWormhole() == wormhole;
    }

    //Will reset enteringWormhole if applicable
    public void outsideWormhole(Wormhole wormhole) {
        /*Toast toast = new Toast("outsideWormhole called", gameCore.HUDStage);
        toast.startToast();*/
        if (currentWormhole >= 0) {
            if (wormholes.get(currentWormhole) == wormhole) {
                wormholeShipStatus = EnumWormholeShipStatus.OUTSIDE_OF_WORMHOLE;
                currentWormhole = -1;
                //Gdx.app.log("outsideWormhole()", "ship LEAVING --> OUTSIDE");
                return;
            }
        }
        //for (int i = 0; i < 20; i++) //Gdx.app.log("outsideWormhole", "problem -!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!-!");
    }

    public void draw(float delta) {
        if (currentSelect != -1) {
            wormholes.get(currentSelect).drawSelectSprite();
            wormholes.get(getCorrespondingWormholePairIndex(currentSelect)).drawSelectSprite();
        }
        for (Wormhole wormhole : wormholes) {
            wormhole.draw();
        }
    }

    protected void dispose() {
        //dispose of textures
        //use entity system to delete if necessary?
    }

    /**
     * Select wormhole that has been tapped
     *
     * @return true if a wormhole has been selected
     */
    public boolean select(float x, float y) {
        float wormholeRadiusPixels = diameter/2;
        x -= wormholeRadiusPixels;
        y -= wormholeRadiusPixels;
        for (int i = 0; i < wormholes.size; i++) {
            if (Utility.touchOnObject(x, y, wormholes.get(i))) {
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

    public void deleteSelected() {
        if (currentSelect != -1) {
            //wormholes.get(currentSelect).dispose();
            wormholes.removeIndex(currentSelect);
            int pairIndex = getCorrespondingWormholePairIndex(currentSelect);
            pairIndex = currentSelect < pairIndex ? pairIndex - 1 : pairIndex;
            wormholes.removeIndex(pairIndex);
            currentSelect = -1;
        }
    }

    /**
     * @return true if pan is attempted on wormhole
     */
    public boolean pan(float x, float y) {
        boolean pan = false;
        float wormholeRadiusPixels = diameter/2;
        float wormholeTouchX = x - wormholeRadiusPixels;
        float wormholeTouchY = y - wormholeRadiusPixels;
        for (int i = 0; i < wormholes.size; i++) {
            if (wormholes.get(i).touch || (currentTouch == -1 && wormholes.get(i).pan(x, y))) {
                pan = true;
                currentTouch = i;
                wormholes.get(i).touch = true;
                if (Utility.validateWormholeScreenPosition(x, y, wormholes.get(i).pixelRadius, wormholes.get(i), gameCore)) {
                    wormholes.get(i).position.set(wormholeTouchX, wormholeTouchY);
                    wormholes.get(i).setPosition();
                } else if (Utility.validateWormholeScreenPosition(wormholes.get(i).position.x + wormholeRadiusPixels, y, wormholes.get(i).pixelRadius, wormholes.get(i), gameCore)) {
                    wormholes.get(i).position.set(wormholes.get(i).position.x, wormholeTouchY);
                    wormholes.get(i).setPosition();
                } else if (Utility.validateWormholeScreenPosition(x, wormholes.get(i).position.y + wormholeRadiusPixels, wormholes.get(i).pixelRadius, wormholes.get(i), gameCore)) {
                    wormholes.get(i).position.set(wormholeTouchX, wormholes.get(i).position.y);
                    wormholes.get(i).setPosition();
                }
                break;
            }
        }
        return pan;
    }

    /*protected void moveSpaceObject(int index, float x, float y) {
        spaceObjects.get(index).bodyWrapper.body.setTransform(Utility.pixelsToMeters(x), Utility.pixelsToMeters(y), 0);
        //Move comets with planet
        if (cometManagers != null) {
            for (SpaceObject comet : cometManagers.get(index).spaceObjects) {
                float cometX = x - comet.distanceFromSpaceObject;
                comet.bodyWrapper.body.setTransform(Utility.pixelsToMeters(cometX), Utility.pixelsToMeters(y), 0);
                comet.startingX = cometX - GameCore.halfScreenWidth;
                comet.startingY = y;
            }
        }
    }*/

    //TODO: Look at panStop in wormhole, might use that instead/also?
    public boolean touchStop() {
        boolean result = false;
        if (currentTouch != -1) {
            result = true;
            wormholes.get(currentTouch).touch = false;
            currentTouch = -1;
        }
        return result;
    }

    protected Wormhole getSelectedWormhole() {
        return wormholes.get(currentSelect);
    }

    /**
     * For detecting when a zoom is started
     *
     * //@param initialPointer1 first touch down vector
     * //@param initialPointer2 second
     * @return true if zoom is started; which atm it always will be
     */
    /*public boolean zoom(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        float x1 = initialPointer1.x;
        float y1 = Utility.AndrToLibGDXCoord_y(initialPointer1.y);
        float x2 = initialPointer2.x;
        float y2 = Utility.AndrToLibGDXCoord_y(initialPointer2.y);
        //float initialX = Utility.midPoint(x1, x2);
        //float initialY = Utility.midPoint(y1, y2);
        float initialSize = Utility.distance(x1, y1, x2, y2);
        //Reusing these variables
        //could potentially move some of this stuff into if in true case?
        x1 = pointer1.x;
        y1 = Utility.AndrToLibGDXCoord_y(pointer1.y);
        x2 = pointer2.x;
        y2 = Utility.AndrToLibGDXCoord_y(pointer2.y);
        float x = Utility.midPoint(x1, x2);
        float y = Utility.midPoint(y1, y2);
        float size = Utility.distance(x1, y1, x2, y2);
        float changeInSize = size - initialSize;
        float scale = 10;
        boolean result = false;
        for (int i = 0; i < spaceObjects.size; i++) {
            if (spaceObjects.get(i).touch || (Utility.touchOnObject(x, y, spaceObjects.get(i)) && currentTouch == -1)) {
                result = true;
                spaceObjects.get(i).touch = true;
                currentTouch = i;
                float radius = spaceObjects.get(i).getRadius();
                float v = spaceObjects.get(i).validateRadius;
                radius += changeInSize / scale;
                //if(validateScreenPosition(x, y, radius, spaceObjects.get(i))) {
                if (radius > 40 && radius < 300 && Utility.validatePlanetScreenPosition(x, y, radius, v, spaceObjects.get(i), gameCore)) {
                    //spaceObjects.get(i).bodyWrapper.setRadius(radius); done in setRadius method
                    spaceObjects.get(i).setRadius(radius);
                    //Gdx.app.log("zoom: ", "new radius: " + radius);
                }
                break;
            }
        }
        return result;
    }*/

    protected boolean allMultiesPassed() {
        boolean result = true;
        for (Wormhole wormhole : wormholes) {
            if (wormhole.multi && !wormhole.usedByShip) {
                wormhole.blink = true;
                result = false;
            }
        }
        return result;
    }
}
