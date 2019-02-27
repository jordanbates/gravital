package com.everless.gravital;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Objects;

/**
 * Created by Jordan on 8/7/2015.
 * Collision detection callbacks
 */
public class ObjectContactListener implements ContactListener {
    private GameCore gameCore;
    private boolean triggered = false;
    private long startTime, elapsedTime;
    Main main;

    public ObjectContactListener(GameCore gameCore) {
        main = Main.getInstance();
        this.gameCore = gameCore;
        startTime = TimeUtils.millis();
        elapsedTime = 10000;
    }

    @Override
    public void beginContact(Contact contact) {
        Entity dataA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity dataB = (Entity) contact.getFixtureB().getBody().getUserData();
        System.out.println(dataA.type + " collided with " + dataB.type);
        //if (dataA_o instanceof Entity && dataB_o instanceof Entity) {
            //Entity dataA = (Entity) dataA_o;
            //Entity dataB = (Entity) dataB_o;
            if (Objects.equals(dataA.type, "ship")) {
                if (Objects.equals(dataB.type, "planet")) {
                    shipCollision(dataA, 0);
                } else if (Objects.equals(dataB.type, "comet")) {
                    shipCollision(dataA, 2);
                }
            }
            else if (Objects.equals(dataA.type, "shipPiece") || Objects.equals(dataB.type, "shipPiece")) {
                main.soundHandler.play();
            }
            else { //TEMP
                elapsedTime = TimeUtils.timeSinceMillis(startTime);
                if (elapsedTime > 1000) {
                    //main.soundHandler.play();
                    startTime = TimeUtils.millis();
                }
            }
            /*else if (dataA == "planet") {
                if (dataB == "ship") {
                    shipCollision(contact, 1);
                } else if (dataB == "comet") {
                    cometPlanetCollision(contact, 1);
                }
            } else if (dataA == "comet") {
                if (dataB == "ship") {
                    //shipCollision(contact, 3);
                } else if (dataB == "planet") {
                    cometPlanetCollision(contact, 0);
                }
            }*/
        /*}
        else {
            System.out.println("NOT both entities: " + dataA_o + " collided with " + dataB_o);
        }*/
    }

    protected void shipCollision(Entity ship, int option) {
        if (!GameCore.shipInPortal) {
            if (!GameCore.shipInPortal) {
                main.soundHandler.play();
            }
            if (!triggered) {
                gameCore.shipFail();
                if (option % 2 == 0) {
                    //Ship is fixture A
                    //GameCore.world.destroyBody(contact.getFixtureA().getBody());

                    //contact.getFixtureA().getBody().setActive(false);
                    ship.die();
                } else {
                    //Ship is fixture B
                    //GameCore.world.destroyBody(contact.getFixtureB().getBody());

                    //contact.getFixtureB().getBody().setActive(false);
                    ship.die();
                }
                triggered = true;
            }
        }
    }

    protected void cometPlanetCollision(Contact contact, int option) {
        //Was going to use this to stop comet particle effects
        //Realized it's more work than I want to do right now
        if(option % 2 == 0) {
            //Ship is fixture A
        }
        else {
            //Ship is fixture B
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Entity dataA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity dataB = (Entity) contact.getFixtureA().getBody().getUserData();
        if(dataA.type.equals("ship") ||dataB.type.equals("ship")) {
            if (!gameCore.shipCollisionImpulseSet) {
                gameCore.shipCollisionImpulse = impulse.getNormalImpulses()[0];
                gameCore.shipCollisionImpulseSet = true;
                System.out.println("Ship detected in postSolve. Impulse: " + gameCore.shipCollisionImpulse);
            }
        }
    }
}
