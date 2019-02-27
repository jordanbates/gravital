package com.everless.gravital;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

public class IOSLauncher extends IOSApplication.Delegate implements IabInterface, AdInterface {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        return new IOSApplication(new LibGDXLauncher(this, this), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    @Override
    public void showOrLoadImageInterstitial() {

    }

    @Override
    public boolean purchaseCreate() {
        return false;
    }

    @Override
    public boolean purchaseWarpedWormholes() {
        return false;
    }

    @Override
    public boolean purchaseTheTimingIsRight() {
        return false;
    }

    @Override
    public boolean processPurchases() {
        return false;
    }

    @Override
    public boolean creationModePurchased() {
        return false;
    }

    @Override
    public boolean warpedWormholesPurchased() {
        return false;
    }

    @Override
    public boolean theTimingIsRightPurchased() {
        return false;
    }
}