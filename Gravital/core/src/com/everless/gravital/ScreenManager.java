package com.everless.gravital;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by Jordan on 8/8/2015.
 * Screen Manager
 */
public final class ScreenManager {

    private static ScreenManager instance;

    private Game game;

    private IntMap<Screen> screens;

    private ScreenManager() {
        screens = new IntMap<Screen>();
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public void initialize(Game game) {
        this.game = game;
    }

    public void show(EnumScreen screen) {
        //dispose(screen);
        //game.setScreen(screen.getScreenInstance());

        Screen newScreen = screen.getScreenInstance();
        game.setScreen(newScreen);

        /*
        if (game == null) return;
        if (!screens.containsKey(screen.ordinal())) {
            screens.put(screen.ordinal(), screen.getScreenInstance());
        }
        else {
            //dispose(screen);
            //screens.put(screen.ordinal(), screen.getScreenInstance());
            //idk really. need to redesign this
        }*/
        //game.setScreen(screens.get(screen.ordinal()));
    }

    public void dispose(EnumScreen screen) {
        if (!screens.containsKey(screen.ordinal())) return;
        screens.remove(screen.ordinal()).dispose();
    }

    public void dispose() {
        for (com.badlogic.gdx.Screen screen : screens.values()) {
            screen.dispose();
        }
        screens.clear();
        instance = null;
    }
}