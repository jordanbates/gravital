package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * Created by Jordan on 7/27/2015.
 * Main Menu
 */
public class MenuMain implements Screen {
    private Stage stage;
    private Texture background;
    private BitmapFont fontStars;
    private int stars;
    Main main;

    @Override
    public void show() {
        //Utility.setFontStuffTemp();
        main = Main.getInstance();
        main.iabInterface.processPurchases();
        float fadeInLength = 0.0f;
        if (!main.appStarted) {
            fadeInLength = 2.3f;
            main.appStarted = true;
        }
        main.soundHandler.playLoaded(); //this breaks the htc - maybe
        //Gdx.app.log("MenuMain()", "Before login. main.profile.getLogins()" + main.profile.getLogins());
        //Gdx.app.log("MenuMain()", "After login. main.profile.getLogins()" + main.profile.getLogins());
        stars = main.profile.getStars();

        stage = new Stage(new ExtendViewport(Utility.MODEL_SCREEN_WIDTH, Utility.MODEL_SCREEN_HEIGHT));

        Gdx.input.setInputProcessor(stage);
        background = main.assetManager.get("home/background.png", Texture.class);

        fontStars = main.skin.getFont("font_g_othic_56pt_extra");
        fontStars.getData().setScale(.9f, .9f);

        final Image star = new Image(main.assetManager.get("home/star.png", Texture.class));
        star.setPosition(stage.getViewport().getWorldWidth() - 200, stage.getViewport().getWorldHeight() - 100);
        star.setOrigin(Align.center); //https://libgdx.badlogicgames.com/nightlies/docs/api/constant-values.html#com.badlogic.gdx.utils.Align.center
        star.addAction(
                Actions.sequence(
                        Actions.alpha(0),
                        Actions.alpha(1, fadeInLength),
                        Actions.delay(
                                2, Actions.scaleTo(0, 1, .3f)
                        ),
                        Actions.scaleTo(1, 1, .3f),
                        Actions.repeat(
                                -1, Actions.sequence(
                                        Actions.delay(
                                                7, Actions.scaleTo(0, 1, .3f)
                                        ),
                                        Actions.scaleTo(1, 1, .3f)
                                )
                        ))
        );
        star.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                starToast();
                return true;
            }
        });
        stage.addActor(star);

        Image nameImage = new Image(main.assetManager.get("home/name.png", Texture.class));
        float width = 900;
        float height = (width * nameImage.getHeight()) / nameImage.getWidth();
        nameImage.setSize(width, height);
        nameImage.setPosition(stage.getViewport().getWorldWidth() / 2 - nameImage.getWidth() / 2, stage.getViewport().getWorldHeight() / 2 + 500);
        nameImage.addAction(
                Actions.sequence(
                        Actions.alpha(0),
                        Actions.alpha(1, fadeInLength)
                )
        );
        stage.addActor(nameImage);

        TextureRegionDrawable playButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("home/planet.png", Texture.class)));
        Button playButton = new Button(playButtonDrawable);
        playButton.setWidth(879* Utility.MODEL_SCREEN_WIDTH/1200);
        playButton.setHeight(890* Utility.MODEL_SCREEN_WIDTH/1200);
        playButton.setPosition(stage.getViewport().getWorldWidth() / 2 - playButton.getWidth() / 2, stage.getViewport().getWorldHeight() / 2 - playButton.getHeight() / 2);
        playButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                main.soundHandler.play();
                //ScreenManager.getInstance().show(EnumScreen.LEVEL_PACK_MENU);
                Utility.switchScreens(EnumScreen.MAIN_MENU, EnumScreen.LEVEL_PACK_MENU);
                return false;
            }
        });
        stage.addActor(playButton);

        TextureRegionDrawable arrowButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("home/play.png", Texture.class)));
        Button arrowButton = new Button(arrowButtonDrawable);
        arrowButton.setSize(Utility.MODEL_SCREEN_WIDTH * .2f, stage.getViewport().getWorldHeight() * .17f);
        arrowButton.setPosition(stage.getViewport().getWorldWidth() / 2 - arrowButton.getWidth() / 2 + stage.getViewport().getWorldWidth() * .037f, stage.getViewport().getWorldHeight() / 2 - arrowButton.getHeight() / 2 + stage.getViewport().getWorldWidth() * .037f);
        arrowButton.setTransform(true);
        arrowButton.setOrigin(1);
        arrowButton.addAction(
                Actions.sequence(
                        Actions.alpha(0),
                        Actions.alpha(1, fadeInLength),
                        /*Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                load();
                            }
                        }),*/
                        Actions.repeat(
                                -1, Actions.sequence(
                                        Actions.delay(
                                                0, Actions.scaleTo(.85f, .85f, 15)),
                                        Actions.delay(
                                                0, Actions.scaleTo(1, 1, 3, Interpolation.elastic)))
                        )
                )
        );
        arrowButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                main.soundHandler.play();
                Utility.switchScreens(EnumScreen.MAIN_MENU, EnumScreen.LEVEL_PACK_MENU);
                return false;
            }
        });
        stage.addActor(arrowButton);

        TextureRegionDrawable createButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("home/comet.png", Texture.class)));
        Button createButton = new Button(createButtonDrawable);
        createButton.setWidth(849* Utility.MODEL_SCREEN_WIDTH/1200);
        createButton.setHeight(484 * Utility.MODEL_SCREEN_WIDTH / 1200);
        createButton.setPosition(stage.getViewport().getWorldWidth() / 2 - 150, stage.getViewport().getWorldHeight() / 2 - 800);
        createButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                creationModePressed();
                return false;
            }
        });
        createButton.padLeft(20.0f);
        createButton.padRight(20.0f);
        createButton.addAction(
                Actions.sequence(
                        Actions.alpha(0),
                        Actions.alpha(1, fadeInLength)
                )
        );
        stage.addActor(createButton);

        TextureRegionDrawable hammerButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("home/create.png", Texture.class)));
        Button hammerButton = new Button(hammerButtonDrawable);
        hammerButton.setWidth(190 * Utility.MODEL_SCREEN_WIDTH / 1200);
        hammerButton.setHeight(173 * Utility.MODEL_SCREEN_WIDTH / 1200);
        hammerButton.setPosition(stage.getViewport().getWorldWidth() / 2 - Utility.MODEL_SCREEN_WIDTH * .088f, stage.getViewport().getWorldHeight()/2 - 716);
        hammerButton.setTransform(true);
        hammerButton.setOrigin(1);
        hammerButton.addAction(
                Actions.sequence(
                        Actions.alpha(0),
                        Actions.alpha(1, fadeInLength),
                        Actions.delay(
                                5, Actions.rotateBy(50, .4f, Interpolation.swingOut)),
                        Actions.delay(
                                0, Actions.rotateBy(-50, 1.1f, Interpolation.elastic)),
                        Actions.repeat(
                                -1, Actions.sequence(
                                        Actions.delay(
                                                10, Actions.rotateBy(50, .4f, Interpolation.swingOut)),
                                        Actions.delay(
                                                0, Actions.rotateBy(-50, 1.1f, Interpolation.elastic))
                                )
                        )
                )
        );
        hammerButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                creationModePressed();
                return false;
            }
        });
        stage.addActor(hammerButton);

        TextureRegionDrawable soundButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("home/sound_on.png", Texture.class)));
        final TextureRegionDrawable soundOffButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("home/sound_off.png", Texture.class)));
        final Button soundButton = new Button(soundButtonDrawable, soundButtonDrawable, soundOffButtonDrawable);
        soundButton.setSize(Utility.MODEL_SCREEN_WIDTH * .098f, Utility.MODEL_SCREEN_HEIGHT * .049f);
        if (!main.soundHandler.getSoundOn()) {
            soundButton.setChecked(true);
        }
        soundButton.setPosition(stage.getViewport().getWorldWidth() * .05f, stage.getViewport().getWorldHeight() * .05f);
        soundButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (soundButton.isChecked()) {
                    main.soundHandler.soundOn();
                    //if (isPostSplashScreenLoadingFinished()) {
                    main.soundHandler.play();
                    //}
                } else {
                    main.soundHandler.soundOff();
                }
                return false;
            }
        });
        soundButton.addAction(
                Actions.sequence(
                        Actions.alpha(0),
                        Actions.alpha(1, fadeInLength)
                )
        );
        stage.addActor(soundButton);
        promptUserForRating();
        main.persistProfile();
        //temp();
    }

    private void temp() {
        main.level = 0;
        main.levelPack = "My Levels";
        //for (int i = 0; i < main.getHighestLevel(); i++) {
        for (int i = 0; i < 100; i++) {
            try {
                main.level++;
                LoadedLevel loadedLevel = Utility.readJSONLevel();
                Json json = new Json();
                String text = json.prettyPrint(loadedLevel);
                //Gdx.app.log("Temp: ", text);
            }
            catch (Exception ignored) {}
        }
    }

    private void creationModePressed() {
        if (main.profile.getCreateStatus()) {
            startCreationMode();
        }
        else if (main.iabInterface.creationModePurchased()) {
            main.profile.setCreateStatus(true);
            startCreationMode();
        }
        else {
            stage.addActor(new DialogPurchasePrompt(stage, "Creation Mode", null));
        }
    }

    private void startCreationMode() {
        main.level = 0;
        main.levelPack = "create";
        main.levelName = "";
        main.soundHandler.play();
        Utility.switchScreens(EnumScreen.MAIN_MENU, EnumScreen.GAME_CORE);
    }

    private void starToast() {
        Toast toast = new Toast("Earn stars by completing levels", stage, Toast.TOAST_MEDIUM, Toast.TOAST_SMALL);
        toast.setPositionTop();
        toast.startToast();
    }

    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());

        stage.getBatch().begin();
        //stage.getBatch().draw(background, 0, 0);
        
        // Stage doesn't clean up the SpriteBatch's color when it's done,
        // so when the loop comes back around, the last-used color is still applied
        // to the sprite batch. In your case, the last used color happens to be the dialog's color.
        // Hence setting the color white first
        stage.getBatch().setColor(Color.WHITE);
        stage.getBatch().draw(background, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        fontStars.draw(stage.getBatch(), "" + stars, stage.getViewport().getWorldWidth() - 130, stage.getViewport().getWorldHeight() - 90 + fontStars.getLineHeight() / 2);
        /*//Gdx.app.log("MenuMain()", "stars: " + stars);
        //Gdx.app.log("MenuMain()", "stage.getViewport().getWorldWidth(): " + stage.getViewport().getWorldWidth());
        //Gdx.app.log("MenuMain()", "stage.getViewport().getWorldHeight(): " + stage.getViewport().getWorldHeight());
        //Gdx.app.log("MenuMain()", "fontStars.getLineHeight(): " + fontStars.getLineHeight());*/
        stage.getBatch().end();
        stage.draw();
    }

    public void promptUserForRating() {
        //Gdx.app.log("Menumain.promptUserForRating()", "main.profile.promptRating: " + main.profile.promptRating);
        if (main.profile.promptRating && !main.promptedRate) {
            int logins = main.profile.getLogins();
            //Gdx.app.log("Menumain.promptUserForRating()", "main.profile.getLogins(): " + main.profile.getLogins());
            if (logins <= 50) {
                if (logins % 10 == 0) {
                    stage.addActor(new DialogRatingPrompt(stage));
                }
            } else {
                if ((logins - 50) % 25 == 0) {
                    stage.addActor(new DialogRatingPrompt(stage));
                }
            }
            main.promptedRate = true;
        }
    }

    public void resize (int width, int height) {
        Gdx.app.log("MenuMain.resize()", "start of resize");
        stage.getViewport().update(width, height, true);
        Gdx.app.log("MenuMain.resize()", "end of resize");
    }

    public void dispose () {
        Gdx.app.log("MenuMain.dispose()", "disposing");
        stage.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        //If user purchases creation mode they will be taken back to MenuMain, want to update status
        main.iabInterface.processPurchases();
    }

    @Override
    public void hide() {
        dispose();
        ScreenManager.getInstance().dispose(EnumScreen.MAIN_MENU);
    }
}