package com.everless.gravital;

/**
 * Created by Jordan on 8/5/2016.
 * Interface for running Android in-app com.everless.gravital.billing code
 * http://pipplo.com/libgdx-google-in-app-billing-v3/
 */
public interface IabInterface {
    //product sku provided in the google play console
    //String SKU_PURCHASE_CREATE = "purchase_create";
    String SKU_PURCHASE_CREATE = "1001";
    String SKU_PURCHASE_WARPED_WORMHOLES = "purchase_warped_wormholes";
    String SKU_PURCHASE_THE_TIMING_IS_RIGHT = "purchase_the_timing_is_right";

    // (arbitrary) request code for the purchase flow
    int RC_REQUEST = 10001;
    boolean purchaseCreate();
    boolean purchaseWarpedWormholes();
    boolean purchaseTheTimingIsRight();

    boolean processPurchases();
    boolean creationModePurchased();
    boolean warpedWormholesPurchased();
    boolean theTimingIsRightPurchased();
}
