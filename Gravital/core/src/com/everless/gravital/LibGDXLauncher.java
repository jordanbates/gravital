package com.everless.gravital;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;

/**
 * Created by Jordan on 7/27/2015.
 * Starts LibGDX game
 */
public class LibGDXLauncher extends Game implements ApplicationListener
{
    public Main main;
    private IabInterface iabInterface;
    private AdInterface adInterface;

    public LibGDXLauncher(IabInterface iabInterface, AdInterface adInterface) {
        this.iabInterface = iabInterface;
        this.adInterface = adInterface;
        main = Main.getInstance();
        Utility.main = main;
    }

    @Override
    public void create() {
        main.create(this, iabInterface, adInterface);
        ScreenManager.getInstance().show(EnumScreen.SPLASH_SCREEN);
    }

    @Override
    public void dispose() {
        main.dispose();
        super.dispose();
    }
}
