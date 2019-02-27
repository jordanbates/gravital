package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jordan on 8/5/2015.
 * PlanetManager class
 */
public class PlanetManager extends SpaceObjectManager {
    private boolean cometsStarted = false;
    public Array<Texture> textures = new Array<>();
    protected Texture texture1, texture2, texture3;
    public Array<Texture> ringTextures = new Array<>();
    protected Texture ringTexture1, ringTexture2, ringTexture3;
    protected final int MAX_PLANETS = 9;
    protected final float defaultDiameter = 200;
    public Main main;

    public PlanetManager(GameCore gameCore, LoadedLevel loadedLevel) {
        super(gameCore);
        main = Main.getInstance();
        //Gdx.app.log("PlanetManager()", "creating planetManager");
        cometManagers = new Array<>();
        texture1 = main.assetManager.get("planets/planet_red.png", Texture.class);
        texture2 = main.assetManager.get("planets/planet_blue.png", Texture.class);
        texture3 = main.assetManager.get("planets/planet_orange.png", Texture.class);
        textures.add(texture1);
        textures.add(texture2);
        textures.add(texture3);

        ringTexture1 = main.assetManager.get("rings/ring_red.png", Texture.class);
        ringTexture2 = main.assetManager.get("rings/ring_blue.png", Texture.class);
        ringTexture3 = main.assetManager.get("rings/ring_orange.png", Texture.class);
        ringTextures.add(ringTexture1);
        ringTextures.add(ringTexture2);
        ringTextures.add(ringTexture3);

        for (int i = 0; i < loadedLevel.planets.size; i++) {
            float x = loadedLevel.planets.get(i).x;
            float y = loadedLevel.planets.get(i).y;
            float diameter = loadedLevel.planets.get(i).diameter;

            //Gdx.app.log("PlanetManager.PlanetManager()", "attempting to load planet at x, y:" + x + ", " + y);

            //TODO: Shouldn't I not be adding halfscreenwidth here?
            if (addPlanet(x + GameCore.screenWidth/2, y, diameter, loadedLevel.planets.get(i))) {
                /*CometManager cometManager = new CometManager(gameCore, (Planet) spaceObjects.get(i));
                cometManager.addComets(loadedLevel.planets.get(i));
                cometManagers.add(cometManager);*/ //TODO: temp comment out
            }
            else {
                //there is a serious problem
                //TODO: do something
                //Gdx.app.log("PlanetManager()", "PROBLEM LOADING PLANET");
            }
        }

        for(SpaceObject spaceObject : spaceObjects) {
            gameCore.entityManager.createEntity(spaceObject.bodyWrapper.body, "planet");
        }
    }

    public boolean addPlanet(float x, float yToBeConverted) {
        return addPlanet(x, yToBeConverted, defaultDiameter);
    }

    /**
     * return true if added
     */
    public boolean addPlanet(float x, float y, float diameter) {
        int index = spaceObjects.size % 3;
        SpaceObject spaceObject = new Planet(gameCore, textures.get(index), ringTextures.get(index), x - GameCore.screenWidth/2, y, diameter);
        if ((spaceObjects.size < MAX_PLANETS) && Utility.validatePlanetScreenPosition(x, y, diameter / 2, diameter / 2, spaceObject, gameCore)) {
            if (index == 0) {
                spaceObject.color = "red";
            } else if (index == 1) {
                spaceObject.color = "blue";
            }
            else {
                spaceObject.color = "orange";
            }
            spaceObjects.add(spaceObject);
            CometManager cometManager = new CometManager(gameCore, (Planet) spaceObject);
            cometManagers.add(cometManager);
            if (gameCore.isGameStateCreate()) {
                select(x, y); // select planet
            }
            //Gdx.app.log("PlanetManager", "addSpaceObject: Successfully added object");
            return true;
        }
        else {
            removeBody(spaceObject);
            gameCore.removeEntities(); //TODO: Remove if not needed
            //Gdx.app.log("PlanetManager", "addSpaceObject: Failed to add object - removing");
            return false;
        }
    }

    /**
     * return true if added
     */
    public boolean addPlanet(float x, float y, float diameter, LoadedPlanet loadedPlanet) {
        int index = 0;
        String color = loadedPlanet.color;
        switch (color) {
            case "blue": index = 1;
                break;
            case "orange": index = 2;
                break;
        }
        SpaceObject spaceObject = new Planet(gameCore, textures.get(index), ringTextures.get(index), x - GameCore.screenWidth/2, y, diameter);
        if ((spaceObjects.size < MAX_PLANETS) && Utility.validatePlanetScreenPosition(x, y, diameter / 2, diameter / 2, spaceObject, gameCore)) {
            spaceObject.color = color;
            spaceObjects.add(spaceObject);
            CometManager cometManager = new CometManager(gameCore, (Planet) spaceObject);
            cometManager.addComets(loadedPlanet);
            cometManagers.add(cometManager);
            if (gameCore.isGameStateCreate()) {
                select(x, y); // select planet
            }
            //Gdx.app.log("PlanetManager", "addSpaceObject: Successfully added object");
            return true;
        }
        else {
            removeBody(spaceObject);
            gameCore.removeEntities(); //TODO: Remove if not needed
            //Gdx.app.log("PlanetManager", "addSpaceObject: Failed to add object - removing");
            return false;
        }
    }

    public boolean addComet() {
        SpaceObject planet = spaceObjects.get(currentSelect);
        CometManager cometManager = cometManagers.get(currentSelect);
        Array<CometPath> cometPaths = cometManager.cometPaths;

        cometPaths.sort();

        float currentPath;
        if (cometPaths.size > 0) {
            currentPath = cometPaths.get(cometPaths.size - 1).radius + Utility.cometSpace;
        }
        else {
            currentPath = planet.validateRadius + Utility.cometSpace;
        }

        // Want to try adding comets on most outward path in, creating a new path if possible
        boolean moreInnerPaths = true;
        while (moreInnerPaths) {
            CometPath existingCometPath = null;
            for (CometPath cometPath : cometManager.cometPaths) {
                if (Math.abs(currentPath - cometPath.radius) < Utility.cometSpace) {
                    currentPath = cometPath.radius;
                    existingCometPath = cometPath;
                    break;
                }
            }
            Vector2 pos;
            // If this is a new comet path, just add comet to first position on path
            // If the comet path is unavailable, go to next in loop
            // Kinda messy
            if (existingCometPath == null) {
                if (!Utility.validateCometPathPosition(gameCore, cometManager, currentPath, null)) {
                    currentPath -= Utility.cometSpace;
                    if (currentPath < planet.getRadius() + Utility.cometSpace) {
                        moreInnerPaths = false;
                    }
                    continue;
                }
                pos = new Vector2(planet.getXinPixels() + currentPath, planet.getYinPixels());
            } else { // Else find an available spot on path
                pos = Utility.findAvailablePositionOnCometPath(existingCometPath.comets, planet, currentPath);
            }
            if (pos.x != -9999) {
                boolean success = cometManager.addComet(pos.x, pos.y, Utility.cometDiameter, true);
                if (!success) {
                    return false;
                }
                planet.validateRadius = Math.max(planet.validateRadius, currentPath);
                return true;
            } else {
                currentPath -= Utility.cometSpace;
                if (currentPath < planet.getRadius() + Utility.cometSpace) {
                    moreInnerPaths = false;
                }
            }
        }
        //Out of inner paths, try outer paths
        //Gdx.app.log("PlanetManager.addComet()", "no inner paths work. trying outer paths");
        currentPath = planet.validateRadius + Utility.cometSpace;
        //Gdx.app.log("currentPath < halfScreenHeight: ", currentPath + " ? " + GameCore.halfScreenHeight);
        while (currentPath < GameCore.halfScreenHeight) {
            if (Utility.validateCometPathPosition(gameCore, cometManager, currentPath, null)) {
                Vector2 pos = new Vector2(planet.getXinPixels() + currentPath, planet.getYinPixels());
                if (pos.x != -9999) {
                    boolean success = cometManager.addComet(pos.x, pos.y, Utility.cometDiameter, true);
                    if (!success) {
                        return false;
                    }
                    planet.validateRadius = Math.max(planet.validateRadius, currentPath);
                    return true;
                }
            }
            currentPath += Utility.cometSpace;
            //Gdx.app.log("currentPath < halfScreenHeight: ", currentPath + " ? " + GameCore.halfScreenHeight);
        }
        //Gdx.app.log("PlanetManager.addComet()", "no positions available; exiting addComet");
        return false;
    }

    public void draw(float delta) {
        super.draw(delta);
        for (int i = 0; i < spaceObjects.size; i++) {
            if (gameCore.isGameStateCreate()) {
                Planet planet = (Planet) spaceObjects.get(i);
                CometManager cometManager = cometManagers.get(i);
                for (CometPath cometPath : cometManager.cometPaths) {
                    planet.drawRingSprite(cometPath.radius);
                }
            }
        }
        for(CometManager cometManager : cometManagers) {
            cometManager.draw(delta);
        }
    }

    public void startComets() {
        if(!cometsStarted) {
            //Gdx.app.log("PlanetManager", "starting comets");
            for (CometManager cometManager : cometManagers) {
                cometManager.startComets();
            }
            cometsStarted = true;
        }
    }

    public void updateCometSpeeds() {
        for (CometManager cometManager : cometManagers) {
            cometManager.updateCometSpeeds();
        }
    }

    public void setSpritePosition() {
        super.setSpritePosition();
        for(CometManager cometManager : cometManagers) {
            cometManager.setSpritePosition();
            cometManager.setSpriteRotationTowardsTravel();
        }
    }

    public void dispose() {
        super.dispose();
        for(CometManager cometManager : cometManagers) {
            cometManager.dispose();
        }
    }

    protected boolean cometPan(float x, float y) {
        //Gdx.app.log("cometPan", "in cometPan");
        for (CometManager cometManager : cometManagers) {
            if (cometManager.pan(x, y)) {
                //Gdx.app.log("cometPan", "comet pan success");
                return true;
            }
        }
        return false;
    }

    protected void switchSelectedCometDirection() {
        for (CometManager cometManager : cometManagers) {
            cometManager.switchSelectedCometDirection();
        }
    }

    protected boolean isSelectedCometRotatingClockwise() {
        for (CometManager cometManager : cometManagers) {
            if (cometManager.isCometSelected()) {
                return cometManager.isSelectedCometRotatingClockwise();
            }
        }
        //TODO: Error, shouldn't get this far
        return true;
    }

    // Will tell if there exists ANY comet in game that is selected
    public boolean isCometSelected() {
        boolean selected = false;
        if (cometManagers != null) {
            for (CometManager cometManager : cometManagers) {
                if (cometManager.currentSelect != -1) {
                    selected = true;
                }
            }
        }
        return selected;
    }

    protected SpaceObject getSelectedComet() {
        for (CometManager cometManager : cometManagers) {
            if (cometManager.currentSelect != -1) {
                return cometManager.getSelectedSpaceObject();
            }
        }
        //TODO: Error
        return null;
    }
}