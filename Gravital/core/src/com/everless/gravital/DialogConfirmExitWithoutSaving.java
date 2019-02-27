package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

/**
 * Created by Jordan on 9/18/2016.
 * Confirm user wants to exit creation mode without saving
 */
public class DialogConfirmExitWithoutSaving extends Dialog {
    String dialogText;
    Stage stage;
    GameCore gameCore;

    public DialogConfirmExitWithoutSaving(GameCore gameCore) {
        super("", Main.getInstance().skin);
        gameCore.unselectAll();
        stage = gameCore.HUDStage;
        this.gameCore = gameCore;
        dialogText = "Are you sure you want to \n     exit without saving?";
        setDialog();
    }

    private void setDialog() {
        text(dialogText);
        getButtonTable().defaults().size(270, 120);
        getButtonTable().defaults().pad(55);
        button("Save", "Save");
        button("Exit", "Exit");

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
            case "Save":
                stage.addActor(new DialogSave(gameCore, true));
                break;
            case "Exit":
                if (Main.getInstance().levelPack.equals("Edit My Levels")) {
                    gameCore.endAction = EnumEndAction.LEVELS_MENU;
                }
                else {
                    gameCore.endAction = EnumEndAction.MAIN_MENU;
                }
                gameCore.fadeOut();
                break;
        }
    }
}
