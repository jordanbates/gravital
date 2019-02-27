package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jordan on 7/29/2015.
 * Utility class for conversions
 */
public class Utility {
    //Warning: static assets can cause problems being unloaded
    public static Main main;
    public static final float PIXELS_TO_METERS = 100f;
    private static Random rand = new Random();
    public static final float cometDiameter = 25;
    public static final float cometSpace = 75;
    public static float trueShipSpriteWidth;
    public static float trueShipSpriteHeight;
    public static final float MODEL_SCREEN_WIDTH = 1080;
    public static final float MODEL_SCREEN_HEIGHT = 1920;
    //public static final float MODEL_SCREEN_HEIGHT = 1776;
    public static final float MODEL_HALF_SCREEN_WIDTH = MODEL_SCREEN_WIDTH / 2;
    public static final float MODEL_HALF_SCREEN_HEIGHT = MODEL_SCREEN_HEIGHT / 2;
    public static float backgroundX, backgroundY, backgroundWidth, backgroundHeight;
    public static ArrayList<String> levelPacks = new ArrayList<>();
    public static BitmapFont font_112_border, font_70_border, font_50_border, font_30_border, font_140, font_50, font_70;

    static {
        levelPacks.add("Ready For Takeoff");
        levelPacks.add("Through The Wormhole");
        levelPacks.add("Breaking Barriers");
        levelPacks.add("Warped Wormholes");
        levelPacks.add("The Timing Is Right");
        levelPacks.add("My Levels");
    }

    public static float metersToPixels(float pixelValue) {
        return pixelValue * PIXELS_TO_METERS;
    }

    public static float pixelsToMeters(float pixelValue) {
        return pixelValue / PIXELS_TO_METERS;
    }

    public static Vector2 convertVectorToLibGDXCoordinates(Vector2 vector2) {
        float newX = convertValueToLibGDXCoordinate(GameCore.screenWidth, vector2.x);
        float newY = convertValueToLibGDXCoordinate(GameCore.screenWidth, vector2.y);
        return new Vector2(newX, newY);
    }

    public static float convertValueToLibGDXCoordinate(float axisTotal, float value) {
        float newValue = (value - (axisTotal / 2)) / 2;
        if (axisTotal == GameCore.screenHeight) {
            newValue = -newValue;
        }
        return newValue;
    }

    //Shouldn't need to use this if you unproject all input coordinates
    public static float AndrToLibGDXCoord_y(float value) {
        return GameCore.screenHeight - value;
    }

    public static float LibGDXCoordinateToAndroid_y(float value) {
        return GameCore.screenHeight + value;
    }

    public static float distance(float x1, float y1, float x2, float y2) {
        float xDistance = x2 - x1;
        float yDistance = y2 - y1;
        float xDistanceSquared = xDistance * xDistance;
        float yDistanceSquared = yDistance * yDistance;
        return (float) Math.sqrt(xDistanceSquared + yDistanceSquared);
    }

    public static float angle(float x1, float y1, float x2, float y2) {
        float dx = (x2 - x1);
        float dy = (y2 - y1);
        double angle;

        // Calculate angle
        if (dx == 0.0) {
            if (dy == 0.0)
                angle = 0.0;
            else if (dy > 0.0)
                angle = Math.PI / 2.0;
            else
                angle = Math.PI * 3.0 / 2.0;
        } else if (dy == 0.0) {
            if (dx > 0.0)
                angle = 0.0;
            else
                angle = Math.PI;
        } else {
            if (dx < 0.0)
                angle = Math.atan(dy / dx) + Math.PI;
            else if (dy < 0.0)
                angle = Math.atan(dy / dx) + (2 * Math.PI);
            else
                angle = Math.atan(dy / dx);
        }

        // Convert to degrees
        //angle = angle * 180 / Math.PI;

        // Return
        return (float) angle;
    }

    public static float angleInDegrees(float x1, float y1, float x2, float y2) {
        float radians = angle(x1, y1, x2, y2);
        return (float) (radians * 180 / Math.PI);
    }

    public static float normalizeAngle(float angle) {
        return (float) Math.atan2(Math.sin(angle), Math.cos(angle));
    }

    public static LoadedLevel readJSONLevel() {
        Json json = new Json();
        json.setTypeName(null);
        json.setUsePrototypes(false);
        json.setIgnoreUnknownFields(true);
        json.setOutputType(JsonWriter.OutputType.json);

        int level = main.level;
        String levelPack = main.levelPack;
        //Gdx.app.log("Utility.readJSONLevel", "level: " + level + ", levelPack: " + levelPack);

        //Preloaded levels, including empty level for create, are in internal; else local
        if ((levelPacks.contains(levelPack) && !levelPack.equals("My Levels"))
                || (levelPack.equals("create") && level == 0)) {
            //Gdx.app.log("Utility.readJSONLevel", "first part of if, fetching file from internal (preloaded)");
            //Gdx.app.log("Utility.readJSONLevel", "FileHandle: " + Gdx.files.internal("levels/" + levelPack + "/" + level + ".json").toString());
            return json.fromJson(LoadedLevel.class, Gdx.files.internal(
                    "levels/" + levelPack + "/" + level + ".json"));
            /*return json.fromJson(LoadedLevel.class,
                    main.assetManager.get("levels/Ready For Takeoff/1.json", Json.class).toString());*/
        }
        else {
            /*if (levelPack.equals("Edit My Levels")) {
                levelPack = "My Levels";
            }*/
            //Gdx.app.log("Utility.readJSONLevel", "in else, fetching file from local (user made)");
            return json.fromJson(LoadedLevel.class, Gdx.files.local(
                    "levels/" + levelPack + "/" + level + ".json"));
        }
    }

    public static void saveJSONLevel(LoadedLevel loadedLevel, String levelPack) {
        //Gdx.app.log("Utility.saveJSONLevel()", "entering saveJSONLevel. levelPack: " + levelPack);
        int level = loadedLevel.level;
        Json json = new Json();
        String text = json.prettyPrint(loadedLevel);
        ////Gdx.app.log("Utility.Save LoadedLevel: ", text);

        FileHandle file = Gdx.files.local("levels/" + levelPack + "/" + level + ".json");
        file.writeString(text, false); // True means append, false means overwrite.

        //Assuming the last level pack is always My Levels
        if (levelPack.equals(Utility.levelPacks.get(Utility.levelPacks.size()-1))) {
            main.profile.saveUserLevel(loadedLevel.level, loadedLevel.levelName);
        }

        //Gdx.app.log("Utility.saveJSONLevel: ", "saving to Gdx.files.local(levels/" + levelPack + "/" + level + ".json");
        main.persistProfile();
    }

    public static LoadedLevel renameLevel(LoadedLevel loadedLevel, String name) {
        //Gdx.app.log("Utility.renameLevel()", "entering renameLevel");
        main.profile.saveUserLevel(loadedLevel.level, name);
        loadedLevel.levelName = name;
        return loadedLevel;
    }

    //Meant for user-created levels only
    public static void deleteLevel(LoadedLevel loadedLevel) {
        main.profile.deleteUserLevel(loadedLevel.level);
        FileHandle file = Gdx.files.local("levels/" + main.levelPack + "/" + loadedLevel.level + ".json");
        file.delete();
        main.persistProfile();
    }

    //original
    public static boolean overlap_(float x1, float y1, float r, SpaceObject o2) {
        float pad = (o2 instanceof Ship) ? 50.0f : 0.0f;
        float w2 = o2.getWidth()/2 + pad;
        float h2 = o2.getHeight()/2 + pad;
        return Utility.overlap(x1, y1, r, r, o2.getXinPixels(), o2.getYinPixels(), w2, h2);
    }

    //new
    public static boolean overlap(float x1, float y1, float r, SpaceObject o2) {
        float w2, h2;
        if (o2 instanceof Ship) {
            float pad = 50.0f;
            w2 = o2.getWidth()/2 + pad;
            h2 = o2.getHeight()/2 + pad;
        }
        else {
            w2 = o2.validateRadius;
            h2 = o2.validateRadius;
        }
        return Utility.overlap(x1, y1, r, r, o2.getXinPixels(), o2.getYinPixels(), w2, h2);
    }

    public static boolean overlapPath(float x1, float y1, float r, SpaceObject o2) {
        float w2, h2;
        if (o2 instanceof Ship) {
            float pad = 50.0f;
            w2 = o2.getWidth()/2 + pad;
            h2 = o2.getHeight()/2 + pad;
        }
        else {
            w2 = o2.validateRadius;
            h2 = o2.validateRadius;
        }
        float r2 = Math.max(w2, h2);
        return Utility.overlapPath(x1, y1, r, o2.getXinPixels(), o2.getYinPixels(), r2);
    }

    public static boolean overlapWithParentPlanet(float x1, float y1, float r, SpaceObject o2) {
        float w2 = o2.getWidth();
        float h2 = o2.getHeight();
        return Utility.overlap(x1, y1, r, r, o2.getXinPixels(), o2.getYinPixels(), w2, h2);
    }

    public static boolean overlap(float x1, float y1, float r1, Portal portal) {
        float r2 = portal.pixelRadius;
        float x2 = portal.position.x + r2;
        float y2 = portal.position.y + r2;
        return Utility.overlap(x1, y1, r1, r1, x2, y2, r2, r2);
    }

    public static boolean overlapPath(float x1, float y1, float r1, Portal portal) {
        float r2 = portal.pixelRadius;
        float x2 = portal.position.x + r2;
        float y2 = portal.position.y + r2;
        return overlapPath(x1, y1, r1, x2, y2, r2);
    }

    public static boolean overlap(float x1, float y1, float r1, Wormhole wormhole) {
        float r2 = wormhole.pixelRadius;
        float x2 = wormhole.position.x + r2;
        float y2 = wormhole.position.y + r2;
        return Utility.overlap(x1, y1, r1, r1, x2, y2, r2, r2);
    }

    public static boolean overlapPath(float x1, float y1, float r1, Wormhole wormhole) {
        float r2 = wormhole.pixelRadius;
        float x2 = wormhole.position.x + r2;
        float y2 = wormhole.position.y + r2;
        return overlapPath(x1, y1, r1, x2, y2, r2);
    }

    public static boolean overlap(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
        float distance = distance(x1, y1, x2, y2);

        //Currently checking against max of widths and heights
        //Okay since only using circular objects
        return (distance < Math.max(w1 + w2, h1 + h2));
    }

    // For non-solid circle path + solid circle
    // Assuming 1st object is the non-solid
    // pathPad extends from both sides of perimeter
    public static boolean overlapPath(float x1, float y1, float r1, float x2, float y2, float r2) {
        float distance = distance(x1, y1, x2, y2);
        float pathPad = 20;

        float maxD = r1 + r2 + pathPad;
        return (distance < maxD && distance > r1 - r2 - pathPad);
    }

    public static boolean offScreen(float x, float y, float r) {
        return (x < r) || (x > GameCore.screenWidth - r) || (y < r) || (y > GameCore.screenHeight - r);
    }

    public static boolean touchOnObject(float x, float y, SpaceObject spaceObject) {
        float posX = spaceObject.getXinPixels();
        float posY = spaceObject.getYinPixels();
        float halfWidth = spaceObject.getWidth()/2;
        float halfHeight = spaceObject.getHeight()/2;
        float bottom = posX - halfHeight;
        float top = posX + halfHeight;
        float left = posY - halfWidth;
        float right = posY + halfWidth;
        return touchOnObject(x, y, bottom, top, left, right);
    }

    //Allows a larger area of touch
    public static boolean touchOnComet(float x, float y, SpaceObject spaceObject) {
        float posX = spaceObject.getXinPixels();
        float posY = spaceObject.getYinPixels();
        float halfWidth = spaceObject.getWidth() * 1.5f; //wrong on purpose
        float halfHeight = spaceObject.getHeight() * 1.5f;
        float bottom = posX - halfHeight;
        float top = posX + halfHeight;
        float left = posY - halfWidth;
        float right = posY + halfWidth;
        return touchOnObject(x, y, bottom, top, left, right);
    }

    public static boolean touchOnObject(float x, float y, Portal portal) {
        float posX = portal.position.x;
        float posY = portal.position.y;
        float radius = Utility.metersToPixels(portal.meterRadius);
        float bottom = posX - radius;
        float top = posX + radius;
        float left = posY - radius;
        float right = posY + radius;
        return touchOnObject(x, y, bottom, top, left, right);
    }

    public static boolean touchOnObject(float x, float y, Wormhole wormhole) {
        float posX = wormhole.position.x;
        float posY = wormhole.position.y;
        float radius = Utility.metersToPixels(wormhole.meterRadius);
        float bottom = posX - radius;
        float top = posX + radius;
        float left = posY - radius;
        float right = posY + radius;
        return touchOnObject(x, y, bottom, top, left, right);
    }

    public static boolean touchOnObject(float x, float y, float bottom, float top, float left, float right) {
        return x > bottom && x < top && y > left && y < right;
    }

    /**
     * @param r radius
     * @param v validatedRadius
     * @return true if planet can move here
     */
    public static boolean validatePlanetScreenPosition(float x, float y, float r, float v, SpaceObject spaceObject, GameCore gameCore) {
        if (offScreen(x, y, r) || overlap(x, y, v, gameCore.portal) || overlap(x, y, v, gameCore.ship)) {
            return false;
        }
        if (gameCore.planetManager != null) {
            for (SpaceObject planet : gameCore.planetManager.spaceObjects) {
                //Will want to check for comet paths as well
                if (spaceObject != planet && overlap(x, y, v, planet)) {
                    return false;
                }
            }
        }
        if (gameCore.wormholeManager != null) {
            for (Wormhole wormhole : gameCore.wormholeManager.wormholes) {
                if (overlap(x, y, v, wormhole)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean validateCometScreenPosition(float x, float y, float r, SpaceObject comet, SpaceObject parentPlanet, CometManager cometManager, GameCore gameCore) {
        //if (offScreen(x, y, r) || overlap(x, y, r, gameCore.portal) || overlap(x, y, r, gameCore.ship)) {
        //if (overlap(x, y, r, gameCore.portal) || overlap(x, y, r, gameCore.ship)) {
        //    return false;
        //}
        //Shouldn't need to check those because it should be taken care of by checking planets
        //validateRadius first
        if (gameCore.planetManager != null) {
            for (SpaceObject planet : gameCore.planetManager.spaceObjects) {
                //if it's the parent planet, we don't want validate radius
                if ((parentPlanet == planet && overlapWithParentPlanet(x, y, r, planet))
                        ||overlap(x, y, r, planet)) {
                    return false;
                }
            }
            for (SpaceObject fellowComet : cometManager.spaceObjects) {
                if (comet != fellowComet && overlap(x, y, r, fellowComet)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean validatePortalScreenPosition(float x, float y, float r, GameCore gameCore) {
        if (offScreen(x, y, r) || overlap(x, y, r, gameCore.ship)) {
            return false;
        }
        for (SpaceObject planet: gameCore.planetManager.spaceObjects) {
            if (overlap(x, y, r, planet)) {
                return false;
            }
        }
        if (gameCore.wormholeManager != null) {
            for (Wormhole wormhole : gameCore.wormholeManager.wormholes) {
                if (overlap(x, y, r, wormhole)) {
                    return false;
                }
            }
        }
        return true;
    }

    // This is for checking NEW coordinates that are not the current coordinates of the wormhole
    public static boolean validateWormholeScreenPosition(float x, float y, float r, Wormhole wormhole, GameCore gameCore) {
        if (offScreen(x, y, r) || overlap(x, y, r, gameCore.portal) || overlap(x, y, r, gameCore.ship)) {
            //Gdx.app.log("validateWormholeScreenPosition", "first if");
            return false;
        }
        if (gameCore.planetManager != null) {
            for (SpaceObject planet: gameCore.planetManager.spaceObjects) {
                if (overlap(x, y, r, planet)) {
                    //Gdx.app.log("validateWormholeScreenPosition", "second if");
                    return false;
                }
            }
        }
        if (gameCore.wormholeManager != null) {
            for (Wormhole wormhole2 : gameCore.wormholeManager.wormholes) {
                if (wormhole != wormhole2 && overlap(x, y, r, wormhole2)) {
                    //Gdx.app.log("validateWormholeScreenPosition", "third if");
                    return false;
                }
            }
        }
        return true;
    }

    //For adding wormholes
    public static boolean validateWormholeScreenPosition(Wormhole wormhole, GameCore gameCore) {
        float r = wormhole.pixelRadius;
        float x = wormhole.position.x + r;
        float y = wormhole.position.y + r;
        if (offScreen(x, y, r) || overlap(x, y, r, gameCore.portal) || overlap(x, y, r, gameCore.ship)) {
            //Gdx.app.log("validateWormholeScreenPosition", "first if");
            return false;
        }
        if (gameCore.planetManager != null) {
            for (SpaceObject planet: gameCore.planetManager.spaceObjects) {
                if (overlap(x, y, r, planet)) {
                    //Gdx.app.log("validateWormholeScreenPosition", "second if");
                    return false;
                }
            }
        }
        if (gameCore.wormholeManager != null) {
            for (Wormhole wormhole2 : gameCore.wormholeManager.wormholes) {
                if (wormhole != wormhole2 && overlap(x, y, r, wormhole2)) {
                    //Gdx.app.log("validateWormholeScreenPosition", "third if");
                    return false;
                }
            }
        }
        return true;
    }

    // Only one comet on path
    public static boolean moveCometPath(GameCore gameCore, CometManager cometManager, Comet comet, float pathRadius) {
        if (validateCometPathPosition(gameCore, cometManager, pathRadius, comet.cometPath)) {
            //move it;
            comet.cometPath.radius = pathRadius;
            return true;
        }
        return false;
    }

    public static boolean createCometPath(GameCore gameCore, CometManager cometManager, Comet comet, float pathRadius) {
        // Check if new comet path placement intersects with anything
        // If not, create new path
        if (validateCometPathPosition(gameCore, cometManager, pathRadius, comet.cometPath)) {
            comet.cometPath.removeComet(comet);
            CometPath cometPath = new CometPath(pathRadius);
            cometPath.addComet(comet);
            cometManager.cometPaths.add(cometPath);
            return true;
        }
        return false;
    }

    public static boolean joinCometPath(GameCore gameCore, CometManager cometManager, Comet comet, float pathRadius, float x, float y) {
        // Check if there's path to snap to, if so, snap to it
        // Check if comet intersects with other comets on path
        float pathPad = cometSpace / 2;
        for (CometPath cometPath : cometManager.cometPaths) {
            //Check if we're close enough to snap to
            if (cometPath != comet.cometPath && Math.abs(cometPath.radius - pathRadius) < pathPad) {
                //Close enough to snap to

                float planetX = cometManager.planet.getXinPixels();
                float planetY = cometManager.planet.getYinPixels();
                float angle = Utility.angle(planetX, planetY, x, y);
                float increment = (float) (2 * Math.PI / 16);
                List<Float> cometAngles = new ArrayList<>();
                for (SpaceObject comet1 : cometPath.comets) {
                    cometAngles.add(Utility.angle(planetX, planetY, comet1.getXinPixels(), comet1.getYinPixels()));
                }
                for (float cometAngle : cometAngles) {
                    //2PI Radians = 0 Radians
                    if ((Math.abs(cometAngle - angle) < increment - .01) || Math.abs(cometAngle - angle) > 2 * Math.PI - increment - .01) {
                        return false;
                    }
                }
                if (comet.cometPath.comets.size == 1) {
                    cometManager.cometPaths.removeValue(comet.cometPath, true);
                } else {
                    comet.cometPath.removeComet(comet);
                }
                cometPath.addComet(comet);
                //Calculate a closer position to snap to path
                Vector2 newPosition = calculateClosestPositionOnPath(x, y, planetX, planetY, cometPath.radius);
                cometManager.moveSpaceObject(comet, newPosition.x, newPosition.y);
                comet.startingX = newPosition.x - GameCore.screenWidth/2;
                comet.startingY = newPosition.y;
                return true;
            }
        }
        return false;
    }

    public static Vector2 calculateClosestPositionOnPath(float x, float y, float centerX, float centerY, float radius) {
        double vX = x - centerX;
        double vY = y - centerY;
        double magV = Math.sqrt(vX * vX + vY * vY);
        float aX = (float) (centerX + vX / magV * radius);
        float aY = (float) (centerY + vY / magV * radius);
        return new Vector2(aX, aY);
    }

    public static boolean validateCometPathPosition(GameCore gameCore, CometManager cometManager, float pathRadius, CometPath cometPath) {
        float x = cometManager.planet.getXinPixels();
        float y = cometManager.planet.getYinPixels();
        if (pathRadius < cometManager.planet.getRadius() + cometSpace) {
            return false;
        }
        for (CometPath cometPath2 : cometManager.cometPaths) {
            if (cometPath2 != cometPath && Math.abs(cometPath2.radius - pathRadius) < cometSpace) { //temp using cometSpace instead of pathPad
                return false;
            }
        }
        if (overlapPath(x, y, pathRadius, gameCore.portal) || overlapPath(x, y, pathRadius, gameCore.ship)) {
            return false;
        }
        for (SpaceObject planet2: gameCore.planetManager.spaceObjects) {
            if (cometManager.planet != planet2 && overlapPath(x, y, pathRadius, planet2)) {
                return false;
            }
        }
        if (gameCore.wormholeManager != null) {
            for (Wormhole wormhole : gameCore.wormholeManager.wormholes) {
                if (overlapPath(x, y, pathRadius, wormhole)) {
                    return false;
                }
            }
        }
        return true;
    }

    //Returns Vector2 of new position. <-9999, -9999> if none available
    public static Vector2 findAvailablePositionOnCometPath(Array<Comet> comets, SpaceObject planet, float pathRadius) {
        float planetX = planet.getXinPixels();
        float planetY = planet.getYinPixels();
        List<Float> cometAngles = new ArrayList<>();
        for (Comet comet : comets) {
            cometAngles.add(Utility.angle(planetX, planetY, comet.getXinPixels(), comet.getYinPixels()));
        }
        float increment = (float) (2*Math.PI/16);
        float angle = 0;
        while (angle < 2*Math.PI) {
            boolean noConflicts = true;
            for (float cometAngle : cometAngles) {
                if (Math.abs(cometAngle - angle) < increment - .01) {
                    noConflicts = false;
                }
            }
            if (noConflicts) {
                float x = (float) (planetX + pathRadius * Math.cos(angle));
                float y = (float) (planetY + pathRadius * Math.sin(angle));
                return new Vector2(x, y);
            }
            angle += increment;
        }
        return new Vector2(-9999, -9999);
        //x = originX + r * cos(a)
        //y = originY + r * sin(a)
    }

    public static int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    public static float midPoint(float c1, float c2){
        return (c1 + c2)/2;
    }

    /**
     * Following ship coordinate functions are in pixels
     */
    public static float getShipBottomMiddleX(Ship ship) {
        return Utility.midPoint(ship.sprite.getVertices()[SpriteBatch.X1], ship.sprite.getVertices()[SpriteBatch.X4]);
    }

    public static float getShipTopMiddleX(Ship ship) {
        return Utility.midPoint(ship.sprite.getVertices()[SpriteBatch.X2], ship.sprite.getVertices()[SpriteBatch.X3]);
    }

    public static float getShipBottomMiddleY(Ship ship) {
        return Utility.midPoint(ship.sprite.getVertices()[SpriteBatch.Y1], ship.sprite.getVertices()[SpriteBatch.Y4]);
    }

    public static float getShipTopMiddleY(Ship ship) {
        return Utility.midPoint(ship.sprite.getVertices()[SpriteBatch.Y2], ship.sprite.getVertices()[SpriteBatch.Y3]);
    }

    public static float getShipMiddleX(Ship ship) {
        return Utility.midPoint(getShipBottomMiddleX(ship), getShipTopMiddleX(ship));
    }

    public static float getShipMiddleY(Ship ship) {
        return Utility.midPoint(getShipBottomMiddleY(ship), getShipTopMiddleY(ship));
    }

    public static float getShipXDiff(Ship ship) {
        return ship.sprite.getVertices()[SpriteBatch.X2] - ship.sprite.getVertices()[SpriteBatch.X1];
    }

    public static float getShipXHorizontalDiff(Ship ship) {
        return ship.sprite.getVertices()[SpriteBatch.X4] - ship.sprite.getVertices()[SpriteBatch.X1];
    }

    public static float getShipYDiff(Ship ship) {
        return ship.sprite.getVertices()[SpriteBatch.Y2] - ship.sprite.getVertices()[SpriteBatch.Y1];
    }

    public static float getShipYHorizontalDiff(Ship ship) {
        return ship.sprite.getVertices()[SpriteBatch.Y4] - ship.sprite.getVertices()[SpriteBatch.Y1];
    }

    /**
     * Take in screen width and height (Gdx.graphics.get...) and use to calculate
     * full screen background when using a fitViewport
     * Change max to min and you will calculate dimensions to fit on viewport only
     */
    /*public static void setBackgroundDimensionsForFitViewport(float width, float height) {
        float widthScale = width / MODEL_SCREEN_WIDTH;
        float heightScale = height / MODEL_SCREEN_HEIGHT;
        float scale = Math.max(widthScale, heightScale);
        float newWidth = scale * MODEL_SCREEN_WIDTH;
        float newHeight = scale * MODEL_SCREEN_HEIGHT;
        float widthOffset = (width - newWidth) / 2;
        float heightOffset = (height - newHeight) / 2;
        backgroundX1 = widthOffset;
        backgroundY1 = heightOffset;
        backgroundX2 = backgroundX1 + newWidth;
        backgroundY2 = backgroundY1 + newHeight;
    }*/

    /**
     * Take in screen width and height (Gdx.graphics.get...) and use to calculate
     * full screen background when using a fitViewport
     * Change max to min and you will calculate dimensions to fit on viewport only
     */
    public static void setBackgroundDimensionsForFitViewport(float width, float height) {
        //Gdx.app.log("setBackgroundDimensionsForFitViewport", "width, height: " + width + ", " + height);
        /*float widthScale = width / MODEL_SCREEN_WIDTH;
        float heightScale = height / MODEL_SCREEN_HEIGHT;*/
        float widthScale = MODEL_SCREEN_WIDTH / width;
        float heightScale = MODEL_SCREEN_HEIGHT / height;
        float scale = Math.max(widthScale, heightScale);
        /*float newWidth = scale * MODEL_SCREEN_WIDTH;
        float newHeight = scale * MODEL_SCREEN_HEIGHT;*/
        /*float newWidth = scale * width;
        float newHeight = scale * height;*/
        float newWidth = scale * MODEL_SCREEN_WIDTH;
        float newHeight = scale * MODEL_SCREEN_HEIGHT;
        float widthOffset = (width - newWidth) / 2;
        float heightOffset = (height - newHeight) / 2;
        backgroundX = widthOffset;
        backgroundY = heightOffset;
        backgroundWidth = newWidth;
        backgroundHeight = newHeight;
        //Gdx.app.log("setBackgroundDimensionsForFitViewport", "backgroundX: " + backgroundX);
        //Gdx.app.log("setBackgroundDimensionsForFitViewport", "backgroundY: " + backgroundY);
        //Gdx.app.log("setBackgroundDimensionsForFitViewport", "backgroundWidth: " + backgroundWidth);
        //Gdx.app.log("setBackgroundDimensionsForFitViewport", "backgroundHeight: " + backgroundHeight);
    }

    /**
     * Going much above 120-130 in size will cause problems
     */
    private static BitmapFont generateFont(FreeTypeFontGenerator generator, FreeTypeFontGenerator.FreeTypeFontParameter parameter, int size, int borderWidth, int borderGamma, Color color, Color borderColor) {
        parameter.size = size;
        parameter.borderWidth = borderWidth;
        parameter.borderGamma = borderGamma;
        parameter.borderColor = borderColor;
        parameter.color = color;
        return generator.generateFont(parameter);
    }

    private static BitmapFont generateFont(FreeTypeFontGenerator generator, FreeTypeFontGenerator.FreeTypeFontParameter parameter, int size, Color color) {
        parameter.size = size;
        parameter.color = color;
        return generator.generateFont(parameter);
    }

    /**
     * Generate all fonts needed
     */
    public static void generateFonts(FreeTypeFontGenerator generator, FreeTypeFontGenerator.FreeTypeFontParameter parameter) {
        font_112_border = Utility.generateFont(generator, parameter, 112, 5, 15, Color.BLACK, Color.WHITE);
        font_70_border = Utility.generateFont(generator, parameter, 70, 5, 15, Color.BLACK, Color.WHITE);
        font_50_border = Utility.generateFont(generator, parameter, 50, 5, 15, Color.BLACK, Color.WHITE);
        font_30_border = Utility.generateFont(generator, parameter, 30, 1, 1, Color.BLACK, Color.WHITE);
        //titleFont = Utility.generateFont(156, 5, 15, Color.BLACK, Color.WHITE);
        font_140 = Utility.generateFont(generator, parameter, 130, Color.WHITE);
        //140 works for all but timing is right
        //135 works for none
        //120 works for all
        //130 works for all

        font_70 = Utility.generateFont(generator, parameter, 70, Color.WHITE);
        font_50 = Utility.generateFont(generator, parameter, 50, Color.WHITE);
        /*font_112_border = new BitmapFont();
        font_70_border = font_112_border;
        font_50_border = font_112_border;
        font_30_border = font_112_border;
        font_140 = font_112_border;*/
    }

    public static void switchScreens(EnumScreen currentScreen, EnumScreen newScreen) {
        ScreenManager.getInstance().dispose(currentScreen);
        ScreenManager.getInstance().show(newScreen);
    }

    public static void reloadScreen(EnumScreen screen) {
        ScreenManager.getInstance().dispose(screen);
        ScreenManager.getInstance().show(screen);
    }

    public static Vector3 project(float x, float y, float z) {
        return GameCore.camera.project(new Vector3(x, y, z));
    }

    public static Vector2 project(float x, float y) {
        Vector3 vector3 = GameCore.camera.project(new Vector3(x, y, 0));
        return new Vector2(vector3.x, vector3.y);
    }

    //TODO: Refactor these to take in a camera instead of using gamecore?
    public static float projectX(float x) {
        return GameCore.camera.project(new Vector3(x, 0, 0)).x;
    }

    public static float projectY(float y) {
        return GameCore.camera.project(new Vector3(0, y, 0)).y;
    }

    public static float projectZ(float z) {
        return GameCore.camera.project(new Vector3(0, 0, z)).z;
    }

    public static Vector3 unproject(float x, float y, float z) {
        return GameCore.camera.unproject(new Vector3(x, y, z));
    }

    public static Vector2 unproject(float x, float y) {
        Vector3 vector3 = GameCore.camera.unproject(new Vector3(x, y, 0));
        return new Vector2(vector3.x, vector3.y);
    }

    public static float unprojectX(float x) {
        return GameCore.camera.unproject(new Vector3(x, 0, 0)).x;
    }

    public static float unprojectY(float y) {
        return GameCore.camera.unproject(new Vector3(0, y, 0)).y;
    }

    public static float unprojectZ(float z) {
        return GameCore.camera.unproject(new Vector3(0, 0, z)).z;
    }
}

