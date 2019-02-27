package com.everless.gravital;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Stack;

/**
 * Created by Jordan on 7/27/2015.
 * Refactoring all content in Main to be instantiated
 * which will be passed between screens
 *
 * Uses Singleton pattern
 */
public class Main
{
    private static final Main instance = new Main();
    private Main() {}

    public static Main getInstance() {
        return instance;
    }

    public Game game;
    public AssetManager assetManager;
    public String levelPack;
    public int level;
    public String levelName;
    public Profile profile;
    protected ProfileService profileService = new ProfileService();
    public SoundHandler soundHandler = new SoundHandler();
    public boolean appStarted = false;
    public Skin skin;
    public Stack<Toast> delayedToasts = new Stack<>();
    public boolean promptedRate = false;
    public IabInterface iabInterface;
    public AdInterface adInterface;
    public FreeTypeFontGenerator generator;
    public FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    public boolean adLoaded = false;
    protected int sessionShipLaunches = 0;
    protected boolean playVideo = false;
    protected boolean playVideoMode = false;

    public void create(Game game, IabInterface iabInterface, AdInterface adInterface) {
        this.game = game;
        this.iabInterface = iabInterface;
        this.adInterface = adInterface;
        iabInterface.processPurchases();

        ScreenManager.getInstance().initialize(game);

        retrieveProfile();
        assetManager = new AssetManager();
        createFonts();
        Gdx.input.setCatchBackKey(true);
    }

    public void createFonts() {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/GOTHIC.TTF"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        Utility.generateFonts(generator, parameter);
    }

    public void loadGameSounds() {
        soundHandler.loadGameAssets();
    }

    public void postSplashScreenLoading() {
        profile.login();
        soundHandler.soundOn();
    }

    public void dispose() {
        generator.dispose();
        assetManager.dispose();
        ScreenManager.getInstance().dispose();
    }

    public boolean showAd() {
        int completedLevels = profile.getStars();
        return completedLevels > 7;
    }

    public void retrieveProfile() {
        profile = profileService.retrieveProfile();
    }

    public void persistProfile() {
        profileService.persist(profile);
    }

    public void persistProfile(Profile profile) {
        profileService.persist(profile);
    }

    public int getHighestLevel() {
        return profile.existingLevels.get(levelPack);
    }

    public int getHighestLevel(String levelPack) {
        try {
            return profile.existingLevels.get(levelPack);
        }
        catch (Exception e) {
            profileService.createNewProfile();
            retrieveProfile();
            return 5;
        }
    }

    public int getHighestUnlockedLevel() {
        return profile.unlockedLevels.get(levelPack);
    }
}
