package net.pubnative.mediation.adapter.network;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Map;

public class AdMobNetworkBannerAdapter extends PubnativeNetworkBannerAdapter {

    private static final String TAG = AdMobNetworkBannerAdapter.class.getSimpleName();
    protected AdView mAdView;

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
                ViewGroup rootView = (ViewGroup)((Activity) context).findViewById(android.R.id.content);
                RelativeLayout container = new RelativeLayout(context);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                container.setLayoutParams(params);
                mAdView = new AdView(context);
                mAdView.setAdSize(AdSize.SMART_BANNER);
                mAdView.setAdUnitId(unitId);
                //mAdView.setVisibility(View.INVISIBLE);
                mAdView.setAdListener(listener);
                container.addView(mAdView);
                rootView.addView(container);
                container.setGravity(Gravity.BOTTOM);
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
            //mAdView.setVisibility(View.VISIBLE);
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
        return true;
    }

    private com.google.android.gms.ads.AdListener listener = new com.google.android.gms.ads.AdListener() {

        @Override
        public void onAdClosed() {

            super.onAdClosed();
        }

        @Override
        public void onAdFailedToLoad(int i) {

            super.onAdFailedToLoad(i);
        }

        @Override
        public void onAdLoaded() {

            super.onAdLoaded();
        }

        @Override
        public void onAdOpened() {

            super.onAdOpened();
        }
    };
}
