package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created by Jordan on 5/26/2016.
 * Handles long press on user-made levels
 */
class DialogLevelButton extends Dialog {
    private int level;
    String dialogText = "Level Options";
    TextField levelNameTextField;
    String levelName;
    LoadedLevel loadedLevel;
    Stage stage;
    Main main;

    public DialogLevelButton(int level, Stage stage) {
        super("", Main.getInstance().skin);
        main = Main.getInstance();
        this.level = level;
        this.stage = stage;
        main.level = level;
        loadedLevel = Utility.readJSONLevel();
        levelName = main.profile.getUserLevelName(level);
        setDialog();
        //Gdx.app.log("DialogLevelButton", "loadedLevel: " + loadedLevel);
        //Gdx.app.log("DialogLevelButton", "level: " + level);
        //Gdx.app.log("DialogLevelButton", "main.getHighestLevel(): " + main.getHighestLevel());
        //Gdx.app.log("DialogLevelButton", "main.getHighestUnlockedLevel(): " + main.getHighestUnlockedLevel());
        //Gdx.app.log("DialogLevelButton", "main.profile.myLevelsNames.size(): " + main.profile.myLevelsNames.size());
    }

    private void setDialog() {
        text(dialogText);
        getButtonTable().defaults().size(210, 120);
        getButtonTable().defaults().pad(10);
        getButtonTable().defaults().padBottom(50);
        button("Edit", "Edit");
        button("Save", "Save");
        button("Delete", "Delete");
        button("Cancel", "Cancel");

        levelNameTextField = new TextField(levelName, main.skin);
        //levelNameTextField.setMessageText(String.valueOf(level)); //levelname?
        levelNameTextField.setMaxLength(30);
        levelNameTextField.setPosition(20, 250);
        levelNameTextField.setSize(900, 100);
        getButtonTable().addActor(levelNameTextField);

        setModal(true);
        setMovable(false);
        setWidth(Utility.MODEL_SCREEN_WIDTH - 50);
        setHeight(800);
        setPosition(stage.getViewport().getWorldWidth() / 2 - getWidth() / 2, stage.getViewport().getWorldHeight() - 1000);
    }

    @Override
    protected void result(Object object) {
        Gdx.input.setOnscreenKeyboardVisible(false);
        String pressed = object.toString();
        //Gdx.app.log("LevelButtonDialog", "result: " + pressed);
        switch (pressed) {
            case "Edit":
                main.level = level;
                main.levelPack = "Edit My Levels";
                main.levelName = levelName;
                Utility.saveJSONLevel(loadedLevel, main.levelPack);
                main.soundHandler.play();
                Utility.switchScreens(EnumScreen.LEVELS_MENU, EnumScreen.GAME_CORE);
                break;
            case "Save":
                if (!levelNameTextField.getText().equals(levelName)) {
                    loadedLevel = Utility.renameLevel(loadedLevel, levelNameTextField.getText());
                    Utility.saveJSONLevel(loadedLevel, main.levelPack);
                    Utility.reloadScreen(EnumScreen.LEVELS_MENU);
                }
                break;
            case "Delete":
                stage.addActor(new DialogConfirmDelete(stage, levelName, loadedLevel));
                //Utility.deleteLevel(loadedLevel);
                //Utility.reloadScreen(EnumScreen.LEVELS_MENU);
                //main.profile.existingLevels.put(main.levelPack, main.profile.existingLevels.get(main.levelPack) - 1);
                break;
        }
    }
}