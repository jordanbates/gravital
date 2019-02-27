package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

/**
 * Created by Jordan on 11/3/2016.
 * Tell user that any purchase removes ads
 */
public class DialogAdMessage extends Dialog {
    String dialogText = "Any purchase made \n will remove ads.";
    Stage stage;
    GameCore gameCore;

    public DialogAdMessage (GameCore gameCore) {
        super("", Main.getInstance().skin);
        stage = gameCore.HUDStage;
        this.gameCore = gameCore;
        setDialog();
    }

    private void setDialog() {
        text(dialogText);
        getButtonTable().defaults().size(270, 120);
        getButtonTable().defaults().pad(70);
        button("Got it!", "Got it!");

        setModal(true);
        setMovable(false);
        setWidth(Utility.MODEL_SCREEN_WIDTH - 200);
        setHeight(500);
        setPosition(stage.getViewport().getWorldWidth() / 2 - getWidth() / 2, stage.getViewport().getWorldHeight() - 1200);
    }

    @Override
    protected void result(Object object) {
        Gdx.input.setOnscreenKeyboardVisible(false);
    }
}