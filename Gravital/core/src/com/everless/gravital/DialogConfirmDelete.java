package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

/**
 * Created by Jordan on 8/14/2016.
 * Confirm that user wants to delete the level in question
 */
public class DialogConfirmDelete extends Dialog {
    String dialogText, levelName;
    Stage stage;
    LoadedLevel loadedLevel;

    public DialogConfirmDelete(Stage stage, String levelName, LoadedLevel loadedLevel) {
        super("", Main.getInstance().skin);
        this.stage = stage;
        this.loadedLevel = loadedLevel;
        dialogText = "Are you sure you want to delete\n   " + levelName + "?";
        this.levelName = levelName;
        setDialog();
    }

    private void setDialog() {
        text(dialogText);
        getButtonTable().defaults().size(270, 120);
        getButtonTable().defaults().pad(55);
        button("Delete", "Delete");
        button("Cancel", "Cancel");

        setModal(true);
        setMovable(false);
        setWidth(Utility.MODEL_SCREEN_WIDTH - 150);
        setHeight(600);
        setPosition(stage.getViewport().getWorldWidth() / 2 - getWidth() / 2, stage.getViewport().getWorldHeight() - 1200);
    }

    @Override
    protected void result(Object object) {
        Gdx.input.setOnscreenKeyboardVisible(false);
        String pressed = object.toString();
        switch (pressed) {
            case "Delete":
                Utility.deleteLevel(loadedLevel);
                Utility.reloadScreen(EnumScreen.LEVELS_MENU);
                break;
        }
    }
}
