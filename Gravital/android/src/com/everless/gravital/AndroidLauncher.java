package com.everless.gravital;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.everless.gravital.util.IabHelper;
import com.everless.gravital.util.IabResult;
import com.everless.gravital.util.Inventory;
import com.everless.gravital.util.Purchase;

import java.util.Calendar;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import io.fabric.sdk.android.Fabric;

public class AndroidLauncher extends AndroidApplication implements IabInterface, AdInterface {
	private static final String TAG = "AndroidLauncher";
	private static final String AD_UNIT_ID_IMAGE_INTERSTITIAL = "ca-app-pub-3533499829185744/6742010515";
	private static final String AD_UNIT_ID_VIDEO_INTERSTITIAL = "ca-app-pub-3533499829185744/7432694511";
	protected InterstitialAd imageInterstitialAd, videoInterstitialAd;
	IabHelper mHelper;
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
	boolean creationModePurchased, warpedWormholesPurchased, theTimingIsRightPurchased;
	boolean IABHelperSetUp = false;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		inAppBilling();
		setNotifications();
		setAdViews();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new LibGDXLauncher(this, this), config);
	}

	public void setNotifications() {
		Intent alarmIntent = new Intent(AndroidLauncher.this, AlarmReceiver.class);
		PendingIntent pendingIntent =
				PendingIntent.getBroadcast(AndroidLauncher.this, 0, alarmIntent, 0);

		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int repeatingPeriod = 1000 * 60 * 60 * 24 * 24; //24 days
		int startingPeriod = 1000 * 60 * 60 * 24; //1 day

		manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + startingPeriod,
				repeatingPeriod, pendingIntent);
	}

	public void inAppBilling() {
		String base64EncodedPublicKey = "notTheActualKey";
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					//Log.d("IAB", "Problem setting up In-app Billing: " + result);
				} else {
					//Log.d("IAB", "Billing Success: " + result);
					IABHelperSetUp = true;
				}
			}
		});

		setUpPurchaseFinishedListener();
		setUpQueryInventoryFinishedListener();
	}

	public void setAdViews() {
		imageInterstitialAd = new InterstitialAd(this);
		videoInterstitialAd = new InterstitialAd(this);

		imageInterstitialAd.setAdUnitId(AD_UNIT_ID_IMAGE_INTERSTITIAL);
		videoInterstitialAd.setAdUnitId(AD_UNIT_ID_VIDEO_INTERSTITIAL);

		imageInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				Log.i(TAG, "image ad loaded");
			}

			@Override
			public void onAdClosed() {
				Log.i(TAG, "image ad closed");
			}
		});

		videoInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				Log.i(TAG, "video ad loaded");
			}

			@Override
			public void onAdClosed() {
				Log.i(TAG, "video ad closed");
			}
		});
	}

	@Override
	public void loadImageInterstitial() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (!imageInterstitialAd.isLoaded()) {
						AdRequest interstitialRequest = new AdRequest.Builder().build();
						imageInterstitialAd.loadAd(interstitialRequest);
						Log.i(TAG, "loading imageInterstitial");
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showImageInterstitial() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (imageInterstitialAd.isLoaded()) {
						imageInterstitialAd.show();
						Log.i(TAG, "showing imageInterstitial");
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadVideoInterstitial() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (!videoInterstitialAd.isLoaded()) {
						AdRequest interstitialRequest = new AdRequest.Builder().build();
						videoInterstitialAd.loadAd(interstitialRequest);
						Log.i(TAG, "loading videoInterstitialAd");
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void showVideoInterstitial() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (videoInterstitialAd.isLoaded()) {
						videoInterstitialAd.show();
						Log.i(TAG, "showing videoInterstitialAd");
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadOrShowVideoInterstitial() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (videoInterstitialAd.isLoaded()) {
						videoInterstitialAd.show();
						Log.i(TAG, "showing videoInterstitialAd");
					}
					else {
						AdRequest interstitialRequest = new AdRequest.Builder().build();
						videoInterstitialAd.loadAd(interstitialRequest);
						Log.i(TAG, "loading videoInterstitialAd");
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUpPurchaseFinishedListener() {
		// Callback for when a purchase is finished
		mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
			public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
				if ( purchase == null) return;
				//Log.d("IAB", "Purchase finished: " + result + ", purchase: " + purchase);

				// if we were disposed of in the meantime, quit.
				if (mHelper == null) return;

				if (result.isFailure()) {
					//complain("Error purchasing: " + result);
					return;
				}

				//Log.d("IAB", "Purchase successful.");

				if (purchase.getSku().equals(SKU_PURCHASE_CREATE)) {
					//Log.d("IAB", "Purchase is for creation mode. Congratulating user.");
				}
			}
		};
	}

	public void setUpQueryInventoryFinishedListener() {
		// Listener that's called when we finish querying the items and subscriptions we own
		mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
			public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
				//Log.d("IAB", "Query inventory finished.");

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null) return;

				if (result.isFailure()) {
					// handle failure
					return;
				}

				Purchase createPurchase = inventory.getPurchase(SKU_PURCHASE_CREATE);
				Purchase warpedWormholesPurchase = inventory.getPurchase(SKU_PURCHASE_WARPED_WORMHOLES);
				Purchase theTimingIsRightPurchase = inventory.getPurchase(SKU_PURCHASE_THE_TIMING_IS_RIGHT);
				creationModePurchased = (createPurchase != null);
				warpedWormholesPurchased = (warpedWormholesPurchase != null);
				theTimingIsRightPurchased = (theTimingIsRightPurchase != null);
			}
		};
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null) try {
			mHelper.dispose();
		} catch (IabHelper.IabAsyncInProgressException e) {
			e.printStackTrace();
		}
		mHelper = null;
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);

		if (mHelper != null) {
			// Pass on the activity result to the helper for handling
			if (mHelper.handleActivityResult(request, response, data)) {
				//Log.d("IAB", "onActivityResult handled by IABUtil.");
			}
		}
	}

	@Override
	//Return true if successfully connected to Google Play Services
	public boolean purchaseCreate() {
		if (IABHelperSetUp) {
			try {
				mHelper.launchPurchaseFlow(this, SKU_PURCHASE_CREATE, RC_REQUEST,
						mPurchaseFinishedListener, "HANDLE_PAYLOADS");
			} catch (IabHelper.IabAsyncInProgressException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	//Return true if successfully connected to Google Play Services
	public boolean purchaseWarpedWormholes() {
		if (IABHelperSetUp) {
			try {
				mHelper.launchPurchaseFlow(this, SKU_PURCHASE_WARPED_WORMHOLES, RC_REQUEST,
						mPurchaseFinishedListener, "HANDLE_PAYLOADS");
			} catch (IabHelper.IabAsyncInProgressException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	//Return true if successfully connected to Google Play Services
	public boolean purchaseTheTimingIsRight() {
		if (IABHelperSetUp) {
			try {
				mHelper.launchPurchaseFlow(this, SKU_PURCHASE_THE_TIMING_IS_RIGHT, RC_REQUEST,
						mPurchaseFinishedListener, "HANDLE_PAYLOADS");
			} catch (IabHelper.IabAsyncInProgressException e) {
				e.printStackTrace();
			}
				return true;
		}
		return false;
	}

	@Override
	//Return true if successfully connected to Google Play Services
	public boolean processPurchases() {
		final boolean[] result = {false};
		//Not sure why this needs to be run on ui thread but it'll occasionally cause problems otherwise
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (IABHelperSetUp) {
					try {
						mHelper.queryInventoryAsync(mGotInventoryListener);
					} catch (IabHelper.IabAsyncInProgressException e) {
						e.printStackTrace();
					}
					result[0] = true;
				}
			}
		});
		return result[0];
	}

	@Override
	public boolean creationModePurchased() {
		return creationModePurchased;
	}

	@Override
	public boolean warpedWormholesPurchased() {
		return warpedWormholesPurchased;
	}

	@Override
	public boolean theTimingIsRightPurchased() {
		return theTimingIsRightPurchased;
	}
}
