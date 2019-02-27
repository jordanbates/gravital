package com.everless.gravital;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Jordan on 5/26/2016.
 */
class DialogPause extends Dialog {
    private GameCore gameCore;
    public DialogPause(GameCore gameCore, String[] buttonTexts, Skin skin) {
        //super("Pause", "Game Paused", buttonTexts, skin);
        super("Pause", Main.getInstance().skin);
        this.gameCore = gameCore;
    }
    @Override
    protected void result(Object object) {
        if (gameCore.isGameStateRun()) {
            gameCore.gameState = EnumGameState.PAUSE;
        } else if (gameCore.isGameStatePause()) {
            gameCore.gameState = EnumGameState.RUN;
        } else if (gameCore.isGameStateCreateRun()) {
            gameCore.gameState = EnumGameState.CREATE_RUN_PAUSED;
        } else if (gameCore.isGameStateCreateRunPaused()) {
            gameCore.gameState = EnumGameState.CREATE_RUN;
        }
    }
}
