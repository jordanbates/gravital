package com.everless.gravital;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by Jordan on 7/27/2015.
 *
 * Base class for body types
 */
public class BodyWrapper {
    protected Body body;
    protected Sprite sprite;
    protected float bodyWidth = 0.0f;
    protected float bodyHeight = 0.0f;
    protected float halfBodyWidth = 0.0f;
    protected float halfBodyHeight = 0.0f;
    protected float x = 0.0f;
    protected float y = 0.0f;
    private BodyDef bodyDef = new BodyDef();
    public boolean bodyExists = true;
    private float density, restitution;
    private float friction = 500;

    public BodyWrapper(Sprite sprite, float x, float y) {
        bodyWidth = sprite.getWidth();
        bodyHeight = sprite.getHeight();
        halfBodyWidth = bodyWidth / 2;
        halfBodyHeight = bodyHeight / 2;
        this.sprite = sprite;
        this.x = x;
        this.y = y;
    }

    public BodyWrapper(Sprite sprite, float x, float y, float width, float height) {
        bodyWidth = width;
        bodyHeight = height;
        halfBodyWidth = bodyWidth / 2;
        halfBodyHeight = bodyHeight / 2;
        this.sprite = sprite;
        this.x = x;
        this.y = y;
    }

    public void createShipBody() {
        //density = 0.05f; //normal rectangle
        density = .084f; //new smaller rectangle
        //density = 0.06666667f; //two rects
        //density = .1f; //triangle
        restitution = 1.0f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        setBodyDefPosition(bodyDef);
        body = GameCore.world.createBody(bodyDef);
        setShipShapeAndFixture();
    }

    public void createCometBody() {
        density = 5.0f;
        restitution = .01f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        setBodyDefPosition(bodyDef);
        body = GameCore.world.createBody(bodyDef);
        setShapeAndFixtureAsCircle();
    }

    public void createPlanetBody() {
        density = 0.1f;
        restitution = .01f;
        bodyDef.type = BodyDef.BodyType.StaticBody;

        setBodyDefPosition(bodyDef);
        body = GameCore.world.createBody(bodyDef);
        setShapeAndFixtureAsCircle();
    }

    public void createDenseSphereBody() {
        density = 500.0f;
        restitution = .01f;
        bodyDef.type = BodyDef.BodyType.StaticBody;

        setBodyDefPosition(bodyDef);
        body = GameCore.world.createBody(bodyDef);
        setShapeAndFixtureAsCircle();
    }

    public void createEdgeBody() {
        bodyDef.type = BodyDef.BodyType.StaticBody;

        setEdgeBodyDefPosition();
        body = GameCore.world.createBody(bodyDef);
        setShipShapeAndFixture(); //change if using
    }

    private void setShipShapeAndFixture() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Utility.pixelsToMeters(bodyWidth / 2), Utility.pixelsToMeters(bodyHeight / 2));

        /*Vector2[] vertices = new Vector2[3];
        vertices[0] = new Vector2(0, 0);
        vertices[1] = new Vector2(Utility.pixelsToMeters(bodyWidth), 0);
        vertices[2] = new Vector2(Utility.pixelsToMeters(bodyWidth/2), Utility.pixelsToMeters(bodyHeight));
        shape.set(vertices);*/

        /*shape.setAsBox(Utility.pixelsToMeters(bodyWidth / 2), Utility.pixelsToMeters(bodyHeight / 4));
        PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(Utility.pixelsToMeters(bodyWidth / 4), Utility.pixelsToMeters(bodyHeight / 4), new Vector2(0, Utility.pixelsToMeters(bodyHeight/2)), 0);
        createShipMultiFixtureDef(shape, shape2);*/
        createFixtureDef(shape);
        shape.dispose();
        //shape2.dispose();
    }

    private void setShapeAndFixtureAsCircle() {
        //PolygonShape shape = new PolygonShape();
        CircleShape shape = new CircleShape();
        shape.setRadius(Utility.pixelsToMeters(bodyWidth / 2));
        //shape.setAsBox(Utility.pixelsToMeters(bodyWidth / 2), Utility.pixelsToMeters(bodyHeight / 2));
        createFixtureDefAsCircle(shape);
        shape.dispose();
    }

    private void createFixtureDef(PolygonShape shape) {
        FixtureDef polygonFixtureDef = new FixtureDef();
        polygonFixtureDef.shape = shape;
        polygonFixtureDef.density = density;
        polygonFixtureDef.restitution = restitution;
        //boxFixtureDef.friction = friction;
        body.createFixture(polygonFixtureDef);
    }

    private void createShipMultiFixtureDef(PolygonShape shape, PolygonShape shape2) {
        FixtureDef polygonFixtureDef = new FixtureDef();
        polygonFixtureDef.shape = shape;
        polygonFixtureDef.density = density;
        polygonFixtureDef.restitution = restitution;
        //boxFixtureDef.friction = friction;
        FixtureDef polygonFixtureDef2 = new FixtureDef();
        polygonFixtureDef2.shape = shape2;
        polygonFixtureDef2.density = density;
        polygonFixtureDef2.restitution = restitution;
        body.createFixture(polygonFixtureDef);
        body.createFixture(polygonFixtureDef2);
    }

    private void createFixtureDefAsCircle(CircleShape shape) {
        FixtureDef ballFixtureDef = new FixtureDef();
        ballFixtureDef.shape = shape;
        ballFixtureDef.density = density;
        ballFixtureDef.restitution = restitution;
        //ballFixtureDef.friction = friction;
        body.createFixture(ballFixtureDef);
    }

    private void setBodyDefPosition(BodyDef bodyDef) {
        bodyDef.position.set(Utility.pixelsToMeters(x),
                Utility.pixelsToMeters(y));
    }

    private void setEdgeBodyDefPosition() {
        bodyDef.position.set(0,0);
    }

    protected void setUserData(String userData) {
        body.setUserData(userData);
    }

    protected void removeBodySafely() {
        //ArrayList<Element> arrayList = new ArrayList<Element>(Arrays.asList(array));
        //final ArrayList<JointEdge> jointEdgeList = new ArrayList<JointEdge>(Arrays.asList(body.getJointList()));
        //JointEdge[] jointEdges = body.getJointList();
        //while (list.size() > 0) {
        //    world.destroyJoint(list.get(0).joint);
        //}
        // actual removeBody
        GameCore.world.destroyBody(body);
        bodyExists = false;
    }

    // To be used for resizing circular objects with a single fixture
    // Does not change the actual fixture size, just the variables
    // BodyWrapper will need to be recreated for actual change in body
    protected void setTemporaryRadius(float radius) {
        float diameter = radius * 2;
        bodyWidth = diameter;
        bodyHeight = diameter;
        halfBodyWidth = radius;
        halfBodyHeight = radius;
    }
}
