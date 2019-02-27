package com.everless.gravital;

/**
 * Created by Jordan on 8/12/2015.
 * http://blog.gemserk.com/2013/02/13/our-solution-to-handle-multiple-screen-sizes-in-android-part-two/
 */
public class VirtualViewport {

    float virtualWidth;
    float virtualHeight;

    public float getVirtualWidth() {
        return virtualWidth;
    }

    public float getVirtualHeight() {
        return virtualHeight;
    }

    public VirtualViewport(float virtualWidth, float virtualHeight) {
        this(virtualWidth, virtualHeight, false);
    }

    public VirtualViewport(float virtualWidth, float virtualHeight, boolean shrink) {
        this.virtualWidth = virtualWidth;
        this.virtualHeight = virtualHeight;
    }

    public float getWidth() {
        //return getWidth(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return getWidth(1080, 1920);
    }

    public float getHeight() {
        //return getHeight(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return getHeight(1080, 1920);
    }

    /**
     * Returns the view port width to let all the virtual view port to be shown on the screen.
     *
     * @param screenWidth
     *            The screen width.
     * @param screenHeight
     *            The screen Height.
     */
    public float getWidth(float screenWidth, float screenHeight) {
        float virtualAspect = virtualWidth / virtualHeight;
        float aspect = screenWidth / screenHeight;
        if (aspect > virtualAspect || (Math.abs(aspect - virtualAspect) < 0.01f)) {
            return virtualHeight * aspect;
        } else {
            return virtualWidth;
        }
    }

    /**
     * Returns the view port height to let all the virtual view port to be shown on the screen.
     *
     * @param screenWidth
     *            The screen width.
     * @param screenHeight
     *            The screen Height.
     */
    public float getHeight(float screenWidth, float screenHeight) {
        float virtualAspect = virtualWidth / virtualHeight;
        float aspect = screenWidth / screenHeight;
        if (aspect > virtualAspect || (Math.abs(aspect - virtualAspect) < 0.01f)) {
            return virtualHeight;
        } else {
            return virtualWidth / aspect;
        }
    }

}