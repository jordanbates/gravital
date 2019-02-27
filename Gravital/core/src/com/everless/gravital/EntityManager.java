package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.ArrayList;
import java.util.List;

/**
 * http://gamedev.stackexchange.com/a/22943
 */
public class EntityManager
{
    public List<Entity> entities = new ArrayList<>(); //extant entities
    //public List<Entity> entitiesToAdd = new ArrayList<Entity>(); //forthcoming entities
    public List<Entity> entitiesToRemove = new ArrayList<>(); //erstwhile entities <-- the important one for you.

    public void createEntity(Body body, String type) {
        Entity entity = new Entity(body, EntityManager.this, type);
        addEntity(entity);
        entity.body.setUserData(entity);
    }

    private void addEntity(Entity entity) {
        if (!entities.contains(entity)) {
            entities.add(entity);
        }
    }

    public void removeEntity(Entity entity) {
        //Gdx.app.log("removeEntity", "attempting to remove entity: " + entity.type);
        if (!entitiesToRemove.contains(entity)) {
            entitiesToRemove.add(entity);
            //Gdx.app.log("removeEntity", "successfully added entity to remove list: " + entity.type);
        }
    }

    public void remove() {
        for (Entity entity : entitiesToRemove) {
            if (entities.contains(entity)) {
                //GameCore.world.destroyBody(entity.body); //if this doesn't work, at least make inactive
                entity.body.setActive(false);
                entities.remove(entity);
            }
        }
        entitiesToRemove.clear();
    }
}