package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

/**
 * Created by Jordan on 9/21/2016.
 * Store dialog showing all purchase options
 */
public class DialogStore extends Dialog {
    String dialogText;
    Stage stage;
    Table contentTable2, contentTable3, buttonTable2, buttonTable3;
    public Main main;

    public DialogStore(Stage stage) {
        super("", Main.getInstance().skin);
        main = Main.getInstance();
        this.stage = stage;
        setDialog();
    }

    private void setDialog() {
        //add(contentTable2 = new Table(getSkin())).expand().fill();
        //row();
        //add(buttonTable2 = new Table(getSkin()));

        /*contentTable2.defaults().space(6);
        buttonTable2.defaults().space(6);*/

        /*buttonTable2.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                if (!values.containsKey(actor)) return;
                while (actor.getParent() != buttonTable2)
                    actor = actor.getParent();
                result(values.get(actor));
                if (!cancelHide) hide();
                cancelHide = false;
            }
        });*/

        //text(dialogText);
        getButtonTable().defaults().size(270, 120);
        getButtonTable().defaults().pad(55);
        text("Creation Mode");
        getContentTable().row().fillX();
        //row();
        //text2("\"Warped Wormholes\" Level Pack");
        text("\n\n\n\"Warped Wormholes\" Level Pack");
        //contentTable2.row();
        //text("\"The Timing Is Right\" Level Pack");


        Dialog purchaseButton = button("Purchase", "Creation Mode");
        //getButtonTable().getCell(purchaseButton).setActorX(700);
        getButtonTable().row();
        //button2("Exit", "Exit").setPosition(500, 300);
        Dialog exitButton = button("Exit", "Exit");
        //getButtonTable().getCell(exitButton).align(Align.right);

        /*Button buttonPurchase = new Button(Main.skin.get("planet", TextButton.TextButtonStyle.class)
        Window window = new Window("title", getSkin());*/

        setModal(true);
        setMovable(false);
        setWidth(Utility.MODEL_SCREEN_WIDTH - 20);
        setHeight(800);
        setPosition(stage.getViewport().getWorldWidth() / 2 - getWidth() / 2, stage.getViewport().getWorldHeight() - 1200);
    }

    /** Adds a label to the content table. The dialog must have been constructed with a skin to use this method. */
    public Dialog text2 (String text) {
        if (getSkin() == null)
            throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
        return text2(text, getSkin().get(Label.LabelStyle.class));
    }

    /** Adds a label to the content table. */
    public Dialog text2 (String text, Label.LabelStyle labelStyle) {
        return text2(new Label(text, labelStyle));
    }

    /** Adds the given Label to the content table */
    public Dialog text2 (Label label) {
        contentTable2.add(label);
        return this;
    }

    /** Adds a text button to the button table. The dialog must have been constructed with a skin to use this method.
     * @param object The object that will be passed to {@link #result(Object)} if this button is clicked. May be null. */
    public Dialog button2 (String text, Object object) {
        if (getSkin() == null)
            throw new IllegalStateException("This method may only be used if the dialog was constructed with a Skin.");
        return button2(text, object, getSkin().get(TextButton.TextButtonStyle.class));
    }

    /** Adds a text button to the button table.
     * @param object The object that will be passed to {@link #result(Object)} if this button is clicked. May be null. */
    public Dialog button2 (String text, Object object, TextButton.TextButtonStyle buttonStyle) {
        return button2(new TextButton(text, buttonStyle), object);
    }

    /** Adds the given button to the button table.
     * @param object The object that will be passed to {@link #result(Object)} if this button is clicked. May be null. */
    public Dialog button2 (Button button, Object object) {
        buttonTable2.add(button);
        setObject(button, object);
        return this;
    }

    @Override
    protected void result(Object object) {
        Gdx.input.setOnscreenKeyboardVisible(false);
        String pressed = object.toString();
        switch (pressed) {
            case "Creation Mode":
                main.iabInterface.purchaseCreate();
                break;
        }
    }
}
