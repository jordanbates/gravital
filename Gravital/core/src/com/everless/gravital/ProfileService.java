package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Created by Jordan on 9/3/2015.
 * Modified from https://code.google.com/p/steigert-libgdx/source/browse/trunk/tyrian-game/src/com/blogspot/steigert/tyrian/services/ProfileService.java?r=19
 * Profile service
 */
public class ProfileService {
    //private static final String PROFILE_DATA_FILE = ".tyrian/profile-v1.json"; // the location of the profile data file
    private static final String PROFILE_DATA_FILE = "profile-v1.json";
    private Profile profile; //The loaded profile (may be null)

    /**
     * Retrieves the player's profile, creating one if needed.
     */
    public Profile retrieveProfile()
    {
        //Gdx.app.log("ProfileService.retrieveProfile()", "Retrieving profile" );

        if(profile != null) {
            return profile;
        }
        //FileHandle profileDataFile = Gdx.files.external( PROFILE_DATA_FILE );
        FileHandle profileDataFile = null;
        try {
            profileDataFile = Gdx.files.local(PROFILE_DATA_FILE);
        }
        catch (Exception ignored) {}
        Json json = new Json();

        if(profileDataFile != null && profileDataFile.exists()) {
            try {
                //String profileAsCode = profileDataFile.readString();
                //String profileAsText = Base64Coder.decodeString( profileAsCode );
                //profile = json.fromJson( Profile.class, profileAsText ); //restore the state
                profile = json.fromJson( Profile.class, profileDataFile.readString() ); //restore the state

                //String text = json.prettyPrint(profile);
                ////Gdx.app.log("ProfileService.retrieveProfile(): ", text); //PRETTYPRINT

            } catch( Exception e ) {
                //TODO: save corrupted profile
                //TODO: Give message to user
                Gdx.app.error("ProfileService.retrieveProfile()", "Unable to parse existing profile data file", e );
                //Recover by creating a fresh new profile data file; note that the player will lose all game progress
                profile = new Profile();
                persist( profile );
            }
        }
        else { // create a new profile data file
            //Gdx.app.log("ProfileService.retrieveProfile()", "Profile does not exist; creating new profile" );
            profile = new Profile();
            persist( profile );
        }
        return profile;
    }

    protected void persist(Profile profile)
    {
        //Gdx.app.log("ProfileService.persist()", "Persisting profile" );

        Json json = new Json();
        //If want to write to external, make sure to add permission
        //FileHandle profileDataFile = Gdx.files.external( PROFILE_DATA_FILE );
        FileHandle profileDataFile = Gdx.files.local(PROFILE_DATA_FILE);
        String profileAsText = json.toJson(profile);

        //String text = json.prettyPrint(profileAsText);
        ////Gdx.app.log("ProfileService.persist(): ", text); //PRETTYPRINT

        //String profileAsCode = Base64Coder.encodeString( profileAsText );
        //profileDataFile.writeString( profileAsCode, false );
        profileDataFile.writeString(profileAsText, false);
    }

    /**
     * Persists the player's profile. If no profile is available, this method does nothing.
     */
    public void persist()
    {
        if(profile != null) {
            persist(profile);
        }
    }

    public void createNewProfile() {
        //TODO: save corrupted profile
        profile = new Profile();
        persist( profile );
    }
}
