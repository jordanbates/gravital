package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created by Jordan on 5/26/2016.
 * Save game
 */
class DialogSave extends Dialog {
    GameCore gameCore;
    String dialogText = "Save?";
    TextField levelNameTextField;
    String levelName;
    String messageText = "Enter a name";
    boolean exit;
    public Main main;

    public DialogSave(GameCore gameCore, boolean exitCreationMode) {
        super("", Main.getInstance().skin);
        main = Main.getInstance();
        this.gameCore = gameCore;
        gameCore.unselectAll();
        exit = exitCreationMode;
        setDialog();
    }

    private void setDialog() {
        text(dialogText);
        getButtonTable().defaults().size(210, 120);
        getButtonTable().defaults().pad(55);
        button("Save", "Save");
        button("Cancel", "Cancel");

        levelNameTextField = new TextField(main.levelName, main.skin);
        if (main.level == 0) {
            levelNameTextField.setMessageText(messageText);
        }
        levelNameTextField.setMaxLength(30);
        levelNameTextField.setPosition(-130, 270);
        levelNameTextField.setSize(900, 100);
        getButtonTable().addActor(levelNameTextField);

        setModal(true);
        setMovable(false);
        setWidth(Utility.MODEL_SCREEN_WIDTH - 50);
        setHeight(800);
        setPosition(GameCore.screenWidth / 2 - getWidth() / 2, GameCore.screenHeight - 1000);
    }

    @Override
    protected void result(Object object) {
        Gdx.input.setOnscreenKeyboardVisible(false);
        String pressed = object.toString();
        //Gdx.app.log("LevelButtonDialog", "result: " + pressed);
        switch (pressed) {
            case "Save":
                levelName = levelNameTextField.getText();
                if(levelName == null || levelName.isEmpty()) {
                    System.out.println("level name empty or null");
                    Toast toast = new Toast("Please give the level a name", gameCore.HUDStage, Toast.TOAST_MEDIUM, Toast.TOAST_SMALL);
                    toast.startToast();
                }
                else if (main.profile.myLevelsNames.contains(levelName)) {
                    gameCore.HUDStage.addActor(new DialogConfirmOverwrite(gameCore, levelName));
                }
                else {
                    gameCore.saveUserLevel(levelName);
                }
                if (exit) {
                    if (main.levelPack.equals("Edit My Levels")) {
                        gameCore.endAction = EnumEndAction.LEVELS_MENU;
                    }
                    else {
                        gameCore.endAction = EnumEndAction.MAIN_MENU;
                    }
                    gameCore.fadeOut();
                }
                else {
                    gameCore.unsavedChanges = false;
                    gameCore.resume(); //TODO: see if i need to move this to dialogConfirm
                }
        }
    }
}