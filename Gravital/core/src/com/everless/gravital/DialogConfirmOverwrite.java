package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

/**
 * Created by Jordan on 8/11/2016.
 * Confirm user wants to overwrite level
 */
public class DialogConfirmOverwrite extends Dialog {
    GameCore gameCore;
    String dialogText, levelName;

    public DialogConfirmOverwrite(GameCore gameCore, String levelName) {
        super("", Main.getInstance().skin);
        this.gameCore = gameCore;
        dialogText = "Are you sure you want to overwrite\n   " + levelName + "?";
        this.levelName = levelName;
        setDialog();
    }

    private void setDialog() {
        text(dialogText);
        getButtonTable().defaults().size(270, 120);
        getButtonTable().defaults().pad(55);
        button("Overwrite", "Save");
        button("Cancel", "Cancel");

        setModal(true);
        setMovable(false);
        setWidth(Utility.MODEL_SCREEN_WIDTH - 150);
        setHeight(600);
        setPosition(GameCore.screenWidth / 2 - getWidth() / 2, GameCore.screenHeight - 1200);
    }

    @Override
    protected void result(Object object) {
        Gdx.input.setOnscreenKeyboardVisible(false);
        String pressed = object.toString();
        switch (pressed) {
            case "Save":
                gameCore.saveUserLevel(levelName);
                break;
            case "Cancel":
                gameCore.HUDStage.addActor(new DialogSave(gameCore, false));
        }
    }
}