package net.pubnative.mediation.adapter.network;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdBanner;
import com.flurry.android.ads.FlurryAdBannerListener;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class YahooNetworkBannerAdapter extends PubnativeNetworkBannerAdapter implements FlurryAdBannerListener {

    public static final String TAG = YahooNetworkBannerAdapter.class.getSimpleName();
    protected FlurryAdBanner mAdBanner;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public YahooNetworkBannerAdapter(Map data) {

        super(data);
    }

    @Override
    public void load(Context context) {
        Log.v(TAG, "load");
        if (context == null || mData == null) {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String apiKey = (String) mData.get(YahooNetworkRequestAdapter.KEY_FLURRY_API_KEY);
            String adSpaceName = (String) mData.get(YahooNetworkRequestAdapter.KEY_AD_SPACE_NAME);
            if (TextUtils.isEmpty(apiKey) || TextUtils.isEmpty(adSpaceName)) {
                invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                FlurryAgent.setLogEnabled(true);
                FlurryAgent.setLogLevel(Log.VERBOSE);
                // initialize flurry with new apiKey
                FlurryAgent.init(context, apiKey);
                // execute/resume session
                if (!FlurryAgent.isSessionActive()) {
                    FlurryAgent.onStartSession(context);
                }
                ViewGroup rootView = (ViewGroup)((Activity) context).findViewById(android.R.id.content);
                mAdBanner = new FlurryAdBanner(context, rootView, adSpaceName);
                mAdBanner.setListener(this);
                // Add targeting
                FlurryAdTargeting targeting = getTargeting();
                if (targeting != null) {
                    mAdBanner.setTargeting(targeting);
                }
                mAdBanner.fetchAd();
            }
        }
    }

    protected FlurryAdTargeting getTargeting() {

        FlurryAdTargeting result = null;
        if (mTargeting != null) {
            result = new FlurryAdTargeting();
            result.setAge(mTargeting.age);
            if (mTargeting.gender == null) {
                result.setGender(FlurryGender.UNKNOWN);
            } else if (mTargeting.gender.equals("female")) {
                result.setGender(FlurryGender.FEMALE);
            } else if (mTargeting.gender.equals("male")) {
                result.setGender(FlurryGender.MALE);
            } else {
                result.setGender(FlurryGender.UNKNOWN);
            }
            if (mTargeting.interests != null) {
                Map interests = new HashMap();
                interests.put("interest", TextUtils.join(",", mTargeting.interests));
                result.setKeywords(interests);
            }
        }
        return result;
    }

    @Override
    public void show() {
        mAdBanner.displayAd();
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mAdBanner != null) {
            //FlurryAgent.onEndSession(this);
            mAdBanner.destroy();
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mAdBanner != null) {
            result = mAdBanner.isReady();
        }
        return result;
    }

    @Override
    public void onFetched(FlurryAdBanner flurryAdBanner) {
        invokeLoadFinish(this);
    }

    @Override
    public void onRendered(FlurryAdBanner flurryAdBanner) {
        invokeShow();
    }

    @Override
    public void onShowFullscreen(FlurryAdBanner flurryAdBanner) {

    }

    @Override
    public void onCloseFullscreen(FlurryAdBanner flurryAdBanner) {

    }

    @Override
    public void onAppExit(FlurryAdBanner flurryAdBanner) {

    }

    @Override
    public void onClicked(FlurryAdBanner flurryAdBanner) {
        invokeClick();
    }

    @Override
    public void onVideoCompleted(FlurryAdBanner flurryAdBanner) {

    }

    @Override
    public void onError(FlurryAdBanner flurryAdBanner, FlurryAdErrorType flurryAdErrorType, int i) {
        Log.v(TAG, "onError: " + i);
        invokeLoadFail(PubnativeException.ADAPTER_UNKNOWN_ERROR);
    }

}
