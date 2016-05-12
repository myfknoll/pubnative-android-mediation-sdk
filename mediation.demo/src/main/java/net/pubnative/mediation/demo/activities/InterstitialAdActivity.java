package net.pubnative.mediation.demo.activities;

import android.util.Log;
import android.view.View;

import net.pubnative.mediation.demo.Settings;
import net.pubnative.mediation.request.PubnativeNetworkInterstitial;

public class InterstitialAdActivity extends StandardAdUnitActivity implements PubnativeNetworkInterstitial.Listener {

    private static final String TAG = InterstitialAdActivity.class.getSimpleName();

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        mLoaderContainer.setVisibility(View.VISIBLE);
        PubnativeNetworkInterstitial interstitial = new PubnativeNetworkInterstitial();
        interstitial.setListener(this);
        interstitial.load(this, Settings.getAppToken(this), mPlacementSpinner.getSelectedItem().toString());
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkInterstitial.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeNetworkInterstitialLoadFinish(PubnativeNetworkInterstitial interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialLoadFinish");
        mLoaderContainer.setVisibility(View.GONE);
        interstitial.show();
    }

    @Override
    public void onPubnativeNetworkInterstitialLoadFail(PubnativeNetworkInterstitial interstitial, Exception exception) {
        Log.v(TAG, "onPubnativeNetworkInterstitialLoadFail", exception);
        mLoaderContainer.setVisibility(View.GONE);
    }

    @Override
    public void onPubnativeNetworkInterstitialShow(PubnativeNetworkInterstitial interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialShow");
        mLoaderContainer.setVisibility(View.GONE);
    }

    @Override
    public void onPubnativeNetworkInterstitialImpressionConfirmed(PubnativeNetworkInterstitial interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialImpressionConfirmed");
    }

    @Override
    public void onPubnativeNetworkInterstitialClick(PubnativeNetworkInterstitial interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialClick");
    }

    @Override
    public void onPubnativeNetworkInterstitialHide(PubnativeNetworkInterstitial interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialHide");
    }
}
