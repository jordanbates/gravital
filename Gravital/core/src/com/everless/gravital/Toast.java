package com.everless.gravital;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Created by Jordan on 9/9/2015.
 * Actor for toasts
 */
public class Toast extends TextButton {
    protected Stage stage;
    public final static int TOAST_SHORT = 1;
    public final static int TOAST_MEDIUM = 3;
    public final static int TOAST_LONG = 7;
    public final static int TOAST_SMALL = 200;
    public final static int TOAST_LARGE = 400;
    private int duration;
    private float stageWidth, stageHeight;

    public Toast(String text, Stage stage, int duration, int height) {
        super(text, Main.getInstance().skin.get("border", TextButton.TextButtonStyle.class));
        setDisabled(true);
        this.stage = stage;
        this.duration = duration;
        setSize(650, height);
        getLabel().setFontScale(1);
        getLabel().setWrap(true);
        setTouchable(Touchable.disabled);

        stageWidth = stage.getViewport().getWorldWidth();
        stageHeight = stage.getViewport().getWorldHeight();

        //this.setBounds(0, 0, this.getWidth(), this.getHeight());
        setPositionCenter();
        getColor().a = 0;

        super.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                closeToast();
                return true;
            }
        });
    }

    public void setPositionCenter() {
        //setPosition(GameCore.halfScreenWidth - this.getWidth() / 2, GameCore.halfScreenHeight - this.getHeight() / 2);
        setPosition((stageWidth - this.getWidth()) / 2, (stageHeight - this.getHeight()) / 2);
    }

    public void setPositionTop() {
        //setPosition(GameCore.halfScreenWidth - this.getWidth() / 2, GameCore.screenHeight - this.getHeight() - 30);
        setPosition((stageWidth - this.getWidth()) / 2, stageHeight - this.getHeight() - 30);
    }

    public void setPositionAlmostTop() {
        //setPosition(GameCore.halfScreenWidth - this.getWidth() / 2, GameCore.screenHeight - this.getHeight() - 230);
        setPosition((stageWidth - this.getWidth()) / 2, stageHeight - this.getHeight() - 230);
    }

    public void setPositionBottom() {
        //setPosition(GameCore.halfScreenWidth - this.getWidth() / 2, this.getHeight() + 30);
        setPosition((stageWidth - this.getWidth()) / 2, this.getHeight() + 30);
    }

    public void startToast() {
        addAction(
                Actions.sequence(
                        Actions.alpha(0f),
                        Actions.fadeIn(0.5f),
                        Actions.delay(duration, Actions.fadeOut(0.5f)),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                Toast.super.remove();
                            }
                        })
                )
        );
        stage.addActor(this);
    }

    public void startToastDelayed(float delay) {
        addAction(
                Actions.sequence(
                        Actions.alpha(0f),
                        Actions.delay(delay, Actions.fadeIn(0.5f)),
                        Actions.delay(duration, Actions.fadeOut(0.5f)),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                Toast.super.remove();
                            }
                        })
                )
        );
        stage.addActor(this);
    }

    public void closeToast() {
        this.remove();
    }
}

//This is the new/not working project