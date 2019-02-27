package com.everless.gravital.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.everless.gravital.AdInterface;
import com.everless.gravital.IabInterface;
import com.everless.gravital.LibGDXLauncher;
import com.everless.gravital.Main;

public class DesktopLauncher implements IabInterface, AdInterface {
	public void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new LibGDXLauncher(this, this), config);
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
