package com.everless.gravital;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * http://gamedev.stackexchange.com/a/22943
 */
public class Entity
{
    protected Body body;
    protected EntityManager entityManager;
    protected String type;

    public Entity(Body body, EntityManager entityManager, String type) {
        this.body = body;
        this.entityManager = entityManager;
        this.type = type;
    }

    //Call from your contact listener when the entity expires
    //body.userData is set to the Entity representing that body
    //so you can get access to the Entity from the Body, as vice versa.
    public void die()
    {
        entityManager.removeEntity(this);
    }
}