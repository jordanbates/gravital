package com.everless.gravital;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

/**
 * Created by Jordan on 7/20/2016.
 * All animations, graphics and dialogs concerning tutorials here
 */
public class Tutorial {
    private GameCore gameCore;
    public Main main;
    private Image hand = null;
    protected BitmapFont font = null;
    protected String level1Text = null, level2Text = null, level3Text = null, level4Text = null, level5Text = null, level6Text = null;
    protected Vector2 level1TextPos = null, level2TextPos = null, level3TextPos = null, level4TextPos = null, level5TextPos = null, level6TextPos = null;
    protected String wormholeLevel1Text = null, barrierLevel1Text = null, timingLevel1Text = null, whiteWormholeLevel1Text = null;
    protected Vector2 wormholeLevel1TextPos = null, barrierLevel1TextPos = null, timingLevel1TextPos = null, whiteWormholeLevel1TextPos = null;
    protected String createText1 = null, createText2 = null, createText3 = null, createText4 = null, createText5 = null;
    protected Vector2 createTextPos1 = null, createTextPos2 = null, createTextPos3 = null, createTextPos4 = null, createTextPos5 = null;

    public Tutorial(GameCore gameCore) {
        this.gameCore = gameCore;
        main = Main.getInstance();
        font = main.skin.getFont("font_g_othic_56pt_extra");
    }

    public void startSwipeHandPullShipMotion() {
        if (hand == null) {
            hand = new Image(main.assetManager.get("tutorial/hand.png", Texture.class));
            //nameImage.setWidth();
            hand.setRotation(90);
            hand.setPosition(GameCore.screenWidth/2 + hand.getHeight() - gameCore.ship.getWidth() / 2, 0);
            //star.setOrigin(1); //https://libgdx.badlogicgames.com/nightlies/docs/api/constant-values.html#com.badlogic.gdx.utils.Align.center
            hand.addAction(
                    Actions.repeat(
                            -1, Actions.sequence(
                                    Actions.delay(1, Actions.moveBy(0, 300, 1.5f)),
                                    Actions.delay(1, Actions.moveBy(0, -300, 0))
                            ))
            );
        }
        gameCore.HUDStage.addActor(hand);
    }

    public void stopSwipeHandPullShipMotion() {
        if (hand != null) {
            hand.remove();
        }
    }

    public void draw() {
        if (level1Text != null) {
            font.draw(GameCore.batch, level1Text, level1TextPos.x, level1TextPos.y,
                    350, Align.center, true);
            ////Gdx.app.log("Tutorial.draw()", "drawing level 1 text");
        }
        else if (level2Text != null) {
            font.draw(GameCore.batch, level2Text, level2TextPos.x, level2TextPos.y,
                    300, Align.center, true);
        }
        else if (level3Text != null) {
            font.draw(GameCore.batch, level3Text, level3TextPos.x, level3TextPos.y,
                    500, Align.center, true);
        }
        else if (level4Text != null) {
            font.draw(GameCore.batch, level4Text, level4TextPos.x, level4TextPos.y,
                    600, Align.center, true);
        }
        else if (level5Text != null) {
            font.draw(GameCore.batch, level5Text, level5TextPos.x, level5TextPos.y,
                    600, Align.center, true);
        }
        else if (level6Text != null) {
            font.draw(GameCore.batch, level6Text, level6TextPos.x, level6TextPos.y,
                    330, Align.center, true);
        }
        else if (wormholeLevel1Text != null) {
            font.draw(GameCore.batch, wormholeLevel1Text, wormholeLevel1TextPos.x, wormholeLevel1TextPos.y,
                    360, Align.center, true);
        }
        else if (barrierLevel1Text != null) {
            font.draw(GameCore.batch, barrierLevel1Text, barrierLevel1TextPos.x, barrierLevel1TextPos.y,
                    600, Align.center, true);
        }
        else if (timingLevel1Text != null) {
            font.draw(GameCore.batch, timingLevel1Text, timingLevel1TextPos.x, timingLevel1TextPos.y,
                    500, Align.center, true);
        }
        else if (whiteWormholeLevel1Text != null) {
            font.draw(GameCore.batch, whiteWormholeLevel1Text, whiteWormholeLevel1TextPos.x, whiteWormholeLevel1TextPos.y,
                    600, Align.center, true);
        }
        else if (createText1 != null) {
            font.draw(GameCore.batch, createText1, createTextPos1.x, createTextPos1.y,
                    370, Align.center, true);
            /*font.draw(GameCore.batch, createText2, createTextPos2.x, createTextPos2.y,
                    700, Align.center, true);*/
            font.draw(GameCore.batch, createText3, createTextPos3.x, createTextPos3.y,
                    350, Align.center, true);
            font.draw(GameCore.batch, createText4, createTextPos4.x, createTextPos4.y,
                    450, Align.center, true);
            /*font.draw(GameCore.batch, createText5, createTextPos5.x, createTextPos5.y,
                    750, Align.center, true);*/
        }
    }

    public void startLevel1Text() {
        level1Text = "Pull the ship to launch it into the portal";
        level1TextPos = new Vector2(50, Utility.MODEL_SCREEN_HEIGHT / 2 + 100);
    }

    public void startLevel2Text() {
        level2Text = "Avoid hitting the planet and use its gravity to guide the ship";
        level2TextPos = new Vector2(gameCore.HUDStage.getViewport().getWorldWidth() - 350, Utility.MODEL_SCREEN_HEIGHT / 2 + 200);
    }

    public void startLevel3Text() {
        level3Text = "Enter portal before running out of oxygen";
        level3TextPos = new Vector2(300, gameCore.HUDStage.getViewport().getWorldHeight() - 150);
    }

    public void startLevel4Text() {
        level4Text = "Tap anywhere on the screen to restart";
        level4TextPos = new Vector2(300, gameCore.HUDStage.getViewport().getWorldHeight() - 400);
    }

    public void startLevel5Text() {
        level5Text = "The amount you pull the arrow out determines the speed of the spaceship";
        level5TextPos = new Vector2(150, 600);
    }

    public void startLevel6Text() {
        level6Text = "Hint: After completing a level, tap anywhere on the screen to go to the next level";
        level6TextPos = new Vector2(700, 1200);
    }

    public void startWormholeLevel1Text() {
        wormholeLevel1Text = "Wormholes can help to travel across the screen";
        wormholeLevel1TextPos = new Vector2(40, 900);
    }

    public void startBarrierLevel1Text() {
        barrierLevel1Text = "The barrier arrows will restrict which direction you can launch";
        barrierLevel1TextPos = new Vector2(220, 1280);
    }

    public void startTimingLevel1Text() {
        timingLevel1Text = "The portal cannot be entered until it turns white";
        timingLevel1TextPos = new Vector2(150, 1200);
    }

    public void startWhiteWormholeLevel1Text() {
        whiteWormholeLevel1Text = "Warped (white) wormholes MUST be traveled through";
        whiteWormholeLevel1TextPos = new Vector2(100, 850);
    }

    public void startCreateText() {
        createText1 = "Create planets and wormholes by clicking anywhere on the screen";
        //createText2 = "";
        createText3 = "Launch the ship at any time to test your level";
        createText4 = "Click on the faded Barrier Arrows to activate them";
        //createText5 = "Select planet or wormhole mode from the drawer, then click anywhere on the screen to create one";
        createTextPos1 = new Vector2(gameCore.HUDStage.getViewport().getWorldWidth() - 400, gameCore.HUDStage.getViewport().getWorldHeight() - 300);
        //createTextPos2 = new Vector2(350, gameCore.HUDStage.getViewport().getWorldHeight() - 100);
        createTextPos3 = new Vector2(190, 1000);
        createTextPos4 = new Vector2(600, 400);
        //createTextPos5 = new Vector2(300, 1050);
    }
}
