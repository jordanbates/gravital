package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jordan on 9/3/2015.
 * Modified from https://code.google.com/p/steigert-libgdx/source/browse/trunk/tyrian-game/src/com/blogspot/steigert/tyrian/domain/Profile.java?spec=svn18&r=19
 * Player profile
 */
public class Profile implements Serializable {
    private int currentLevelId;
    private int credits;
    private int create;
    private int warpedWormholes;
    private int theTimingIsRight;
    private int stars;
    protected boolean promptRating;
    protected boolean showAds;
    protected boolean haveShownAdMessage;
    private int logins;
    private boolean tutorialComplete, createTutorialComplete;
    private int drawerHintsCompleted;
    private Map<Integer, Integer> highScores = new HashMap<>();
    //private Ship ship;
    public Map<String, Integer> existingLevels = new HashMap<>(); //<level pack, highest existing level>
    //public Map<String, String> existingLevels = new HashMap<>(); //<level pack, highest existing level>
    public Map<String, Integer> unlockedLevels = new HashMap<>();// = new HashMap<String, Integer>(); //<level pack, highest unlocked level>
    public ArrayList<String> myLevelsNames;
    private final boolean SHOW_ADS_DEFAULT = true;

    public Profile() {
        //TODO: update to use the constants from Utility
        existingLevels.put(Utility.levelPacks.get(0), 50);
        existingLevels.put(Utility.levelPacks.get(1), 50);
        existingLevels.put(Utility.levelPacks.get(2), 50);
        existingLevels.put(Utility.levelPacks.get(3), 50);
        existingLevels.put(Utility.levelPacks.get(4), 50);
        existingLevels.put(Utility.levelPacks.get(5), 0);

        //TODO: Change these as needed. Need to make sure my levels never is locked
        unlockedLevels.put(Utility.levelPacks.get(0), 1);
        unlockedLevels.put(Utility.levelPacks.get(1), 1);
        unlockedLevels.put(Utility.levelPacks.get(2), 1);
        unlockedLevels.put(Utility.levelPacks.get(3), 1);
        unlockedLevels.put(Utility.levelPacks.get(4), 1);
        unlockedLevels.put(Utility.levelPacks.get(5), 0);

        stars = 0;
        create = 0;
        warpedWormholes = 0;
        theTimingIsRight = 0;
        tutorialComplete = false;
        createTutorialComplete = false;
        drawerHintsCompleted = 0;
        logins = 0;
        promptRating = true;
        myLevelsNames = new ArrayList<>();
        showAds = SHOW_ADS_DEFAULT;
        haveShownAdMessage = false;
    }

    // Take in a levelPack and a level
    // If needed, increment unlocked levels and increment stars
    public void setUnlockedLevel(String levelPack, int level) {
        //Gdx.app.log("Profile", "levelPack: " + levelPack);
        //Gdx.app.log("Profile", "unlockedLevels: " + unlockedLevels);
        if (unlockedLevels.containsKey(levelPack) && existingLevels.containsKey(levelPack)) {
            //Gdx.app.log("Profile", "contains key");
            int unlocked = unlockedLevels.get(levelPack);
            int existing = existingLevels.get(levelPack);
            if (level > unlocked && level <= existing) {
                if (level - unlocked > 1) {
                    //TODO: Error
                    //Gdx.app.log("Profile", "Error 1 in Profile!");
                } else {
                    //Have unlocked a new level
                    unlockedLevels.put(levelPack, level);
                    stars++;
                }
            } else {
                //TODO: Error
                //Gdx.app.log("Profile", "Error 2 in Profile!");
            }
        } else {
            //TODO: Error
            //Note: this happens in create
            //Gdx.app.log("Profile", "Error 3 in Profile!");
        }
    }

    //Works for either creating or renaming
    public void saveUserLevel(int index, String name) {
        //Gdx.app.log("Profile.saveUserLevel()", "Entering saveUserLevel");
        ////Gdx.app.log("Profile.saveUserLevel()", "myLevelsNames contents: " + myLevelsNames);
        ////Gdx.app.log("Profile.saveUserLevel()", "existingLevels.myLevels: " + Main.profile.existingLevels.get("My Levels"));
        ////Gdx.app.log("Profile.saveUserLevel()", "Index: " + index + ", name: " + name);
        index -= 1;
        ////Gdx.app.log("Profile.saveUserLevel()", "adjusted index: " + index);
        if (index < myLevelsNames.size()) {
            ////Gdx.app.log("Profile.saveUserLevel()", "renaming level. Previous: " + myLevelsNames.get(index) + ", New: " + name);
            String a = myLevelsNames.set(index, name);
            ////Gdx.app.log("Profile.saveUserLevel()", "confirm previous: " + a);
        }
        else {
            ////Gdx.app.log("Profile.saveUserLevel()", "Creating a new level. Name: " + name);
            myLevelsNames.add(name);
        }
        ////Gdx.app.log("Profile.saveUserLevel()", "myLevelsNames contents: " + myLevelsNames);
        ////Gdx.app.log("Profile.saveUserLevel()", "Exiting saveUserLevel");
    }

    public void deleteUserLevel(int index) {
        //Gdx.app.log("Profile.deleteUserLevel()", "Entering deleteUserLevel");
        index -= 1;
        // This uses set instead of remove so that indices of other values do not change
        // They must remain consistent with the levels in each loadedLevel
        String a = myLevelsNames.set(index, null);
    }

    public String getUserLevelName(int index) {
        return myLevelsNames.get(index - 1);
    }

    public boolean isTutorialComplete() {
        return tutorialComplete;
    }

    public void setTutorialComplete() {
        tutorialComplete = true;
    }

    public boolean isCreateTutorialComplete() {
        return createTutorialComplete;
    }

    public void setCreateTutorialComplete() {
        createTutorialComplete = true;
    }

    public boolean isDrawerHintsComplete() {
        return drawerHintsCompleted >= 5;
    }

    public void drawerHintComplete() {
        drawerHintsCompleted++;
    }

    public void drawerClicked() {
        drawerHintsCompleted = 5;
    }

    public boolean getCreateStatus() {
        return create == 105897;
    }

    public boolean getWarpedWormholesStatus() {
        return warpedWormholes == 318495;
    }

    public boolean getTheTimingIsRightStatus() {
        return theTimingIsRight == 445983;
    }

    /*public boolean getCreateStatus() {
        return true;
    }

    public boolean getWarpedWormholesStatus() {
        return true;
    }

    public boolean getTheTimingIsRightStatus() {
        return true;
    }*/

    public void setCreateStatus(boolean status) {
        if (status) {
            create = 105897;
            showAds = false;
        }
        else {
            create = 0;
        }
    }

    public void setWarpedWormholesStatus(boolean status) {
        if (status) {
            warpedWormholes = 318495;
            showAds = false;
        }
        else {
            warpedWormholes = 0;
        }
    }

    public void setTheTimingIsRightStatus(boolean status) {
        if (status) {
            theTimingIsRight = 445983;
            showAds = false;
        }
        else {
            theTimingIsRight = 0;
        }
    }

    public int getStars() {
        return stars;
    }

    public void login() {
        logins++;
    }

    public int getLogins() {
        return logins;
    }

    // Serializable implementation
    @SuppressWarnings("unchecked")
    @Override
    //public void read(Json json, OrderedMap<String, Object> jsonData) {
    public void read(Json json, JsonValue jsonData) {
        currentLevelId = json.readValue("currentLevelId", Integer.class, jsonData);
        credits = json.readValue("credits", Integer.class, jsonData);
        stars = json.readValue("stars", Integer.class, jsonData);
        tutorialComplete = json.readValue("tutorialComplete", Boolean.class, jsonData);
        createTutorialComplete = json.readValue("createTutorialComplete", Boolean.class, jsonData);
        drawerHintsCompleted = json.readValue("drawerHintsCompleted", Integer.class, jsonData);
        try {
            myLevelsNames = json.readValue("myLevelsNames", ArrayList.class, jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (myLevelsNames == null) {
            ////Gdx.app.log("Profile.read()", "Loading - myLevelsNames is null. creating new Arraylist");
            myLevelsNames = new ArrayList<>();
        }

        try {
            logins = json.readValue("logins", Integer.class, jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            logins = 0;
        }

        try {
            promptRating = json.readValue("promptRating", Boolean.class, jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            promptRating = true;
        }

        try {
            warpedWormholes = json.readValue("warpedWormholes", Integer.class, jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            warpedWormholes = 0;
        }

        try {
            theTimingIsRight = json.readValue("theTimingIsRight", Integer.class, jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            theTimingIsRight = 0;
        }

        try {
            showAds = json.readValue("showAds", Boolean.class, jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            showAds = SHOW_ADS_DEFAULT;
        }

        try {
            haveShownAdMessage = json.readValue("haveShownAdMessage", Boolean.class, jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            haveShownAdMessage = false;
        }

        // libgdx handles the keys of JSON formatted HashMaps as Strings,
        // but we want it to be an integer instead (levelId)
        Map<String, Integer> highScores = json.readValue("highScores", HashMap.class, Integer.class, jsonData);
        Map<String, Integer> tempExistingLevels = json.readValue("existingLevels", HashMap.class, Integer.class, jsonData);
        Map<String, Integer> tempUnlockedLevels = json.readValue("unlockedLevels", HashMap.class, Integer.class, jsonData);
        existingLevels = tempExistingLevels == null ? existingLevels : tempExistingLevels;
        unlockedLevels = tempUnlockedLevels == null ? unlockedLevels : tempUnlockedLevels;
        for (String levelIdAsString : highScores.keySet()) {
            int levelId = Integer.valueOf(levelIdAsString);
            Integer highScore = highScores.get(levelIdAsString);
            this.highScores.put(levelId, highScore);
        }

        //existingLevels.put("My Levels", 0); //TODO: DELETE
        ////Gdx.app.log("Profile.read()", "existingLevels.myLevels: " + existingLevels.get("My Levels"));
    }

    @Override
    public void write(Json json) {
        json.writeValue("currentLevelId", currentLevelId);
        json.writeValue("credits", credits);
        json.writeValue("stars", stars);
        json.writeValue("highScores", highScores);
        json.writeValue("existingLevels", existingLevels);
        json.writeValue("unlockedLevels", unlockedLevels);
        json.writeValue("create", create);
        json.writeValue("warpedWormholes", warpedWormholes);
        json.writeValue("theTimingIsRight", theTimingIsRight);
        json.writeValue("tutorialComplete", tutorialComplete);
        json.writeValue("createTutorialComplete", createTutorialComplete);
        json.writeValue("drawerHintsCompleted", drawerHintsCompleted);
        json.writeValue("myLevelsNames", myLevelsNames);
        json.writeValue("logins", logins);
        json.writeValue("promptRating", promptRating);
        json.writeValue("showAds", showAds);
        json.writeValue("haveShownAdMessage", haveShownAdMessage);
    }
}
