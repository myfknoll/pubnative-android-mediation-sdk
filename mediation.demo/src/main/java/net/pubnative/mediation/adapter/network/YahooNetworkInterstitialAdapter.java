package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by davidmartin on 10/05/16.
 */
public class YahooNetworkInterstitialAdapter extends PubnativeNetworkInterstitialAdapter {

    private static String TAG = YahooNetworkInterstitialAdapter.class.getSimpleName();
    private FlurryAdInterstitial mInterstitial;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public YahooNetworkInterstitialAdapter(Map data) {

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
                // initialize flurry with new apiKey
                FlurryAgent.init(context, apiKey);
                // execute/resume session
                if (!FlurryAgent.isSessionActive()) {
                    FlurryAgent.onStartSession(context);
                }
                mInterstitial = new FlurryAdInterstitial(context, adSpaceName);
                // Add targeting
                FlurryAdTargeting targeting = getTargeting();
                if (targeting != null) {
                    mInterstitial.setTargeting(targeting);
                }
                mInterstitial.fetchAd();
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
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mInterstitial != null) {
            result = mInterstitial.isReady();
        }
        return result;
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        if (mInterstitial != null) {
            mInterstitial.displayAd();
        }
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
    }
}
