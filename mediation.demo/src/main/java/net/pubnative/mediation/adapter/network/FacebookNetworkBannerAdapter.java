package net.pubnative.mediation.adapter.network;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.ImpressionListener;

import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.util.Map;

public class FacebookNetworkBannerAdapter extends PubnativeNetworkBannerAdapter implements AdListener, ImpressionListener {

    public static final String TAG = FacebookNetworkBannerAdapter.class.getSimpleName();
    protected AdView mBannerView;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public FacebookNetworkBannerAdapter(Map data) {

        super(data);
    }

    @Override
    public void load(Context context) {

        Log.d(TAG, "load");
        if (context != null && mData != null) {
            String placementId = (String) mData.get(FacebookNetworkRequestAdapter.KEY_PLACEMENT_ID);
            AdSize bannerSize = PubnativeDeviceUtils.isTablet(context) ? AdSize.BANNER_HEIGHT_90 : AdSize.BANNER_HEIGHT_50;
            if (!TextUtils.isEmpty(placementId)) {
                ViewGroup rootView = (ViewGroup)((Activity) context).findViewById(android.R.id.content);
                RelativeLayout container = new RelativeLayout(context);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                container.setLayoutParams(params);
                mBannerView = new AdView(context, placementId, bannerSize);
                mBannerView.setVisibility(View.INVISIBLE);
                mBannerView.setAdListener(this);
                container.addView(mBannerView);
                rootView.addView(container);
                container.setGravity(Gravity.BOTTOM);
                mBannerView.loadAd();
            } else {
                invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
            }
        } else {
            invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
        }
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        if (mBannerView != null) {
            mBannerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mBannerView != null) {
            mBannerView.destroy();
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        return true;
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // AdListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onError(Ad ad, AdError adError) {

        Log.v(TAG, "onError: " + (adError != null ? (adError.getErrorCode() + " - " + adError.getErrorMessage()) : ""));
        if (adError == null) {
            invokeLoadFail(PubnativeException.ADAPTER_UNKNOWN_ERROR);
        } else {
            switch (adError.getErrorCode()) {
                case AdError.NO_FILL_ERROR_CODE:
                case AdError.LOAD_TOO_FREQUENTLY_ERROR_CODE:
                case FacebookNetworkRequestAdapter.FACEBOOK_ERROR_NO_FILL_1203:
                    invokeLoadFinish(null);
                    break;
                default:
                    invokeLoadFail(new Exception("FacebookNetworkBannerAdapter -code " + adError.getErrorCode() + " -message " + adError.getErrorMessage()));
            }
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {
        Log.v(TAG, "onAdLoaded");
        mBannerView.setImpressionListener(this);
        invokeLoadFinish(this);
    }

    @Override
    public void onAdClicked(Ad ad) {
        Log.v(TAG, "onAdClicked");
        invokeClick();
        destroy();
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        Log.v(TAG, "onLoggingImpression");
        invokeImpressionConfirmed();
    }

}
