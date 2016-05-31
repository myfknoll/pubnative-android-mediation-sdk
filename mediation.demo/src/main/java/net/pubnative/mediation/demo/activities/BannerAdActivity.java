package net.pubnative.mediation.demo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.demo.Settings;
import net.pubnative.mediation.request.PubnativeNetworkBanner;

public class BannerAdActivity extends StandardAdUnitActivity implements PubnativeNetworkBanner.Listener {

    private static final String TAG = "BannerAdActivity";

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        mLoaderContainer.setVisibility(View.VISIBLE);
        PubnativeNetworkBanner banner = new PubnativeNetworkBanner();
        banner.setListener(this);
        banner.load(this, Settings.getAppToken(this), mPlacementSpinner.getSelectedItem().toString());
    }

    @Override
    public void onPubnativeNetworkBannerLoadFinish(PubnativeNetworkBanner banner) {
        Log.v(TAG, "onPubnativeNetworkInterstitialLoadFinish");
        mLoaderContainer.setVisibility(View.GONE);
        banner.show();
        showToast("Banner ad loaded");
    }

    @Override
    public void onPubnativeNetworkBannerLoadFail(PubnativeNetworkBanner banner, Exception exception) {
        Log.v(TAG, "onPubnativeNetworkInterstitialLoadFail", exception);
        mLoaderContainer.setVisibility(View.GONE);
        showToast(exception.getMessage());
        showToast("Banner ad loading failed");
    }

    @Override
    public void onPubnativeNetworkBannerShow(PubnativeNetworkBanner banner) {
        Log.v(TAG, "onPubnativeNetworkInterstitialShow");
        mLoaderContainer.setVisibility(View.GONE);
        showToast("Banner show");
    }

    @Override
    public void onPubnativeNetworkBannerImpressionConfirmed(PubnativeNetworkBanner banner) {
        Log.v(TAG, "onPubnativeNetworkInterstitialImpressionConfirmed");
        showToast("Banner impression confirmed");
    }

    @Override
    public void onPubnativeNetworkBannerClick(PubnativeNetworkBanner banner) {
        Log.v(TAG, "onPubnativeNetworkInterstitialClick");
        showToast("Banner click");
    }

    @Override
    public void onPubnativeNetworkBannerHide(PubnativeNetworkBanner banner) {
        Log.v(TAG, "onPubnativeNetworkInterstitialHide");
        showToast("Banner hide");
    }
}
