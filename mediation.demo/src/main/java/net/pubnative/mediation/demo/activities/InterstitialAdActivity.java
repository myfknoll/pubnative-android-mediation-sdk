package net.pubnative.mediation.demo.activities;

import android.util.Log;
import android.view.View;

import net.pubnative.mediation.demo.Settings;
import net.pubnative.mediation.request.PubnativeNetworkVideo;
import net.pubnative.mediation.request.PubnativeNetworkVideo;

public class InterstitialAdActivity extends StandardAdUnitActivity implements PubnativeNetworkVideo.Listener {

    private static final String TAG = InterstitialAdActivity.class.getSimpleName();

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        mLoaderContainer.setVisibility(View.VISIBLE);
        PubnativeNetworkVideo interstitial = new PubnativeNetworkVideo();
        interstitial.setListener(this);
        interstitial.load(this, Settings.getAppToken(this), mPlacementSpinner.getSelectedItem().toString());
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkVideo.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeNetworkInterstitialLoadFinish(PubnativeNetworkVideo interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialLoadFinish");
        mLoaderContainer.setVisibility(View.GONE);
        interstitial.show();
        showToast("Interstitial ad loaded");
    }

    @Override
    public void onPubnativeNetworkInterstitialLoadFail(PubnativeNetworkVideo interstitial, Exception exception) {
        Log.v(TAG, "onPubnativeNetworkInterstitialLoadFail", exception);
        mLoaderContainer.setVisibility(View.GONE);
        showToast(exception.getMessage());
        showToast("Interstitial ad loading failed");
    }

    @Override
    public void onPubnativeNetworkInterstitialShow(PubnativeNetworkVideo interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialShow");
        mLoaderContainer.setVisibility(View.GONE);
        showToast("Interstitial show");
    }

    @Override
    public void onPubnativeNetworkInterstitialImpressionConfirmed(PubnativeNetworkVideo interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialImpressionConfirmed");
        showToast("Interstitial impression confirmed");
    }

    @Override
    public void onPubnativeNetworkInterstitialClick(PubnativeNetworkVideo interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialClick");
        showToast("Interstitial click");
    }

    @Override
    public void onPubnativeNetworkInterstitialHide(PubnativeNetworkVideo interstitial) {
        Log.v(TAG, "onPubnativeNetworkInterstitialHide");
        showToast("Interstitial hide");
    }
}
