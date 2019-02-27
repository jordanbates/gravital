package com.everless.gravital;

/**
 * Created by Jordan on 8/7/2015.
 * Screen enum to contain the different screens
 */
public enum EnumScreen {

    GAME_CORE {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
            return new GameCore();
        }
    },
    SPLASH_SCREEN {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
            return new SplashScreen();
        }
    },
    MAIN_MENU {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
            return new MenuMain();
        }
    },
    LEVELS_MENU {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
            return new MenuLevels();
        }
    },
    LEVEL_PACK_MENU {
        @Override
        protected com.badlogic.gdx.Screen getScreenInstance() {
            return new MenuLevelPack();
        }
    };

    //NOTE: Make sure to do proper disposal on all screens.... call dispose from hide??

    protected abstract com.badlogic.gdx.Screen getScreenInstance();
}