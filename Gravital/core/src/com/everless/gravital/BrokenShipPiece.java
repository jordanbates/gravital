package com.everless.gravital;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Jordan on 9/11/2015.
 * Broken ship piece
 */
public class BrokenShipPiece extends SpaceObject {

    public BrokenShipPiece(GameCore gameCore, Texture texture, float x, float y, float rotation) {
        super(gameCore, texture);
        //createBodyWrapper(x - GameCore.halfScreenWidth, y);
        createBodyWrapper(50, 500);
        bodyWrapper.createShipBody();
        //bodyWrapper.setUserData("shipPiece");
        gameCore.entityManager.createEntity(bodyWrapper.body, "shipPiece");
    }
}
