package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * Created by Jordan on 7/27/2015.
 * Splash Screen class
 */
public class SplashScreen implements Screen {
    private Stage stage;
    private SpriteBatch backgroundSpriteBatch = new SpriteBatch();
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private float progress = 0.0f;
    private float planetWidth;
    private Texture background;
    private Sprite planet;
    private Sprite planetEmpty;
    private boolean backgroundLoaded = false;
    private boolean planetLoaded = false;
    private boolean skinLoaded = false;
    private BitmapFont font;
    private GlyphLayout layout = new GlyphLayout();
    private float gdxWidth, gdxHeight;
    private AssetManager assetManager;
    private Main main;

    @Override
    public void show() {
        main = Main.getInstance();
        assetManager = main.assetManager;
        stage = new Stage(new ExtendViewport(Utility.MODEL_SCREEN_WIDTH, Utility.MODEL_SCREEN_HEIGHT));

        planetWidth = 879* Utility.MODEL_SCREEN_WIDTH/1200;
        main.soundHandler.loadInitialAssets();
        Utility.setBackgroundDimensionsForFitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gdxWidth = Gdx.graphics.getWidth();
        gdxHeight = Gdx.graphics.getHeight();
        main.loadGameSounds();
        queueAssets();
    }

    private void update(float delta) {
        float getProgress = assetManager.getProgress();
        progress = MathUtils.lerp(progress, getProgress, 0.1f);
        boolean fadeIn = true;
        if(assetManager.update() && fadeIn && progress >= getProgress - 0.01f) {
            main.postSplashScreenLoading();
            Utility.switchScreens(EnumScreen.SPLASH_SCREEN, EnumScreen.MAIN_MENU);
        }
    }

    private void queueAssets() {
        //assetManager.load("audio/SFX/a#.mp3", Sound.class);

        //TODO: Fade these in as loaded
        assetManager.load("skin/uiskin.json", Skin.class);
        assetManager.load("home/background.png", Texture.class);
        assetManager.load("home/planet_empty.png", Texture.class);
        assetManager.load("home/planet.png", Texture.class);

        assetManager.load("home/comet.png", Texture.class);
        assetManager.load("home/create.png", Texture.class);
        assetManager.load("home/name.png", Texture.class);
        assetManager.load("home/play.png", Texture.class);
        assetManager.load("home/sound_on.png", Texture.class);
        assetManager.load("home/sound_off.png", Texture.class);
        assetManager.load("home/star.png", Texture.class);
        assetManager.load("home/half_planet.png", Texture.class);

        assetManager.load("arrow.png", Texture.class);
        assetManager.load("backgrounds/space_big.png", Texture.class);
        assetManager.load("backgrounds/glass_overlay.png", Texture.class);
        assetManager.load("barrier_arrow.png", Texture.class);
        //assetManager.load("buttons/blue_button.png", Texture.class);
        assetManager.load("buttons/2sec.png", Texture.class);
        assetManager.load("buttons/2secsel.png", Texture.class);
        assetManager.load("buttons/5sec.png", Texture.class);
        assetManager.load("buttons/5secsel.png", Texture.class);
        assetManager.load("buttons/8sec.png", Texture.class);
        assetManager.load("buttons/8secsel.png", Texture.class);
        assetManager.load("buttons/comet.png", Texture.class);
        assetManager.load("buttons/comet_rotate_clockwise.png", Texture.class);
        assetManager.load("buttons/comet_rotate_counterclockwise.png", Texture.class);
        //assetManager.load("buttons/dialog_box.png", Texture.class);
        //assetManager.load("buttons/dialog_button.png", Texture.class);
        //assetManager.load("buttons/drawer.png", Texture.class);
        assetManager.load("buttons/drawer_menu.png", Texture.class);
        //assetManager.load("buttons/drawer_restart.png", Texture.class);
        //assetManager.load("buttons/drawer_reversed.png", Texture.class);
        assetManager.load("buttons/forward.png", Texture.class);
        assetManager.load("buttons/menu.png", Texture.class);
        assetManager.load("buttons/planet.png", Texture.class);
        assetManager.load("buttons/restart.png", Texture.class);
        assetManager.load("buttons/save.png", Texture.class);
        assetManager.load("buttons/select_planet.png", Texture.class);
        assetManager.load("buttons/select_wormhole.png", Texture.class);
        assetManager.load("buttons/select_wormhole_2.png", Texture.class);
        //assetManager.load("buttons/toast.png", Texture.class);
        assetManager.load("buttons/wormhole.png", Texture.class);
        assetManager.load("buttons/x.png", Texture.class);
        assetManager.load("comet.png", Texture.class);
        assetManager.load("planets/planet_blue.png", Texture.class);
        assetManager.load("planets/planet_orange.png", Texture.class);
        assetManager.load("planets/planet_red.png", Texture.class);
        assetManager.load("planets/planet_selected.png", Texture.class);
        assetManager.load("portal/portal_gradient.png", Texture.class);
        assetManager.load("portal/portal_white_temp.png", Texture.class);
        assetManager.load("rings/ring_red.png", Texture.class);
        assetManager.load("rings/ring_blue.png", Texture.class);
        assetManager.load("rings/ring_orange.png", Texture.class);
        assetManager.load("ship/ship.png", Texture.class);
        assetManager.load("ship/ship_break_1.png", Texture.class);
        assetManager.load("ship/ship_break_2.png", Texture.class);
        assetManager.load("ship/ship_break_3.png", Texture.class);
        assetManager.load("shooting_star/shooting_star.atlas", TextureAtlas.class);
        assetManager.load("timer.png", Texture.class);
        assetManager.load("tracker.png", Texture.class);
        assetManager.load("tutorial/hand.png", Texture.class);
        assetManager.load("wormhole/wormhole_blue.png", Texture.class);
        assetManager.load("wormhole/wormhole_red.png", Texture.class);
        assetManager.load("wormhole/wormhole_orange.png", Texture.class);
        assetManager.load("wormhole/wormhole_multi.png", Texture.class);
    }

    @Override
    public void render(float delta) {
        update(delta);

        if (progress < .99) {
        //If wanted to switch screens on input- use this instead of run action.
        //if (Gdx.input.justTouched()) {myGame.setScreen(new GameScreen());}

        backgroundSpriteBatch.begin();

        if (backgroundLoaded) {
            //stage.getBatch().draw(background, 0, 0, Utility.MODEL_SCREEN_WIDTH, Utility.MODEL_SCREEN_HEIGHT);
            backgroundSpriteBatch.draw(background, 0, 0, gdxWidth, gdxHeight);
            /*stage.getBatch().draw(background, Utility.backgroundX, Utility.backgroundY,
                    Utility.backgroundWidth, Utility.backgroundHeight);*/
        }
        else if (assetManager.isLoaded("home/background.png", Texture.class)) {
            background = assetManager.get("home/background.png", Texture.class);
            backgroundLoaded = true;
            //stage.getBatch().draw(background, 0, 0, Utility.MODEL_SCREEN_WIDTH, Utility.MODEL_SCREEN_HEIGHT);
        }
        else {
            Gdx.gl.glClearColor(0,0,0,1); //Set clear color to black
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        backgroundSpriteBatch.end();
        stage.getBatch().begin();


            if (planetLoaded) {
                stage.getBatch().draw(
                        planetEmpty.getTexture(),
                        stage.getViewport().getWorldWidth() / 2 - planetWidth / 2,
                        stage.getViewport().getWorldHeight() / 2 - planetWidth / 2 + (planetWidth * progress),          /* reposition to draw from half way up from the original sprite position */
                        planetWidth / 2,
                        planetWidth / 2,
                        planetWidth,
                        planetWidth - (planetWidth * progress),             /* draw the sprite at half height*/
                        planet.getScaleX(),
                        planet.getScaleY(),
                        0,
                        planet.getRegionX(),
                        0,
                        planet.getRegionWidth(),
                        (int) (planet.getRegionHeight() - (planet.getRegionHeight()* progress)),     /* only use the texture data from the top of the sprite */
                        false,
                        false);

                stage.getBatch().draw(
                        planet.getTexture(),
                        stage.getViewport().getWorldWidth() / 2 - planetWidth / 2,
                        stage.getViewport().getWorldHeight() / 2 - planetWidth / 2,
                        planetWidth / 2,
                        planetWidth / 2,
                        planetWidth,
                        planetWidth * progress,             /* draw the sprite at half height*/
                        planet.getScaleX(),
                        planet.getScaleY(),
                        0,
                        planet.getRegionX(),
                        (int) (planet.getHeight() - (planet.getHeight() * progress)),
                        planet.getRegionWidth(),
                        (int) (planet.getRegionHeight() * progress),     /* only use the texture data from the top of the sprite */
                        false,
                        false);
            }
            else if (assetManager.isLoaded("home/planet.png", Texture.class)) {
                planet = new Sprite(assetManager.get("home/planet.png", Texture.class));
                planetEmpty = new Sprite(assetManager.get("home/planet_empty.png", Texture.class));
                planetLoaded = true;
            }

            if (skinLoaded) {
                int percentage = Math.round(progress * 100);
                String text = percentage + "%";
                layout.setText(font, text);
                final float fontX = stage.getViewport().getWorldWidth() / 2 - layout.width / 2 + 40;
                final float fontY = stage.getViewport().getWorldHeight() / 2 + 65;
                font.draw(stage.getBatch(), layout, fontX, fontY);
            } else if (assetManager.isLoaded("skin/uiskin.json", Skin.class)) {
                skinLoaded = true;
                main.skin = assetManager.get("skin/uiskin.json", Skin.class);
                font = main.skin.getFont("font_g_othic_56pt_extra");
                font.getData().setScale(2, 2);
            }

        stage.getBatch().end();
        stage.act();
        stage.draw();
        }
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}