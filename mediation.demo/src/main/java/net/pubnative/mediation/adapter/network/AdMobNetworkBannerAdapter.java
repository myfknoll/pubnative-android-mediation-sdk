package net.pubnative.mediation.adapter.network;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class AdMobNetworkBannerAdapter extends PubnativeNetworkBannerAdapter {

    private static final String TAG = AdMobNetworkBannerAdapter.class.getSimpleName();
    protected AdView mAdView;
    protected Context mContext;
    protected boolean mIsLoaded = false;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public AdMobNetworkBannerAdapter(Map data) {

        super(data);
    }

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context != null && mData != null) {
            String unitId = (String) mData.get(AdMobNetworkRequestAdapter.ADMOB_UNIT_ID);
            if (!TextUtils.isEmpty(unitId)) {
                mContext = context;
                mAdView = new AdView(mContext);
                mAdView.setAdSize(AdSize.SMART_BANNER);
                mAdView.setAdUnitId(unitId);
                mAdView.setAdListener(listener);

                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("16F5F25826CB21FCB488335014973DA7")
                        .build();

                mAdView.loadAd(adRequest);
            }
        }
    }

    @Override
    public void show() {
        Log.v(TAG, "show");
        if (mAdView != null) {
            ViewGroup rootView = (ViewGroup)((Activity) mContext).findViewById(android.R.id.content);
            RelativeLayout container = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.setLayoutParams(params);
            container.setGravity(Gravity.BOTTOM);
            rootView.addView(container);
            container.addView(mAdView);
            invokeShow();
        }
    }

    @Override
    public void destroy() {
        Log.v(TAG, "destroy");
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    @Override
    public boolean isReady() {
        Log.v(TAG, "isReady");
        boolean result = false;
        if (mAdView != null) {
            result = mIsLoaded;
        }
        return result;
    }

    @Override
    public void hide() {
        Log.v(TAG, "hide");
        if (mAdView.getParent() != null) {
            ((ViewGroup) mAdView.getParent()).removeAllViews();
        }
    }

    private com.google.android.gms.ads.AdListener listener = new com.google.android.gms.ads.AdListener() {

        @Override
        public void onAdClosed() {

            Log.v(TAG, "onAdClosed");
            destroy();
            super.onAdClosed();

        }

        @Override
        public void onAdFailedToLoad(int i) {

            Log.v(TAG, "onError: " + i);
            super.onAdFailedToLoad(i);
            invokeLoadFail(PubnativeException.ADAPTER_UNKNOWN_ERROR);
        }

        @Override
        public void onAdLoaded() {

            Log.v(TAG, "onAdLoaded");
            super.onAdLoaded();
            mIsLoaded = true;
            invokeLoadFinish(AdMobNetworkBannerAdapter.this);

        }

        @Override
        public void onAdOpened() {

            Log.v(TAG, "onAdOpened");
            super.onAdOpened();
            invokeClick();
        }

    };
}
