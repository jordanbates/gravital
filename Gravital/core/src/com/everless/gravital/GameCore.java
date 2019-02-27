package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Jordan on 7/27/2015.
 * Game instance
 */
public class GameCore implements Screen, GestureDetector.GestureListener {
    public static World world;
    public static Batch batch;
    public static float screenWidth, screenHeight, screenWidthAdjusted, screenHeightAdjusted;
    public static float halfScreenWidth, halfScreenHeight;
    public static float shipSpriteWidth, shipSpriteHeight;
    public Main main;

    public static OrthographicCamera camera;
    public static Viewport viewport;

    protected Ship ship;
    protected Arrow arrow;
    protected BarrierArrows barrierArrows;
    protected Portal portal;
    protected PlanetManager planetManager;
    protected WormholeManager wormholeManager;
    protected OffScreenTracker offScreenTracker;
    protected Timer timer;
    protected ShootingStar[] shootingStars = new ShootingStar[] {new ShootingStar()};
    protected final int SSSMAX = 150; // 1/SSSMAX chance of a shooting star per update
    protected int shootingStarCount = SSSMAX;
    public static boolean shootingStarActive = false;
    protected Toast toast;
    protected BrokenShip brokenShip;
    protected boolean changeBrokenShipPosition = false; //world is locked when objectContactListener is called
    protected boolean shipBreakInEffect = false;
    protected float shipCollisionImpulse = 0.0f;
    protected boolean shipCollisionImpulseSet = false;

    public static boolean shipExists = true;
    public static boolean shipInPortal = false;
    protected Boolean shipSuccessful = false;
    protected boolean shipInMotion = false;
    protected boolean screenDrawn = false;
    protected String panState = null;
    protected final float FADE = 0.5f;
    protected float fade = FADE;
    protected boolean fadeIn = true;
    protected static boolean fadeOut = false;
    protected boolean firstGesture = false;
    protected boolean unsavedChanges = false;

    protected LoadedLevel loadedLevel;
    protected Texture background;
    protected Texture overlay;
    protected Stage HUDStage;

    //Enums
    public EnumGameState gameState;
    public EnumEndAction endAction;
    public EnumCreateButtonMode createButtonMode;

    protected ShapeRenderer shapeRenderer;

    protected Button menuButton;
    protected Button saveButton;
    protected Button wormholeButton;
    protected Button wormholeButton2;
    protected Button planetButton;

    protected Button xButton;
    protected Button addCometButton;
    protected Button cometRotateClockwiseButton;
    protected Button cometRotateCounterclockwiseButton;
    protected Button twoSecButton;
    protected Button fiveSecButton;
    protected Button eightSecButton;

    protected Button end_restartButton;
    protected Button end_nextLevelButton;
    protected Button end_menuButton;
    protected Image overlayImage;

    private GlyphLayout glyphLayout;
    private Image star;
    private BitmapFont endFont;

    private Tutorial tutorial;

    protected int verticalSelectedButtonOffset = 100;
    protected int horizontalSelectedButtonOffset = 100;
    protected int verticalPortalSelectedButtonOffset = 70;
    protected int horizontalPortalSelectedButtonOffset = 200;

    private final int TICKS_PER_SECOND = 50; //50 normally
    private final int MS_BETWEEN_UPDATE = 1000 / TICKS_PER_SECOND;
    private final int MAX_FRAME_SKIPS = 5;
    private int totalGameTicks = 0;
    private int nextGameTick = MS_BETWEEN_UPDATE;
    //private float interpolation;

    private long startTime = 0, elapsedTime = 0;
    private long endPreFadeStartTime = 0, endPreFadeElapsedTime = 0;

    protected EntityManager entityManager;

    /*Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;*/

    //public Body queriedBody;

    public GameCore() {
        main = Main.getInstance();
    }

    @Override
    public void show() {
        if(main.levelPack.equals("Edit My Levels: Save Temp Level")) {
            main.levelPack = "Edit My Levels";
            saveTempLevel();
        }
        if(main.levelPack.equals("create") || main.levelPack.equals("Edit My Levels")) {
            gameState = EnumGameState.CREATE;
            createButtonMode = EnumCreateButtonMode.PLANET;
        }
        else {
            gameState = EnumGameState.RUN;
        }
        world = new World(new Vector2(0, 0), true);
        loadAssets();
        shapeRenderer = new ShapeRenderer();
        setUpCameraAndViewport();
        HUDStage = new Stage(viewport);
        batch = HUDStage.getBatch();
        batch.setProjectionMatrix(camera.combined);
        setInputMultiplexer();
        setInitialValues();
        loadedLevel = Utility.readJSONLevel(); //need this before createHUD
        createHUD();

        ObjectContactListener objectContactListener = new ObjectContactListener(this);
        world.setContactListener(objectContactListener);

        entityManager = new EntityManager();

        createGameObjects();
        initializeGameObjects();

        shipSpriteWidth = Utility.trueShipSpriteWidth;
        shipSpriteHeight = Utility.trueShipSpriteHeight;

        tutorial = new Tutorial(this);
        if (!main.profile.isTutorialComplete()) {
            tutorial.startSwipeHandPullShipMotion();
        }
        if (main.levelPack.equals(Utility.levelPacks.get(0))) {
            switch (main.level) {
                case 1:
                    tutorial.startLevel1Text();
                    break;
                case 2:
                    tutorial.startLevel2Text();
                    break;
                case 3:
                    tutorial.startLevel3Text();
                    break;
                case 4:
                    tutorial.startLevel4Text();
                    break;
                case 5:
                    tutorial.startLevel5Text();
                    break;
                case 6:
                    tutorial.startLevel6Text();
                    break;
            }
        }
        else if (main.levelPack.equals(Utility.levelPacks.get(1))) {
            if (main.level == 1) {
                tutorial.startWormholeLevel1Text();
            }
        }
        else if (main.levelPack.equals(Utility.levelPacks.get(2))) {
            if (main.level == 1) {
                tutorial.startBarrierLevel1Text();
            }
        }
        else if (main.levelPack.equals(Utility.levelPacks.get(3))) {
            if (main.level == 1) {
                tutorial.startWhiteWormholeLevel1Text();
            }
        }
        else if (main.levelPack.equals(Utility.levelPacks.get(4))) {
            if (main.level == 1) {
                tutorial.startTimingLevel1Text();
            }
        }
        else if (isGameStateCreate() && !main.profile.isCreateTutorialComplete()) {
            tutorial.startCreateText();
        }

        /*debugMatrix = new Matrix4(camera.combined);
        //debugMatrix.scale(Utility.PIXELS_TO_METERS, Utility.PIXELS_TO_METERS, 1f);
        debugMatrix.scale(.14f, .14f, 1f);
        debugMatrix.setTranslation(-.9f, -.9f, 0);
        debugRenderer = new Box2DDebugRenderer();*/

        displayDelayedToasts();

        main.sessionShipLaunches++;
        if (main.playVideoMode && main.sessionShipLaunches % 3 == 0) {
            main.adInterface.loadOrShowVideoInterstitial();
        }
        else {
            if (!main.adLoaded) {
                main.adInterface.loadImageInterstitial();
                main.adLoaded = true;
            } else if (main.showAd() && main.sessionShipLaunches % 25 == 0) {
                main.adInterface.showImageInterstitial();
                main.adLoaded = false;

                if (!main.profile.haveShownAdMessage) {
                    HUDStage.addActor(new DialogAdMessage(this));
                    main.profile.haveShownAdMessage = true;
                }
            }
        }
    }

    //Must do this in show, not on creation.
    public void setInitialValues() {
        setScreenWidthAndHeight();
        shipExists = true;
        shipInMotion = false;
        shipInPortal = false;
        startTime = TimeUtils.millis();
    }

    public void setScreenWidthAndHeight() {
        screenWidth = HUDStage.getViewport().getWorldWidth();
        screenHeight = HUDStage.getViewport().getWorldHeight();
        halfScreenWidth = screenWidth / 2;
        halfScreenHeight = screenHeight / 2;
        screenWidthAdjusted = Utility.projectX(screenWidth);
        screenHeightAdjusted = Utility.projectY(screenHeight);
    }

    public void setUpCameraAndViewport() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Utility.MODEL_SCREEN_WIDTH, Utility.MODEL_SCREEN_HEIGHT, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    private void createGameObjects() {
        timer = new Timer(this); //must be before portal
        ship = new Ship(this, main.assetManager.get("ship/ship.png", Texture.class));
        arrow = new Arrow(this, main.assetManager.get("arrow.png", Texture.class), loadedLevel);
        barrierArrows = new BarrierArrows(this, loadedLevel);
        portal = new Portal(this, loadedLevel);
        planetManager = new PlanetManager(this, loadedLevel);
        wormholeManager = new WormholeManager(this, loadedLevel);
        wormholeManager.setWormholeManager();
        offScreenTracker = new OffScreenTracker(this, main.assetManager.get("tracker.png", Texture.class));
        createBrokenShip();
    }

    private void initializeGameObjects() {
        ship.setSpritePosition();
        planetManager.setSpritePosition();
        planetManager.assignGravities(ship);
        if(gameState.equals(EnumGameState.RUN)) {
            planetManager.startComets();
        }
    }

    private void loadAssets() {
        background = main.assetManager.get("backgrounds/space_big.png", Texture.class);
        overlay = main.assetManager.get("backgrounds/glass_overlay.png", Texture.class);
    }

    private void createHUD() {
        if (isGameStateCreate()) {
            final TextureRegionDrawable menuButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/drawer_menu.png", Texture.class)));
            menuButton = new Button(menuButtonDrawable);
            menuButton.setPosition(50, screenHeight - 150);
            menuButton.setHeight(120);
            menuButton.setWidth(120);
            menuButton.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (isGameStateCreateOrCreateRun()) {
                        if (unsavedChanges) {
                            HUDStage.addActor(new DialogConfirmExitWithoutSaving(GameCore.this));
                        }
                        else {
                            if (main.levelPack.equals("Edit My Levels")) {
                                endAction = EnumEndAction.LEVELS_MENU;
                            }
                            else {
                                endAction = EnumEndAction.MAIN_MENU;
                            }
                            fadeOut();
                        }
                    }
                    else {
                        endAction = EnumEndAction.LEVELS_MENU;
                        fadeOut();
                    }
                }
            });
            HUDStage.addActor(menuButton);

            TextureRegionDrawable saveButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/save.png", Texture.class)));
            saveButton = new Button(saveButtonDrawable);
            saveButton.setPosition(200, screenHeight - 150);
            saveButton.setHeight(120);
            saveButton.setWidth(120);
            saveButton.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    unselectAll();
                    pause();
                    HUDStage.addActor(new DialogSave(GameCore.this, false));
                }
            });
            HUDStage.addActor(saveButton);

            TextureRegionDrawable wormholeButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/wormhole.png", Texture.class)));
            final TextureRegionDrawable selectWormholeButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/select_wormhole.png", Texture.class)));
            wormholeButton = new Button(wormholeButtonDrawable, selectWormholeButtonDrawable, selectWormholeButtonDrawable);
            wormholeButton.setPosition(350, screenHeight - 150);
            wormholeButton.setHeight(120);
            wormholeButton.setWidth(120);
            wormholeButton.addListener(new InputListener() {
                // If true is returned, this listener will receive all touchDragged and touchUp events,
                // even those not over this actor, until touchUp is received. Also when true is returned,
                // the event is handled (won't be passed to next inputListener).
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    planetButton.setChecked(false);
                    if (wormholeButton.isChecked()) {
                        createButtonMode = EnumCreateButtonMode.MULTI_WORMHOLE;
                        wormholeButton.setChecked(true);
                    } else {
                        createButtonMode = EnumCreateButtonMode.WORMHOLE;
                        wormholeButton.setChecked(true);
                        wormholeButton2.setChecked(true);
                    }
                }
            });
            wormholeButton.setChecked(false);
            HUDStage.addActor(wormholeButton);

            TextureRegionDrawable wormholeButton2Drawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/wormhole.png", Texture.class)));
            final TextureRegionDrawable selectWormholeButton2Drawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/select_wormhole_2.png", Texture.class)));
            wormholeButton2 = new Button(wormholeButton2Drawable, selectWormholeButton2Drawable, selectWormholeButton2Drawable);
            wormholeButton2.setPosition(350, screenHeight - 150);
            wormholeButton2.setHeight(120);
            wormholeButton2.setWidth(120);
            wormholeButton2.addListener(new InputListener() {
                //If true is returned, this listener will receive all touchDragged and touchUp events,
                // even those not over this actor, until touchUp is received. Also when true is returned,
                // the event is handled (won't be passed to next inputListener).
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (wormholeButton2.isChecked()) {
                        createButtonMode = EnumCreateButtonMode.WORMHOLE;
                        planetButton.setChecked(false);
                    } else {
                        createButtonMode = EnumCreateButtonMode.WORMHOLE;
                        wormholeButton2.setChecked(true);
                        wormholeButton.setChecked(true);
                    }
                }
            });
            wormholeButton2.setChecked(false);

            final TextureRegionDrawable planetButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/planet.png", Texture.class)));
            TextureRegionDrawable selectPlanetButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/select_planet.png", Texture.class)));
            planetButton = new Button(planetButtonDrawable, selectPlanetButtonDrawable, selectPlanetButtonDrawable);
            planetButton.setPosition(500, screenHeight - 150);
            planetButton.setHeight(120);
            planetButton.setWidth(120);
            planetButton.addListener(new InputListener() {
                //If true is returned, this listener will receive all touchDragged and touchUp events,
                // even those not over this actor, until touchUp is received. Also when true is returned,
                // the event is handled (won't be passed to next inputListener).
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    createButtonMode = EnumCreateButtonMode.PLANET;
                    planetButton.setChecked(true);
                    if (planetButton.isChecked()) {
                        wormholeButton.setChecked(false);
                        wormholeButton2.setChecked(false);
                    }
                }
            });
            planetButton.setChecked(true); //Set checked by default
            HUDStage.addActor(planetButton);

            TextureRegionDrawable xButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/x.png", Texture.class)));
            xButton = new Button(xButtonDrawable);
            xButton.setHeight(150);
            xButton.setWidth(150);
            xButton.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    planetManager.deleteSelectedPlanet();
                    planetManager.deleteSelectedComet();
                    wormholeManager.deleteSelected();
                }
            });

            TextureRegionDrawable addCometButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/comet.png", Texture.class)));
            addCometButton = new Button(addCometButtonDrawable);
            addCometButton.setHeight(150);
            addCometButton.setWidth(150);
            addCometButton.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    planetManager.addComet();
                    planetManager.setSpritePosition();
                }
            });

            TextureRegionDrawable cometRotateClockwiseButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/comet_rotate_clockwise.png", Texture.class)));
            cometRotateClockwiseButton = new Button(cometRotateClockwiseButtonDrawable);
            cometRotateClockwiseButton.setHeight(150);
            cometRotateClockwiseButton.setWidth(150);
            cometRotateClockwiseButton.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    planetManager.switchSelectedCometDirection();
                }
            });

            TextureRegionDrawable cometRotateCounterclockwiseButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/comet_rotate_counterclockwise.png", Texture.class)));
            cometRotateCounterclockwiseButton = new Button(cometRotateCounterclockwiseButtonDrawable);
            cometRotateCounterclockwiseButton.setHeight(150);
            cometRotateCounterclockwiseButton.setWidth(150);
            cometRotateCounterclockwiseButton.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    planetManager.switchSelectedCometDirection();
                }
            });

            final TextureRegionDrawable twoSecButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/2sec.png", Texture.class)));
            TextureRegionDrawable twoSecButtonSelectedDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/2secsel.png", Texture.class)));
            twoSecButton = new Button(twoSecButtonDrawable, twoSecButtonSelectedDrawable, twoSecButtonSelectedDrawable);
            twoSecButton.setHeight(150);
            twoSecButton.setWidth(150);
            twoSecButton.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (twoSecButton.isChecked()) {
                        portal.minTimeOff();
                        twoSecButton.setChecked(false);
                    }
                    else {
                        portal.setMinimumTime(2);
                        fiveSecButton.setChecked(false);
                        eightSecButton.setChecked(false);
                        twoSecButton.setChecked(true);
                    }
                }
            });
            if (loadedLevel.portalMinTime == 2) {
                twoSecButton.setChecked(true);
            }

            TextureRegionDrawable fiveSecButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/5sec.png", Texture.class)));
            TextureRegionDrawable fiveSecButtonSelectedDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/5secsel.png", Texture.class)));
            fiveSecButton = new Button(fiveSecButtonDrawable, fiveSecButtonSelectedDrawable, fiveSecButtonSelectedDrawable);
            fiveSecButton.setHeight(150);
            fiveSecButton.setWidth(150);
            fiveSecButton.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (fiveSecButton.isChecked()) {
                        portal.minTimeOff();
                        fiveSecButton.setChecked(false);
                    }
                    else {
                        portal.setMinimumTime(5);
                        twoSecButton.setChecked(false);
                        eightSecButton.setChecked(false);
                        fiveSecButton.setChecked(true);
                    }
                }
            });
            if (loadedLevel.portalMinTime == 5) {
                fiveSecButton.setChecked(true);
            }

            TextureRegionDrawable eightSecButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/8sec.png", Texture.class)));
            TextureRegionDrawable eightSecButtonSelectedDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/8secsel.png", Texture.class)));
            eightSecButton = new Button(eightSecButtonDrawable, eightSecButtonSelectedDrawable, eightSecButtonSelectedDrawable);
            eightSecButton.setHeight(150);
            eightSecButton.setWidth(150);
            eightSecButton.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (eightSecButton.isChecked()) {
                        portal.minTimeOff();
                        eightSecButton.setChecked(false);
                    }
                    else {
                        portal.setMinimumTime(8);
                        twoSecButton.setChecked(false);
                        fiveSecButton.setChecked(false);
                        eightSecButton.setChecked(true);
                    }
                }
            });
            if (loadedLevel.portalMinTime == 8) {
                eightSecButton.setChecked(true);
            }
        }

        TextureRegionDrawable endRestartButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/restart.png", Texture.class)));
        end_restartButton = new Button(endRestartButtonDrawable);
        float extra = 100;
        end_restartButton.setPosition(screenWidth + extra, halfScreenHeight);
        end_restartButton.setHeight(150);
        end_restartButton.setWidth(150);
        end_restartButton.addAction(
                Actions.sequence(
                        Actions.alpha(0f),
                        Actions.parallel(
                                Actions.fadeIn(1.0f),
                                Actions.moveBy(-screenWidth / 2 - 75 - extra, 0, 2.0f, Interpolation.elastic))));
        end_restartButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (endAction == null || endAction == EnumEndAction.NO_ACTION) {
                    endAction = EnumEndAction.RESTART;
                    fadeOut();
                }
            }
        });

        TextureRegionDrawable nextLevelButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/forward.png", Texture.class)));
        end_nextLevelButton = new Button(nextLevelButtonDrawable);
        end_nextLevelButton.setPosition(-250, halfScreenHeight);
        end_nextLevelButton.setHeight(150);
        end_nextLevelButton.setWidth(150);
        end_nextLevelButton.addAction(
                Actions.sequence(
                        Actions.alpha(0f),
                        Actions.parallel(
                                Actions.fadeIn(0.0f),
                                Actions.moveBy(halfScreenWidth + 250 + 225, 0, 1.9f, Interpolation.elastic))));
        end_nextLevelButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (endAction == null || endAction == EnumEndAction.NO_ACTION) {
                    endAction = EnumEndAction.NEXT_LEVEL;
                    fadeOut();
                }
            }
        });

        TextureRegionDrawable end_menuButtonDrawable = new TextureRegionDrawable(new TextureRegion(main.assetManager.get("buttons/menu.png", Texture.class)));
        end_menuButton = new Button(end_menuButtonDrawable);
        end_menuButton.setPosition(-250, halfScreenHeight + 25);
        end_menuButton.setHeight(100);
        end_menuButton.setWidth(150);
        end_menuButton.addAction(
                Actions.sequence(
                        Actions.alpha(0f),
                        Actions.parallel(
                                Actions.fadeIn(0.0f),
                                Actions.moveBy(halfScreenWidth - 150, 0, 1.8f, Interpolation.elastic))));
        end_menuButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (endAction == null || endAction == EnumEndAction.NO_ACTION) {
                    endAction = EnumEndAction.LEVELS_MENU;
                    fadeOut();
                }
            }
        });

        overlayImage = new Image(overlay);
        if (screenWidth > overlayImage.getWidth()) {
            overlayImage.setWidth(screenWidth);
        }
        overlayImage.setPosition(-overlay.getWidth(), 0);
        overlayImage.addAction(
                Actions.sequence(
                        Actions.delay(0.5f),
                        Actions.moveBy(screenWidth, 0, 1.5f, Interpolation.linear))
        );

        toast = new Toast("Level Saved", HUDStage, Toast.TOAST_SHORT, Toast.TOAST_SMALL);
        toast.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return false;
            }
        });
    }

    private void setInputMultiplexer() {
        InputProcessor backProcessor = new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.BACK && (endAction == null || endAction == EnumEndAction.NO_ACTION)) {
                    if(isGameStateRun()) {
                        endAction = EnumEndAction.LEVELS_MENU;
                        fadeOut();
                    }
                    else {
                        if (unsavedChanges) {
                            HUDStage.addActor(new DialogConfirmExitWithoutSaving(GameCore.this));
                        }
                        else {
                            if (main.levelPack.equals("Edit My Levels")) {
                                endAction = EnumEndAction.LEVELS_MENU;
                            }
                            else {
                                endAction = EnumEndAction.MAIN_MENU;
                            }
                            fadeOut();
                        }
                    }
                }
                else if (keycode == Input.Keys.ESCAPE) {
                    Gdx.app.exit();
                }
                else if (keycode == Input.Keys.MENU) {
                    //toggleDrawerButton(true);
                }
                return true;
            }
        };

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(HUDStage);
        inputMultiplexer.addProcessor(new GestureDetector(this));
        inputMultiplexer.addProcessor(backProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        /**
         * This is going to update the game TICKS_PER_SECOND times per second (to keep game running at
         * constant rate across different devices). It will then render as often as it can. If it
         * hasn't rendered yet after MAX_FRAME_SKIPS updates, it will be forced to render.
         */
        int frameLoops = 0;
        updateTickCount(delta);
        while(totalGameTicks > nextGameTick && frameLoops < MAX_FRAME_SKIPS) {
            updateGame(delta);
            nextGameTick += MS_BETWEEN_UPDATE;
            frameLoops++;
        }
        drawScreen(delta);

        //temp
        //updateGame(delta);
        //drawScreen(delta);

        //interpolation = float( GetTickCount() + MS_BETWEEN_UPDATE - nextGameTick) / float(MS_BETWEEN_UPDATE);
        //display_game( interpolation );
    }

    /**
     * Updating totalGameTicks and returning
     */
    private int updateTickCount(float delta) {
        return totalGameTicks = Math.round(totalGameTicks + ((delta * 1000)));// / MS_BETWEEN_UPDATE));
    }

    private void updateGame(float delta) {
        if (isGameStateRunOrCreateOrCreateRun()) {
            world.step(1f / 60f, 6, 2);
            HUDStage.act(delta); //currently are no act methods

            removeEntities();
            updateArrow();
            updateShip();
            updatePlanetManager();
            updateShootingStars();
            updateHUD();
            updatePortal();
            updateWormholeManager();
            removeEntities();

            if (gameState != EnumGameState.CREATE) {
                updateGravities();
                updateTracker();
            }

            if (!firstGesture) {
                elapsedTime = TimeUtils.timeSinceMillis(startTime);
                if (elapsedTime > 500) {
                    firstGesture = true;
                }
            }
        }
    }

    protected void removeEntities() {
        for (Entity entity : entityManager.entitiesToRemove)
        {
            world.destroyBody(entity.body);
        }
        entityManager.remove();
    }

    private void updateGravities() {
        if (isGameStateRunOrCreateRun()) {
            planetManager.applyGravities();
        }
    }

    private void updateArrow() {
        //arrow.setSpritePosition();
    }

    private void updateTracker() {
        if(!ship.onScreen() && shipExists) {
            offScreenTracker.setSpritePosition();
            offScreenTracker.setSpriteRotation();
        }
    }

    private void updateShip() {
        ship.setSpritePosition();
        if (shipInMotion && shipExists) {
            ship.setSpriteRotationTowardsTravel();
            ship.bodyWrapper.body.setLinearDamping(.03f);
        }
        if (!shipInPortal) {
            portal.resizeShip();
            updateBrokenShip();
        }
    }

    private void updateBrokenShip() {
        if(changeBrokenShipPosition && !shipBreakInEffect) {
            breakShip();
            shipBreakInEffect = true;
        }
        brokenShip.setSpritePosition();
    }

    private void updatePlanetManager() {
        planetManager.setSpritePosition(); //Includes comet rotation
        if(gameState.equals(EnumGameState.CREATE_RUN) && shipInMotion) {
            planetManager.startComets(); //will only be triggered once
        }
    }

    private void updatePortal() {
        portal.incrementRotation();
        portal.setScale();
    }

    private void updateWormholeManager() {
        wormholeManager.update();
    }

    /**
     * Randomly decides to create new shooting stars
     * Will activate inactive stars, unless all are active
     */
    private void updateShootingStars() {
        if(shootingStarActive) {
            shootingStarCount = SSSMAX;
            shootingStarActive = false;
        }
        int randomNum = Utility.randInt(0, shootingStarCount);
        if(randomNum == 0) {
            for(ShootingStar shootingStar : shootingStars) {
                if(!shootingStar.active) {
                    //adjust for min starting at bottom left of screen and not off screen
                    int screenWidthRandomNum = Utility.randInt(0, (int) screenWidth);
                    int screenHeightRandomNum = Utility.randInt(0, (int) screenHeight);
                    int newRadius = Utility.randInt(100, 400);
                    int newDegrees = Utility.randInt(0, 360);
                    shootingStar.setPosition(screenWidthRandomNum, screenHeightRandomNum);
                    shootingStar.radius = newRadius;
                    shootingStar.degrees = newDegrees;
                    shootingStar.active = true;
                    break;
                }
            }
        }
    }

    /**
     * For adding and removing buttons from HUD as necessary
     */
    private void updateHUD() {
        if (addCometButton != null)
            addCometButton.remove();
        if (xButton != null)
            xButton.remove();
        if (cometRotateClockwiseButton != null)
            cometRotateClockwiseButton.remove();
        if (cometRotateCounterclockwiseButton != null)
            cometRotateCounterclockwiseButton.remove();
        if (twoSecButton != null)
            twoSecButton.remove();
        if (fiveSecButton != null)
            fiveSecButton.remove();
        if (eightSecButton != null)
            eightSecButton.remove();
        if (menuButton != null)
            menuButton.remove();
        if (saveButton != null)
            saveButton.remove();
        if (planetButton != null)
            planetButton.remove();
        if (wormholeButton != null)
            wormholeButton.remove();
        if (wormholeButton2 != null)
            wormholeButton2.remove();

        if (isGameStateCreate()) {
            if (createButtonMode != EnumCreateButtonMode.MULTI_WORMHOLE) {
                HUDStage.addActor(wormholeButton);
            }
            else {
                HUDStage.addActor(wormholeButton2);
            }
            HUDStage.addActor(menuButton);
            HUDStage.addActor(saveButton);
            HUDStage.addActor(planetButton);

            if (planetManager.currentSelect != -1) {
                if (xButton.getStage() == null) {
                    setRightButtonSelectCoordinates(xButton, planetManager.getSelectedSpaceObject());
                    HUDStage.addActor(xButton);
                }
                if (addCometButton.getStage() == null) {
                    setLeftButtonSelectCoordinates(addCometButton, planetManager.getSelectedSpaceObject());
                    HUDStage.addActor(addCometButton);
                }
            }
            else if (planetManager.isCometSelected()) {
                if (xButton.getStage() == null) {
                    setRightButtonSelectCoordinates(xButton, planetManager.getSelectedComet());
                    HUDStage.addActor(xButton);
                }
                if (planetManager.isSelectedCometRotatingClockwise() && cometRotateClockwiseButton.getStage() == null) {
                    setLeftButtonSelectCoordinates(cometRotateClockwiseButton, planetManager.getSelectedComet());
                    HUDStage.addActor(cometRotateClockwiseButton);
                }
                else if (!planetManager.isSelectedCometRotatingClockwise() && cometRotateCounterclockwiseButton.getStage() == null) {
                    setLeftButtonSelectCoordinates(cometRotateCounterclockwiseButton, planetManager.getSelectedComet());
                    HUDStage.addActor(cometRotateCounterclockwiseButton);
                }
            }
            else if (wormholeManager.currentSelect != -1) {
                if (xButton.getStage() == null) {
                    setCenterButtonSelectCoordinates(xButton, wormholeManager.getSelectedWormhole());
                    HUDStage.addActor(xButton);
                }
            }
            else if (portal.selected) {
                if (twoSecButton.getStage() == null) {
                    setLeftButtonSelectCoordinates(twoSecButton, portal);
                    HUDStage.addActor(twoSecButton);
                }
                if (fiveSecButton.getStage() == null) {
                    setCenterButtonSelectCoordinates(fiveSecButton, portal);
                    HUDStage.addActor(fiveSecButton);
                }
                if (eightSecButton.getStage() == null) {
                    setRightButtonSelectCoordinates(eightSecButton, portal);
                    HUDStage.addActor(eightSecButton);
                }
            }
        }
        else if (!shipExists && end_restartButton.getStage() == null && isGameStateRun() && shipInPortal) {
            HUDStage.addActor(overlayImage);
            HUDStage.addActor(end_restartButton);
            HUDStage.addActor(end_menuButton);
            if (shipSuccessful && nextLevelAvailable()) {
                HUDStage.addActor(end_nextLevelButton);
            }
            //addStar();
        }
    }

    protected boolean nextLevelAvailable() {
        return main.getHighestLevel() > main.level;
    }

    protected void setLeftButtonSelectCoordinates(Button button, SpaceObject spaceObject) {
        button.setPosition(spaceObject.getXinPixels() - button.getWidth() / 2 - horizontalSelectedButtonOffset,
                spaceObject.getYinPixels() + button.getHeight() / 2 + verticalSelectedButtonOffset);
    }

    protected void setRightButtonSelectCoordinates(Button button, SpaceObject spaceObject) {
        button.setPosition(spaceObject.getXinPixels() - button.getWidth() / 2 + horizontalSelectedButtonOffset,
                spaceObject.getYinPixels() + button.getHeight() / 2 + verticalSelectedButtonOffset);
    }

    protected void setLeftButtonSelectCoordinates(Button button, Wormhole wormhole) {
        button.setPosition(wormhole.position.x - button.getWidth() / 2 - horizontalSelectedButtonOffset,
                wormhole.position.y + button.getHeight() / 2 + verticalSelectedButtonOffset);
    }

    protected void setRightButtonSelectCoordinates(Button button, Wormhole wormhole) {
        button.setPosition(wormhole.position.x - button.getWidth() / 2 + horizontalSelectedButtonOffset,
                wormhole.position.y + button.getHeight() / 2 + verticalSelectedButtonOffset);
    }

    protected void setCenterButtonSelectCoordinates(Button button, Wormhole wormhole) {
        button.setPosition(wormhole.position.x - button.getWidth() / 2 + wormhole.pixelRadius,
                wormhole.position.y + +button.getHeight() / 2 + wormhole.pixelRadius + verticalSelectedButtonOffset);
    }

    protected void setLeftButtonSelectCoordinates(Button button, Portal portal) {
        button.setPosition(portal.position.x + portal.pixelRadius - button.getWidth() / 2 - horizontalPortalSelectedButtonOffset,
                portal.position.y + portal.pixelRadius + button.getHeight() / 2 + verticalPortalSelectedButtonOffset);
    }

    protected void setCenterButtonSelectCoordinates(Button button, Portal portal) {
        button.setPosition(portal.position.x + portal.pixelRadius - button.getWidth() / 2,
                portal.position.y + portal.pixelRadius + button.getHeight() / 2 + verticalPortalSelectedButtonOffset);
    }

    protected void setRightButtonSelectCoordinates(Button button, Portal portal) {
        button.setPosition(portal.position.x + portal.pixelRadius - button.getWidth() / 2 + horizontalPortalSelectedButtonOffset,
                portal.position.y + portal.pixelRadius + button.getHeight() / 2 + verticalPortalSelectedButtonOffset);
    }

    protected void addStar() {
        star = new Image(main.assetManager.get("home/star.png", Texture.class));
        Color c = star.getColor();
        c.a = 0;
        star.setColor(c);
        star.setSize(200, 200);
        star.setPosition(halfScreenWidth - star.getWidth() / 2, halfScreenHeight - 200 - star.getHeight() / 2);
        star.setOrigin(1); //https://libgdx.badlogicgames.com/nightlies/docs/api/constant-values.html#com.badlogic.gdx.utils.Align.center
        star.addAction(
                Actions.sequence(
                        Actions.alpha(0),
                        Actions.alpha(1, 3),
                        Actions.scaleTo(1, 1, .3f),
                        Actions.repeat(
                                -1, Actions.sequence(
                                        Actions.delay(
                                                10, Actions.scaleTo(0, 1, .5f, Interpolation.exp5)
                                        ),
                                        Actions.scaleTo(1, 1, .3f)
                                )
                        ))
        );
        HUDStage.addActor(star);
        endFont = main.skin.getFont("font_g_othic_56pt_extra");
        endFont.getData().setScale(2, 2);
        int stars = main.profile.getStars();
        glyphLayout = new GlyphLayout(endFont, stars + "");
    }

    private void drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        float length = screenWidth/6;
        for(int i = 0; i < screenWidth/length; i++) {
            float pos = (i+1) * length;
            shapeRenderer.rect(pos, 0, 1, screenHeight);
        }
        for(int i = 0; i < screenHeight/length; i++) {
            float pos = (i+1) * length;
            shapeRenderer.rect(0, pos, screenWidth, 1);
        }
        shapeRenderer.end();
    }

    private void drawScreen(float delta) {
        boolean run = isGameStateRun();
        boolean create = isGameStateCreate();

        camera.update();
        //batch.setProjectionMatrix(camera.combined);
        //viewport.apply();
        /*float modelScreenWidth = 1080;
        camera.zoom = modelScreenWidth / screenWidth;*/

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0);
        batch.end();

        if (create) {
            drawGrid();
        }

        batch.begin();
        if (isGameStateRunOrCreateRun()) {
            for (ShootingStar shootingStar : shootingStars) {
                if (shootingStar.active) {
                    shootingStar.draw();
                }
            }
        }
        tutorial.draw();
        portal.draw();
        wormholeManager.draw(delta);
        planetManager.draw(delta);
        if (create || (run && !shipInMotion)) {
            barrierArrows.draw(create);
        }
        //planetManager.drawComets();
        arrow.draw();
        if (shipExists) {
            //if(brokenShip == null) {
            ship.drawSprite(delta);
            //}
            //else {
            //    brokenShip.draw();
            // }
        }
        //if(brokenShipExists && brokenShip.isActive()) {
        if (shipBreakInEffect) {
            brokenShip.draw();
        }
        if (!ship.onScreen() && shipExists) {
            offScreenTracker.draw();
        }
        //debugRenderer.render(world, debugMatrix);
        if (endFont != null && star != null) {
            endFont.draw(batch, glyphLayout, star.getX() + 200 + glyphLayout.width / 2, star.getY() + glyphLayout.height / 2);
        }
        batch.end();
        if (isGameStateRunOrCreateRun()) {
            timer.draw(delta);
        }
        //updateTimer(delta);
        HUDStage.draw();
        batch.setColor(Color.WHITE);
        screenFade(delta);
    }

    public boolean isGameStateRun() {
        return gameState == EnumGameState.RUN;
    }

    public boolean isGameStateCreate() {
        return gameState == EnumGameState.CREATE;
    }

    public boolean isGameStateCreateRun() {
        return gameState == EnumGameState.CREATE_RUN;
    }

    public boolean isGameStateCreateRunPaused() {
        return gameState == EnumGameState.CREATE_RUN_PAUSED;
    }

    public boolean isGameStatePause() {
        return gameState == EnumGameState.PAUSE;
    }

    public boolean isGameStateRunOrCreate() {
        return isGameStateRun() || isGameStateCreate();
    }

    public boolean isGameStateRunOrCreateRun() {
        return isGameStateRun() || isGameStateCreateRun();
    }

    public boolean isGameStateCreateOrCreateRun() {
        return isGameStateCreate() || isGameStateCreateRun();
    }

    public boolean isGameStateRunOrCreateOrCreateRun() {
        return isGameStateRun() || isGameStateCreateOrCreateRun();
    }

    /** we instantiate this vector and the callback here so we don't irritate the GC **/
    /*Vector3 testPoint = new Vector3();
    QueryCallback callback = new QueryCallback() {
        @Override public boolean reportFixture (Fixture fixture) {
            // if the hit point is inside the fixture of the body
            // we report it
            if (fixture.testPoint(testPoint.x, testPoint.y)) {
                queriedBody = fixture.getBody();
                return false;
            } else
                return true;
        }
    };*/

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        //Gdx.app.log("GameCore.resize()", "resizing. updating viewport and resizing screen width/height");
        setScreenWidthAndHeight();
    }

    @Override
    public void pause() {
        saveProfile();
        if(isGameStateRun()) {
            gameState = EnumGameState.PAUSE;
        }
        else if(isGameStateCreateRun()) {
            gameState = EnumGameState.CREATE_RUN_PAUSED;
        }
    }

    @Override
    public void resume() {
        screenDrawn = false;
        if(isGameStatePause()) {
            gameState = EnumGameState.RUN;
        }
        else if(isGameStateCreateRunPaused()) {
            gameState = EnumGameState.CREATE_RUN;
        }
    }

    @Override
    public void hide() {
        gameState = EnumGameState.STOPPED;
        //dispose();
        //Call from here? Want a separate instance every time
        //ScreenManager.getInstance().dispose(ScreenEnum.GAME_CORE);
    }

    @Override
    public void dispose() {
        //Gdx.app.log("GameCore.dispose()", "disposing");
        saveProfile();
        HUDStage.dispose();
        background.dispose();
        planetManager.dispose();
        wormholeManager.dispose();
        //portal.dispose();
        shapeRenderer.dispose();
        //debugRenderer.dispose();
        //batch.dispose(); //disposed already because stage?
        world.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        x = Utility.unprojectX(x);
        y = Utility.unprojectY(y);
        firstGesture = true;
        if (isGameStateCreate()) {
            if (planetManager.select(x, y)) {
                wormholeManager.unselect();
                planetManager.unselectComet();
                portal.unselect();
            } else if (wormholeManager.select(x, y)) {
                planetManager.unselect();
                planetManager.unselectComet();
                portal.unselect();
            } else if (planetManager.selectComet(x, y)) {
                planetManager.unselect();
                wormholeManager.unselect();
                portal.unselect();
            } else if (portal.select(x, y)) {
                planetManager.unselect();
                planetManager.unselectComet();
                wormholeManager.unselect();
            } else if (barrierArrows.tap(x, y)) {
                unselectAll();
            } else if (createButtonMode == EnumCreateButtonMode.PLANET && planetManager.addPlanet(x, y)) {
                unsavedChanges = true;
                wormholeManager.unselect();
                portal.unselect();
                planetManager.unselectComet();
                planetManager.setSpritePosition();
                planetManager.assignGravities(ship);
            } else if (createButtonMode == EnumCreateButtonMode.WORMHOLE && wormholeManager.addWormholePair(x, y, false)) {
                unsavedChanges = true;
                planetManager.unselect();
                planetManager.unselectComet();
                portal.unselect();
            } else if (createButtonMode == EnumCreateButtonMode.MULTI_WORMHOLE && wormholeManager.addWormholePair(x, y, true)) {
                unsavedChanges = true;
                planetManager.unselect();
                planetManager.unselectComet();
                portal.unselect();
            }
            else {
                unselectAll();
            }
        }
        else {
            defaultTapAction();
        }
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        x = Utility.unprojectX(x);
        y = Utility.unprojectY(y);
        firstGesture = true;
        //Gdx.app.log("GameCore.longPress", "x, y: " + x + ", " + y);
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        //don't think unprojection should be required here
        firstGesture = true;
        //Gdx.app.log("GameCore.fling", "velocities: " + velocityX + ", " + velocityY);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        x = Utility.unprojectX(x);
        y = Utility.unprojectY(y);
        if (planetManager != null) {
            if (planetManager.lastAction.equals("zoom")) {
                planetManager.lastAction = "pan attempt";
                return true;
            } else if (isGameStateCreate()) {
                if (panState != null) {
                    switch (panState) {
                        case "planet":
                            planetManager.pan(x, y);
                            break;
                        case "portal":
                            portal.pan(x, y);
                            break;
                        case "wormhole":
                            wormholeManager.pan(x, y);
                            break;
                        case "comet":
                            planetManager.cometPan(x, y);
                            break;
                    }
                } else {
                    if (planetManager.pan(x, y)) {
                        panState = "planet";
                        wormholeManager.unselect();
                        planetManager.select(x, y);
                        portal.unselect();
                    } else if (portal.pan(x, y)) {
                        panState = "portal";
                        portal.select();
                        planetManager.unselect();
                        wormholeManager.unselect();
                    } else if (wormholeManager.pan(x, y)) {
                        panState = "wormhole";
                        planetManager.unselect();
                        portal.unselect();
                        wormholeManager.select(x, y);
                    } else if (planetManager.cometPan(x, y)) {
                        panState = "comet";
                        wormholeManager.unselect();
                        portal.unselect();
                    }
                }
            }
        }
        if (!shipInMotion && (isGameStateRunOrCreate())
                && (panState == null || panState.equals("arrow")) && firstGesture && arrow.touchDown(x, y)) {
            panState = "arrow";
            tutorial.stopSwipeHandPullShipMotion();
        }
        else if(isGameStateCreate()) {
            if (panState != null) {
                if (panState.equals("barrierArrows")) {
                    barrierArrows.touchDown(x, y);
                }
            }
            else {
                if (firstGesture && barrierArrows.touchDown(x, y)) {
                    panState = "barrierArrows";
                    planetManager.unselect();
                    wormholeManager.unselect();
                }
            }
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        x = Utility.unprojectX(x);
        y = Utility.unprojectY(y);
        firstGesture = true;
        if (!shipInMotion) {
            shipInMotion = arrow.touchUp(x, y);
        }
        if(isGameStateCreate()) {
            if (barrierArrows.touchUp() || planetManager.touchStop() ||
                        portal.panStop() || wormholeManager.touchStop()) {
                unsavedChanges = true;
            }
        }
        panState = null;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        initialPointer1 = Utility.unproject(initialPointer1.x, initialPointer1.y);
        initialPointer2 = Utility.unproject(initialPointer2.x, initialPointer2.y);
        pointer1 = Utility.unproject(pointer1.x, pointer1.y);
        pointer2 = Utility.unproject(pointer2.x, pointer2.y);

        planetManager.zoom(initialPointer1, initialPointer2, pointer1, pointer2);
        firstGesture = true;
        return false;
    }

    @Override
    public void pinchStop() {
        unsavedChanges = true;
    }

    //create 0 is default creation mode
    //create -1 is temp level in creation mode
    //create (>0) is temp level in creation mode corresponding to user made level
    public void saveTempLevel() {
        int level = -1;

        if (main.levelPack.equals("create")) {
            main.level = level;
        }
        else if (main.levelPack.equals("Edit My Levels")) {
            level = main.level;
        }

        LoadedLevel loadedLevel = createLoadedLevel(level, Integer.toString(level));
        Utility.saveJSONLevel(loadedLevel, main.levelPack);
    }

    public void saveUserLevel(String levelName) {
        int level = 1;

        String levelPack = "My Levels";

        // Add a level to existingLevels
        // If the level name already exists, want to overwrite it
        if ((main.levelPack.equals("Edit My Levels") || main.levelPack.equals("create"))
                && main.profile.myLevelsNames.contains(levelName)) {
            level = main.profile.myLevelsNames.indexOf(levelName) + 1;
        }
        else if (main.profile.existingLevels.containsKey(levelPack)) {
            level = main.profile.existingLevels.get(levelPack) + 1;
            main.profile.existingLevels.put(levelPack, level);
        }

        LoadedLevel loadedLevel = createLoadedLevel(level, levelName); //levelName if string
        Utility.saveJSONLevel(loadedLevel, levelPack);
        main.levelName = levelName;
    }

    private LoadedLevel createLoadedLevel(int level, String levelName) {
        LoadedLevel loadedLevel = new LoadedLevel();
        loadedLevel.level = level;
        loadedLevel.levelName = levelName;
        loadedLevel.portalx = (portal.position.x - halfScreenWidth + portal.pixelRadius);
        loadedLevel.portaly = portal.position.y + portal.pixelRadius;
        loadedLevel.portalMinTime = portal.minTime;
        loadedLevel.planets = new Array<>();
        for(int i = 0; i < planetManager.spaceObjects.size; i++) {
            LoadedPlanet loadedPlanet = new LoadedPlanet();
            SpaceObject planet = planetManager.spaceObjects.get(i);
            loadedPlanet.x = planet.getXinPixels() - halfScreenWidth;
            loadedPlanet.y = planet.getYinPixels();
            loadedPlanet.diameter = planet.getWidth();
            if (planet.color != null && !planet.color.equals("")) {
                loadedPlanet.color = planet.color;
            }

            loadedPlanet.comets = new Array<>();
            if(planetManager.cometManagers.size > 0) {
                for (SpaceObject comet : planetManager.cometManagers.get(i).spaceObjects) {
                    Comet comet1 = (Comet) comet;
                    LoadedComet loadedComet = new LoadedComet();
                    loadedComet.x = comet.startingX;
                    loadedComet.y = comet.startingY;
                    loadedComet.diameter = comet.getWidth();
                    loadedComet.rotateClockwise = comet1.rotateClockwise;
                    loadedPlanet.comets.add(loadedComet);
                }
            }
            loadedLevel.planets.add(loadedPlanet);
        }

        loadedLevel.wormholes = new Array<>();
        for(int i = 0; i < wormholeManager.wormholes.size; i+=2) {
            LoadedWormhole loadedWormhole = new LoadedWormhole();
            Wormhole wormhole1 = wormholeManager.wormholes.get(i);
            Wormhole wormhole2 = wormholeManager.wormholes.get(i+1);
            loadedWormhole.x = wormhole1.position.x - halfScreenWidth + wormhole1.pixelRadius;
            loadedWormhole.y = wormhole1.position.y + wormhole1.pixelRadius;
            loadedWormhole.x2 = wormhole2.position.x - halfScreenWidth + wormhole2.pixelRadius;
            loadedWormhole.y2 = wormhole2.position.y + wormhole2.pixelRadius;
            if (wormhole1.color != null && !wormhole1.color.equals("")) {
                loadedWormhole.color1 = wormhole1.color;
            }
            if (wormhole2.color != null && !wormhole2.color.equals("")) {
                loadedWormhole.color1 = wormhole2.color;
            }
            loadedWormhole.multi = wormhole1.multi;
            loadedLevel.wormholes.add(loadedWormhole);
        }

        loadedLevel.leftBarrier = arrow.leftBarrier;
        loadedLevel.rightBarrier = arrow.rightBarrier;
        loadedLevel.barrierArrows = barrierArrows.getBarrierArrowOn();
        return loadedLevel;
    }

    /**
     * Fade screen in and out as appropriate
     * @param delta time that has passed
     */
    private void screenFade(float delta) {
        if(fadeIn) {
            fade -= delta;
            if (fade <= 0) {
                fade = FADE;
                fadeIn = false;
            } else {
                drawFade(fade);
            }
        }
        else if(fadeOut) {
            if (isGameStateCreateRun()) {
                endPreFadeElapsedTime = TimeUtils.timeSinceMillis(endPreFadeStartTime);
                float timeToWaitBeforeStartingFade = 500;
                if (endPreFadeElapsedTime < timeToWaitBeforeStartingFade) {
                    return;
                }
            }
            fade -= delta;
            if (fade <= 0) {
                drawFade(1);
                fade = FADE;
                fadeOut = false;
                saveProfile();
                if (isGameStateCreateRun() || endAction == null) {
                    endAction = EnumEndAction.RESTART;
                }
                switch (endAction) {
                    case RESTART: restart();
                        break;
                    case NEXT_LEVEL: nextLevel();
                        break;
                    case LEVELS_MENU: levelsMenu();
                        break;
                    case MAIN_MENU: mainMenu();
                        break;
                }
                endAction = EnumEndAction.NO_ACTION;
            } else {
                drawFade(FADE - fade);
            }
        }
    }

    private void drawFade(float currentFade) {
        float scale = currentFade / FADE;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, scale);
        shapeRenderer.rect(0, 0, screenWidthAdjusted, screenHeightAdjusted);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    protected void fadeOut() {
        endPreFadeStartTime = TimeUtils.millis();
        fadeOut = true;
    }

    private void restart() {
        endAction = EnumEndAction.RESTART;
        unselectAll();
        if(isGameStateCreateRun()) {
            saveTempLevel();
        }
        Utility.reloadScreen(EnumScreen.GAME_CORE);
    }

    private void nextLevel() {
        main.level++;
        restart();
    }

    private void levelsMenu() {
        Utility.switchScreens(EnumScreen.GAME_CORE, EnumScreen.LEVELS_MENU);
    }

    private void mainMenu() {
        Utility.switchScreens(EnumScreen.GAME_CORE, EnumScreen.MAIN_MENU);
    }

    public boolean confirmShipSuccess() {
        boolean success = true;
        if (!timer.pastMinTime()) {
            main.delayedToasts.add(new Toast("Ship entered portal before available", HUDStage, Toast.TOAST_SHORT, Toast.TOAST_SMALL));
            success = false;
        }
        if (!wormholeManager.allMultiesPassed()) {
            main.delayedToasts.add(new Toast("Warped (white) wormholes not passed", HUDStage, Toast.TOAST_SHORT, Toast.TOAST_SMALL));
            success = false;
        }
        return success;
    }

    public boolean shipSuccess() {
        shipSuccessful = confirmShipSuccess();
        if (shipSuccessful) {
            main.soundHandler.playPortalSuccess();
            main.profile.setUnlockedLevel(main.levelPack, main.level + 1);
        }
        if (isGameStateRun()) {
            main.profile.setTutorialComplete();
        }
        else {
            main.profile.setCreateTutorialComplete();
        }
        if (endAction == null) {
            shipExists = false;
            shipInPortal = true;
            if (isGameStateCreateRun() || !shipSuccessful) {
                endAction = EnumEndAction.RESTART;
                fadeOut();
            } else {
                endAction = EnumEndAction.NO_ACTION;
            }
        }
        return shipSuccessful;
    }

    public void shipFail() {
        changeBrokenShipPosition = true;
        endAction = EnumEndAction.RESTART;
        fadeOut();
    }

    //when there is a ship launch in CREATE, gameState should switch to CREATE_RUN
    public void launchShip() {
        unselectAll();
        if(isGameStateCreate()) {
            gameState = EnumGameState.CREATE_RUN;
        }
    }

    public void unselectAll() {
        planetManager.unselect();
        planetManager.unselectComet();
        wormholeManager.unselect();
        portal.unselect();
    }

    public void saveProfile() {
        main.persistProfile();
    }

    public void createBrokenShip() {
        brokenShip = new BrokenShip(this);
    }

    public void breakShip() {
        shipExists = false;
        brokenShip.enablePieces(ship);
        brokenShip.applyLinearImpulses(shipCollisionImpulse);
    }

    public void displayDelayedToasts() {
        if (main.delayedToasts.size() > 0) {
            Toast toast = main.delayedToasts.pop();
            toast.stage = HUDStage;
            toast.setPositionBottom();
            toast.startToastDelayed(.75f);
        }
    }

    private void defaultTapAction() {
        if (shipSuccessful && nextLevelAvailable()) {
            nextLevel();
        }
        else if (!fadeOut) {
            restart();
        }
    }
}
