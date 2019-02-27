package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

/**
 * Created by Jordan on 9/19/2016.
 * Prompt for user purchases
 */
public class DialogPurchasePrompt extends Dialog {
    String dialogText;
    Stage stage;
    String item;
    Main main;
    boolean create = false;
    String levelPackName;

    public DialogPurchasePrompt(Stage stage, String purchaseItem, String levelPackName) {
        super("", Main.getInstance().skin);
        main = Main.getInstance();
        this.stage = stage;
        this.levelPackName = levelPackName;
        item = purchaseItem;
        create = purchaseItem.equals("Creation Mode");
        if (create) {
            dialogText = "Would you like to purchase \n access to " + purchaseItem + "?" +
                    "\n\n Creation mode allows the ability " +
                    "\n      to create and save levels.";
        }
        else {
            dialogText = "Would you like to purchase \n     access to the " + purchaseItem + "level pack?";
        }
        setDialog();
    }

    private void setDialog() {
        //text(dialogText);
        if (create) {
            text("Would you like to purchase " +
                    "\n access to " + item + "?");
            getContentTable().row();
            text("\n Creation mode allows the ability " +
                    "\nto create, save, and replay levels.");
        }
        else {
            text("\n      " + item + "\n contains 50 unique levels. ");
            getContentTable().row();
            text("\n  Would you like to purchase " +
                    "\naccess to " + item + "?");
            getContentTable().row();
            text("\nOR");
            getContentTable().row();
            text("\nPlays levels for free with ads.");
        }
        //Defaults for 3 buttons
        /*getButtonTable().defaults().size(300, 120);
        getButtonTable().defaults().pad(15);
        getButtonTable().defaults().padBottom(55);*/
        getButtonTable().defaults().size(300, 120);
        getButtonTable().defaults().pad(55);
        if (!create) {
            getButtonTable().defaults().padLeft(10);
            getButtonTable().defaults().padRight(10);
        }
        button("Purchase", "Purchase");
        //button("View Store", "View Store");
        if (!create) {
            button("Play Free", "Play Free");
        }
        button("Cancel", "Cancel");

        setModal(true);
        setMovable(false);
        float height = create ? 700 : 1000;
        float width = create ? Utility.MODEL_SCREEN_WIDTH - 90 : Utility.MODEL_SCREEN_WIDTH - 20;
        setWidth(width);
        setHeight(height);
        setPosition(stage.getViewport().getWorldWidth() / 2 - getWidth() / 2, stage.getViewport().getWorldHeight() - 1200);
    }

    @Override
    protected void result(Object object) {
        Gdx.input.setOnscreenKeyboardVisible(false);
        String pressed = object.toString();
        String failText = "Failed to connect to Google Play Services. " +
                "Please check your internet connection and restart Gravital.";
        Toast fail = new Toast(failText, stage, Toast.TOAST_LONG, Toast.TOAST_LARGE);
        switch (pressed) {
            case "Purchase":
                switch (item) {
                    case "Creation Mode":
                        if (!main.iabInterface.purchaseCreate()) {
                            fail.startToast();
                        }
                        break;
                    case "Warped Wormholes":
                        if (!main.iabInterface.purchaseWarpedWormholes()) {
                            fail.startToast();
                        }
                        break;
                    case "The Timing Is Right":
                        if (!main.iabInterface.purchaseTheTimingIsRight()) {
                            fail.startToast();
                        }
                        break;
                }
            case "Play Free":
                main.playVideoMode = true;
                main.levelPack = levelPackName;
                main.soundHandler.play();
                Utility.switchScreens(EnumScreen.LEVEL_PACK_MENU, EnumScreen.LEVELS_MENU);
            /*case "View Store":
                stage.addActor(new DialogStore(stage));*/
        }
    }
}
