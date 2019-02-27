package com.everless.gravital;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jordan on 9/10/2015.
 * Handles pieces of the broken ship
 */
public class BrokenShip {
    protected Array<BrokenShipPiece> brokenShipPieces = new Array<>();
    public Main main;

    public BrokenShip(GameCore gameCore) {
        main = Main.getInstance();
        float shipX = GameCore.screenWidth/2;
        float shipY = GameCore.screenHeight/2;
        float shipWidth = 50;
        float shipHeight = 100;
        float rotation = 0;

        BrokenShipPiece brokenShipPiece1 = new BrokenShipPiece(gameCore, main.assetManager.get("ship/ship_break_1.png", Texture.class),
                shipX, shipY, rotation);
        BrokenShipPiece brokenShipPiece2 = new BrokenShipPiece(gameCore, main.assetManager.get("ship/ship_break_2.png", Texture.class),
                shipX, shipY - shipHeight, rotation);
        BrokenShipPiece brokenShipPiece3 = new BrokenShipPiece(gameCore, main.assetManager.get("ship/ship_break_3.png", Texture.class),
                shipX - shipWidth, shipY - shipHeight, rotation);
        brokenShipPieces.add(brokenShipPiece1);
        brokenShipPieces.add(brokenShipPiece2);
        brokenShipPieces.add(brokenShipPiece3);

        disablePieces();
    }

    protected boolean isActive() {
        return brokenShipPieces.get(0).bodyWrapper.body.isActive();
    }

    protected void disablePieces() {
        for(BrokenShipPiece piece : brokenShipPieces) {
            piece.bodyWrapper.body.setActive(false);
        }
    }

    protected void enablePieces(Ship ship) {
        float x = Utility.pixelsToMeters(ship.getXinPixels());
        float y = Utility.pixelsToMeters(ship.getYinPixels());
        float bottomX = Utility.midPoint(ship.sprite.getVertices()[SpriteBatch.X1], ship.sprite.getVertices()[SpriteBatch.X4]);
        float bottomY = Utility.midPoint(ship.sprite.getVertices()[SpriteBatch.Y1], ship.sprite.getVertices()[SpriteBatch.Y4]);
        float leftX = Utility.midPoint(ship.sprite.getVertices()[SpriteBatch.X1], ship.sprite.getVertices()[SpriteBatch.X2]);
        float leftY = Utility.midPoint(ship.sprite.getVertices()[SpriteBatch.Y1], ship.sprite.getVertices()[SpriteBatch.Y2]);
        float topX = Utility.midPoint(ship.sprite.getVertices()[SpriteBatch.X2], ship.sprite.getVertices()[SpriteBatch.X3]);
        float topY = Utility.midPoint(ship.sprite.getVertices()[SpriteBatch.Y2], ship.sprite.getVertices()[SpriteBatch.Y3]);
        float midX = Utility.midPoint(bottomX, topX);
        float midY = Utility.midPoint(bottomY, topY);
        leftX = Utility.pixelsToMeters(leftX);
        leftY = Utility.pixelsToMeters(leftY);

        float angle = ship.bodyWrapper.body.getAngle();
        ////Gdx.app.log("enablePieces: ", "x: " + x + "y: " + y + "angle: " + angle);

        brokenShipPieces.get(0).bodyWrapper.body.setActive(true);
        brokenShipPieces.get(0).bodyWrapper.body.setTransform(leftX, leftY, angle);
        brokenShipPieces.get(1).bodyWrapper.body.setActive(true);
        brokenShipPieces.get(1).bodyWrapper.body.setTransform(x, y, angle);
        brokenShipPieces.get(2).bodyWrapper.body.setActive(true);
        //brokenShipPieces.get(2).bodyWrapper.body.setTransform(bottomX, bottomY, angle);
        brokenShipPieces.get(2).bodyWrapper.body.setTransform(x, y, angle);

        for(BrokenShipPiece piece : brokenShipPieces) {
            //piece.bodyWrapper.body.setActive(false);
            //piece.bodyWrapper.body.setTransform(x, y, angle);
        }
    }

    protected void applyLinearImpulses(float shipCollisionImpulse) {
        for(BrokenShipPiece piece : brokenShipPieces) {
            float x = Utility.randInt(0, (int) Utility.MODEL_SCREEN_WIDTH);
            float y = Utility.randInt(0, (int) Utility.MODEL_SCREEN_HEIGHT);
            shipCollisionImpulse = Math.max(0.5f, shipCollisionImpulse);
            applyLinearImpulse(piece, x, y, shipCollisionImpulse);
        }
    }

    private void applyLinearImpulse(BrokenShipPiece piece, float x, float y, float shipCollisionImpulse) {
        float deltaX = x - piece.getXinPixels();
        float deltaY = y - piece.getYinPixels();

        float scale = 0.13f;
        float force = shipCollisionImpulse * scale;

        float angle = (float) Math.atan2(deltaY, deltaX);
        piece.bodyWrapper.body.applyLinearImpulse(new Vector2((float) Math.cos(angle) * force,
                (float) Math.sin(angle) * force), piece.getWorldCenter(), true);
    }

    protected void draw() {
        for(BrokenShipPiece piece : brokenShipPieces) {
            piece.drawSprite();
            ////Gdx.app.log("BrokenShip.draw", "x: " + piece.getYinPixels() + " y: " + piece.getYinPixels());
        }
    }

    protected void setSpritePosition() {
        for(BrokenShipPiece piece : brokenShipPieces) {
            piece.setSpritePosition();
            piece.setSpriteRotation();
        }
    }
}
