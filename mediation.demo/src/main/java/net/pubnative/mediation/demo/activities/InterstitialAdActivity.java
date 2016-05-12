package net.pubnative.mediation.demo.activities;

import android.util.Log;
import android.view.View;

public class InterstitialAdActivity extends StandardAdUnitActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        mLoaderContainer.setVisibility(View.VISIBLE);
        PubnativeInterstitial interstitial = new PubnativeInterstitial(this, Settings.getAppToken());
        interstitial.setListener(this);
        interstitial.load();
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeInterstitial.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeInterstitialLoadFinish(PubnativeInterstitial interstitial) {
        Log.v(TAG, "onPubnativeInterstitialLoadFinish");
        interstitial.show();
    }

    @Override
    public void onPubnativeInterstitialLoadFail(PubnativeInterstitial interstitial, Exception exception) {
        Log.v(TAG, "onPubnativeInterstitialLoadFail", exception);
        mLoaderContainer.setVisibility(View.GONE);
    }

    @Override
    public void onPubnativeInterstitialShow(PubnativeInterstitial interstitial) {
        Log.v(TAG, "onPubnativeInterstitialShow");
        mLoaderContainer.setVisibility(View.GONE);
    }

    @Override
    public void onPubnativeInterstitialImpressionConfirmed(PubnativeInterstitial interstitial) {
        Log.v(TAG, "onPubnativeInterstitialImpressionConfirmed");
    }

    @Override
    public void onPubnativeInterstitialClick(PubnativeInterstitial interstitial) {
        Log.v(TAG, "onPubnativeInterstitialClick");
    }

    @Override
    public void onPubnativeInterstitialHide(PubnativeInterstitial interstitial) {
        Log.v(TAG, "onPubnativeInterstitialHide");
    }
}
