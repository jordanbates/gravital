package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

/**
 * Created by Jordan on 8/31/2016.
 * prompt user every so often to rate app
 */
public class DialogRatingPrompt extends Dialog {
    String dialogText, levelName;
    Stage stage;
    public Main main;

    public DialogRatingPrompt(Stage stage) {
        super("", Main.getInstance().skin);
        main = Main.getInstance();
        this.stage = stage;
        dialogText =    "   Please consider taking \n" +
                        "a moment to rate Gravital. \n\n" +
                        "             Thank you!";
        setDialog();
    }

    private void setDialog() {
        text(dialogText);
        getButtonTable().defaults().size(260, 120);
        getButtonTable().defaults().pad(15);
        getButtonTable().defaults().padBottom(55);
        button("Rate", "Rate");
        button("Not Now", "Not Now");
        button("Never", "Never");

        setModal(true);
        setMovable(false);
        setWidth(Utility.MODEL_SCREEN_WIDTH - 120);
        setHeight(600);
        setPosition(stage.getViewport().getWorldWidth() / 2 - getWidth() / 2, stage.getViewport().getWorldHeight() - 1150);
    }

    @Override
    protected void result(Object object) {
        Gdx.input.setOnscreenKeyboardVisible(false);
        String pressed = object.toString();
        switch (pressed) {
            case "Rate":
                main.profile.promptRating = false;
                Gdx.net.openURI("https://play.google.com/store/apps/details?id=com.everless.gravital");
                break;
            case "Never":
                main.profile.promptRating = false;
                break;
        }
    }
}
