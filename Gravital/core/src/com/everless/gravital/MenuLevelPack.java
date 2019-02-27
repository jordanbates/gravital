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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;

public class MenuLevelPack implements Screen {
    private Stage stage;
    //private Skin skin;
    private Texture background;
    private BitmapFont fontStars;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private boolean drawFade;
    private TextButton firstPack;
    private int stars;
    private boolean back = false;
    private float fadeWidth, fadeHeight;
    public Main main;

    @Override
    public void show() {
        main = Main.getInstance();
        main.iabInterface.processPurchases();
        main.playVideoMode = false;
        stage = new Stage(new ExtendViewport(Utility.MODEL_SCREEN_WIDTH, Utility.MODEL_SCREEN_HEIGHT));
        stars = main.profile.getStars();
        //skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        background = main.assetManager.get("home/background.png", Texture.class);
        setInputMultiplexer();

        drawFade = !main.profile.isTutorialComplete();
        Vector3 vec3 = stage.getCamera().project(
                new Vector3(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight(), 0));
        fadeWidth = vec3.x;
        fadeHeight = vec3.y;

        Image star = new Image(main.assetManager.get("home/star.png", Texture.class));
        star.setPosition(stage.getViewport().getWorldWidth() - 200, stage.getViewport().getWorldHeight() - 100);
        star.setOrigin(Align.center); //https://libgdx.badlogicgames.com/nightlies/docs/api/constant-values.html#com.badlogic.gdx.utils.Align.center
        star.addAction(
                Actions.sequence(
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

        fontStars = main.skin.getFont("font_g_othic_56pt_extra");
        fontStars.getData().setScale(.9f, .9f);

        Image halfPlanetImage = new Image(main.assetManager.get("home/half_planet.png", Texture.class));
        halfPlanetImage.setSize(Utility.MODEL_SCREEN_WIDTH * .5f, Utility.MODEL_SCREEN_HEIGHT * .57f);
        halfPlanetImage.setPosition(0, stage.getViewport().getWorldHeight() / 2 - halfPlanetImage.getHeight() / 2);
        halfPlanetImage.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                main.soundHandler.play();
                Utility.switchScreens(EnumScreen.LEVELS_MENU, EnumScreen.MAIN_MENU);
                return true;
            }
        });
        stage.addActor(halfPlanetImage);

        TextureRegionDrawable arrowButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("home/play.png", Texture.class)));
        Button arrowButton = new Button(arrowButtonDrawable);
        arrowButton.setSize(Utility.MODEL_SCREEN_WIDTH * .2f, Utility.MODEL_SCREEN_HEIGHT * .17f);
        arrowButton.setPosition(60, stage.getViewport().getWorldHeight() / 2 - arrowButton.getHeight() / 2 + 20);
        arrowButton.setTransform(true);
        arrowButton.setOrigin(1);
        arrowButton.setRotation(180);
        arrowButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                main.soundHandler.play();
                Utility.switchScreens(EnumScreen.LEVELS_MENU, EnumScreen.MAIN_MENU);
                return true;
            }
        });
        stage.addActor(arrowButton);

        final ArrayList<String> levelPackName = Utility.levelPacks;

        float radius = Utility.MODEL_SCREEN_WIDTH * 0.72f;
        float centerX = stage.getViewport().getWorldWidth() * .1f;
        float centerY = stage.getViewport().getWorldHeight()/2;
        for(int i = 0; i < levelPackName.size(); i++) {
            final int levelPack = i;
            float duration = 0.2f + 0.07f * i;
            boolean disabled = false;
            TextButton.TextButtonStyle buttonStyle = main.skin.get("planet", TextButton.TextButtonStyle.class);
            if (i == 3) {
                if (!main.profile.getWarpedWormholesStatus()) {
                    if (main.iabInterface.warpedWormholesPurchased()) {
                        main.profile.setWarpedWormholesStatus(true);
                    }
                    else {
                        buttonStyle = main.skin.get("locked_planet", TextButton.TextButtonStyle.class);
                        disabled = true;
                    }
                }
            }
            else if (i == 4) {
                if (!main.profile.getTheTimingIsRightStatus()) {
                    if (main.iabInterface.theTimingIsRightPurchased()) {
                        main.profile.setTheTimingIsRightStatus(true);
                    }
                    else {
                        buttonStyle = main.skin.get("locked_planet", TextButton.TextButtonStyle.class);
                        disabled = true;
                    }
                }
            }
            TextButton levelButton = new TextButton(levelPackName.get(i), buttonStyle);
            //levelButton.setDisabled(disabled);
            levelButton.setSize(Utility.MODEL_SCREEN_WIDTH * .15f, Utility.MODEL_SCREEN_WIDTH * .15f);
            float slice = 180/ (levelPackName.size() + 1);
            float angle = 90 - ((i+1) * slice);
            angle = (float) Math.toRadians(angle);
            float x = (float) (centerX + radius * Math.cos(angle));
            float y = (float) (centerY + radius * Math.sin(angle));
            float r = levelButton.getWidth();
            levelButton.setPosition(x - r, y - r);
            levelButton.getLabel().setFontScale(.75f);
            levelButton.getLabel().setWrap(true);
            //the parent (cell) determines the size. this still doesn't do anything though
            levelButton.getLabel().getParent().setWidth(levelButton.getWidth() * 0.5f);
            levelButton.addAction(
                    Actions.parallel(Actions.sizeTo(Utility.MODEL_SCREEN_WIDTH * .28f, Utility.MODEL_SCREEN_WIDTH * .28f, duration, Interpolation.bounce)));
            final int finalI = i;
            levelButton.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    if (finalI == 3) {
                        if (main.profile.getWarpedWormholesStatus()) {
                            startLevel(levelPackName.get(levelPack));
                        }
                        else if (main.iabInterface.warpedWormholesPurchased()) {
                            main.profile.setWarpedWormholesStatus(true);
                            startLevel(levelPackName.get(levelPack));
                        }
                        else {
                            stage.addActor(new DialogPurchasePrompt(stage, "Warped Wormholes", levelPackName.get(levelPack)));
                        }
                    }
                    else if (finalI == 4) {
                        if (main.profile.getTheTimingIsRightStatus()) {
                            startLevel(levelPackName.get(levelPack));
                        }
                        else if (main.iabInterface.theTimingIsRightPurchased()) {
                            main.profile.setTheTimingIsRightStatus(true);
                            startLevel(levelPackName.get(levelPack));
                        }
                        else {
                            stage.addActor(new DialogPurchasePrompt(stage, "The Timing Is Right", levelPackName.get(levelPack)));
                        }
                    }
                    else {
                        startLevel(levelPackName.get(levelPack));
                    }
                }
            });
            stage.addActor(levelButton);
            if (levelPackName.get(i).equals(Utility.levelPacks.get(0))) {
                firstPack = levelButton;
            }
        }
    }

    private void startLevel(String levelPackName) {
        main.levelPack = levelPackName;
        main.soundHandler.play();
        Utility.switchScreens(EnumScreen.LEVEL_PACK_MENU, EnumScreen.LEVELS_MENU);
    }

    private void starToast() {
        Toast toast = new Toast("Earn stars by completing levels", stage, Toast.TOAST_MEDIUM, Toast.TOAST_SMALL);
        toast.setPositionTop();
        toast.startToast();
    }

    public void render (float delta) {
        if (back) {
            mainMenu();
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());

        stage.getBatch().begin();

        stage.getBatch().setColor(Color.WHITE);
        stage.getBatch().draw(background, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        fontStars.draw(stage.getBatch(), "" + stars, stage.getViewport().getWorldWidth() - 130, stage.getViewport().getWorldHeight() - 90 + fontStars.getLineHeight() / 2);
        stage.getBatch().end();

        stage.draw();

        if (drawFade) {
            drawTutorialFade();
            stage.getBatch().begin();
            firstPack.draw(stage.getBatch(), 1);
            stage.getBatch().end();
        }
    }

    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose () {
        //skin.dispose();
        stage.dispose();
    }

    private void setInputMultiplexer() {
        InputProcessor backProcessor = new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    //mainMenu();
                    back = true;
                }
                return true;
            }
        };

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(backProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    //Draws screen except for first pack dark to indicate direction
    private void drawTutorialFade() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, .65f);
        //shapeRenderer.rect(0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        shapeRenderer.rect(0, 0, fadeWidth, fadeHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private static void mainMenu() {
        Utility.switchScreens(EnumScreen.LEVELS_MENU, EnumScreen.MAIN_MENU);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        main.iabInterface.processPurchases();
    }

    @Override
    public void hide() {
        dispose();
    }
}
