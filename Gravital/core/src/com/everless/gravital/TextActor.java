package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Align;

public class TextActor extends Actor {
    BitmapFont font;
    String text;
    ScrollPane scroll;
    float width, height, drawWidth;

    public TextActor(BitmapFont font, String text, Stage stage, float verticalOffSetFromTop){
        this.font = font;
        this.text = text;
        width = stage.getViewport().getWorldWidth();
        height = stage.getViewport().getWorldHeight() - verticalOffSetFromTop;
        drawWidth = width - 50;
    }

    public void setScroll(ScrollPane scroll) {
        this.scroll = scroll;
    }

    public void setDrawWidth(float width) {
        drawWidth = width;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (scroll != null) {
            font.draw(batch, text, (width - drawWidth) / 2, height + scroll.getScrollY(),
                    drawWidth, Align.center, true);
        }
        else {
            font.draw(batch, text, (width - drawWidth) / 2, height,
                    drawWidth, Align.center, true);
        }
    }
}
