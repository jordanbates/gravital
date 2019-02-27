package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.Objects;

public class MenuLevels extends ActorGestureListener implements Screen {
    private Stage stage;
    private ScrollPane scroll;
    //private TextureRegionDrawable background;
    private boolean displayText = false;
    private BitmapFont levelMessageFont;
    private String levelMessageText;
    private Vector2 levelMessagePosition;
    private float levelMessageWidth;
    private Batch batch;
    public Main main;
    //private BitmapFont levelFontLarge, levelFontMediumLarge, levelFontMedium, levelFontSmall, titleFont;

    public MenuLevels() {
        main = Main.getInstance();
    }

    public void render (float delta) {
        //Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        /*if (displayText) {
            levelMessageFont.draw(batch, levelMessageText, levelMessagePosition.x, levelMessagePosition.y,
                    levelMessageWidth, Align.center, true);
        }*/
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose () {
        stage.dispose();
    }

    @Override
    public void show() {
        if (main.levelPack.equals("Edit My Levels")) main.levelPack = "My Levels";
        stage = new Stage(new ExtendViewport(Utility.MODEL_SCREEN_WIDTH, Utility.MODEL_SCREEN_HEIGHT));
        setInputMultiplexer();

        /*if (main.levelPack.equals("My Levels")) {
            //Gdx.app.log("MenuLevels.show()", "existing levels: " + main.profile.existingLevels.get("My Levels"));
        }*/

        //background = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("home/background.png", Texture.class)));
        //generateFonts();

        Table container = new Table();
        stage.addActor(container);
        container.setFillParent(true);
        final Table table = new Table();
        table.padTop(Utility.MODEL_SCREEN_WIDTH * .074f).padBottom(Utility.MODEL_SCREEN_WIDTH * .0185f).defaults().expandX();//.space(4);  //TEMP
        TextureRegionDrawable backButtonDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("buttons/back.png")));
        Button backButton = new Button(backButtonDrawable);
        float width = 100; //75 //TODO: not sure if this is working
        //float height = (width * backButton.getHeight()) / backButton.getWidth();
        float height = 150;
        table.add(backButton).colspan(2).width(width).height(height).padTop(0).padBottom(Utility.MODEL_SCREEN_WIDTH * .0185f).left().padLeft(Utility.MODEL_SCREEN_WIDTH * .037f);
        table.row();
        scroll = new ScrollPane(table, main.skin);
        scroll.layout(); //seeing if this fixes anything

        //BitmapFont font = Utility.generateFont(150, 0, 0, Color.WHITE, Color.BLACK);
        TextActor title = new TextActor(Utility.font_140, main.levelPack, stage, 290);
        title.setScroll(scroll);
        //table.add(title).width(stage.getViewport().getWorldWidth()).height(200);
        //table.row();
        //container.add(title).width(stage.getViewport().getWorldWidth()).height(200).row();
        table.add(title).colspan(table.getColumns()).center().height(400).row();

        backButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                levelPackMenu();
            }
        });

        //TODO: Same?.. unlocked should be changed
        final int levelsSize = main.getHighestLevel();
        final int unlockedLevelsSize = main.profile.unlockedLevels.get(main.levelPack);

        if (Objects.equals(main.levelPack, "My Levels")) {
            if (levelsSize == 0) {
                title.setScroll(null); //If no levels, don't want title to scroll

                String text = "Your levels which you've created in Creation Mode will be displayed here";
                TextActor note = new TextActor(Utility.font_70, text, stage, 1000);
                note.setDrawWidth(stage.getViewport().getWorldWidth() - 300);
                table.add(note).colspan(table.getColumns()).center().height(1000).row();
            }
            else {
                String text = "Hold down on level buttons for more options";
                TextActor note = new TextActor(Utility.font_50, text, stage, 500);
                note.setDrawWidth(stage.getViewport().getWorldWidth() - 300);
                note.setScroll(scroll);
                table.add(note).colspan(table.getColumns()).center().height(20).row();
            }
        }

        int buttonsPerRow = 4;
        float screenPad = stage.getViewport().getWorldWidth() * .15f;
        float buttonDiameter = (stage.getViewport().getWorldWidth() - screenPad) / buttonsPerRow;
        int randomConstantNeededForReasonsBeyondMe = 5;
        float buttonPad = (screenPad / buttonsPerRow) / randomConstantNeededForReasonsBeyondMe;

        boolean userMade = main.levelPack.equals("My Levels");
        boolean row = false;
        int existingIndex = 0; //To keep track of positioning when some My Levels buttons don't exist anymore
        for(int i = 0; i < levelsSize; i++) {
            //try {
            existingIndex++;
            final int level = i + 1;
            //float duration = 0.1f * (i*3);
            //if (level % buttonsPerRow == 0) {
            if (existingIndex % buttonsPerRow == 0) {
                row = true;
            }

            main.level = level; //change to parameter
            String name;

            if (userMade) {
                name = main.profile.getUserLevelName(level);
                if (name == null) {
                    existingIndex--;
                    continue;
                }
            } else {
                name = Integer.toString(level);
            }

            boolean disabled = false;
            TextButton.TextButtonStyle buttonStyle;
            //Assuming the last level pack is always My Levels
            if (level <= unlockedLevelsSize || main.levelPack.equals(Utility.levelPacks.get(Utility.levelPacks.size()-1))) {
                buttonStyle = main.skin.get("planet_variable_font", TextButton.TextButtonStyle.class);
                int nameLength = name.length();
                if (nameLength < 3) {
                    buttonStyle.font = Utility.font_112_border;
                } else if (nameLength < 5) {
                    buttonStyle.font = Utility.font_70_border;
                } else if (nameLength < 10) {
                    buttonStyle.font = Utility.font_50_border;
                } else {
                    buttonStyle.font = Utility.font_30_border;
                }
            } else {
                buttonStyle = main.skin.get("locked_planet", TextButton.TextButtonStyle.class);
                disabled = true;
            }
            if (disabled) {
                name = "";
            }
            TextButton levelButton = new TextButton(name, buttonStyle);
            levelButton.setDisabled(disabled);
            levelButton.getLabel().setWrap(true);
            if (!disabled) {
                //levelButton.getLabel().setFontScale(2);
                final String finalName = name;
                levelButton.addListener(new ActorGestureListener() {

                    public boolean longPress(Actor actor, float x, float y) {
                        System.out.println("long press " + x + ", " + y);
                        if (main.levelPack.equals("My Levels")) {
                            //levelButtonDialog.show(stage);
                            stage.addActor(new DialogLevelButton(level, stage));
                        }
                        return true;
                    }

                    public void tap(InputEvent event, float x, float y, int count, int button) {
                            /*if (level <= unlockedLevelsSize) {
                                main.level = level;
                                startGame();
                            } else {
                                Toast toast = new Toast("This level is locked", stage);
                                toast.startToast();
                            }*/
                        main.level = level;
                        main.levelName = finalName;
                        main.soundHandler.play();
                        startGame();
                    }
                });
            }
            //table.add(levelButton).width(500).height(400).padBottom(20).padTop(20);
            table.add(levelButton).width(buttonDiameter).height(buttonDiameter).pad(buttonPad);
            if (row) {
                table.row();
                row = false;
            }
            //buttons.add(levelButton);
            /*}
            catch (Exception e) {
                e.printStackTrace();
            }*/
        }
        //table.setBackground(background);
        //scroll.setColor(Color.WHITE);
        scroll.setFlickScroll(true);
        scroll.setOverscroll(false, true);
        scroll.layout(); //seeing if this fixes anything
        //scroll.addActor(backButton);
        //container.add(backButton).width(150).height(200).padTop(0).padBottom(0).left().padLeft(40);
        container.row();
        container.add(scroll).expand().fill().colspan(4);
        container.row().space(10).padBottom(Utility.MODEL_SCREEN_WIDTH * .009f);

        scroll.pack();
        container.pack();

        batch = stage.getBatch();
    }

    /**
     * Note: much higher than 120 will cause problems
     */
    /*public void generateFonts() {
        levelFontLarge = Utility.generateFont(112, 5, 15, Color.BLACK, Color.WHITE);
        levelFontMediumLarge = Utility.generateFont(70, 5, 15, Color.BLACK, Color.WHITE);
        levelFontMedium = Utility.generateFont(50, 5, 15, Color.BLACK, Color.WHITE);
        levelFontSmall = Utility.generateFont(30, 1, 1, Color.BLACK, Color.WHITE);
        //titleFont = Utility.generateFont(156, 5, 15, Color.BLACK, Color.WHITE);
        titleFont = Utility.generateFont(140, Color.WHITE);
    }*/

    private void setInputMultiplexer() {
        InputProcessor backProcessor = new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    levelPackMenu();
                }
                return true;
            }
        };

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(backProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private static void levelPackMenu() {
        Utility.switchScreens(EnumScreen.LEVELS_MENU, EnumScreen.LEVEL_PACK_MENU);
    }

    private static void startGame() {
        Utility.switchScreens(EnumScreen.LEVELS_MENU, EnumScreen.GAME_CORE);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
        //ScreenManager.getInstance().dispose(ScreenEnum.LEVELS_MENU);
        //ScreenManager.getInstance().dispose();
    }
}
