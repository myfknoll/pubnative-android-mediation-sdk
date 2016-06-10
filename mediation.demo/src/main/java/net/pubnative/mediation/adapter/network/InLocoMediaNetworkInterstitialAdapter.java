// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.inlocomedia.android.InLocoMedia;
import com.inlocomedia.android.InLocoMediaOptions;
import com.inlocomedia.android.ads.AdError;
import com.inlocomedia.android.ads.AdRequest;
import com.inlocomedia.android.ads.interstitial.InterstitialAd;
import com.inlocomedia.android.profile.UserProfile;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class InLocoMediaNetworkInterstitialAdapter extends PubnativeNetworkInterstitialAdapter {

    private   static final String         TAG            = InLocoMediaNetworkInterstitialAdapter.class.getSimpleName();
    //==============================================================================================
    // Properties
    //==============================================================================================
    protected static final String         KEY_APP_ID     = "app_id";
    protected static final String         KEY_AD_UNIT_ID = "ad_unit_id";
    private                InterstitialAd mInterstitial;

    /**
     * Creates a new instance of InLocoMediaNetworkInterstitialAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public InLocoMediaNetworkInterstitialAdapter(Map data) {

        super(data);
    }

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context == null || mData == null) {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String appId = (String) mData.get(KEY_APP_ID);
            String adUnitId = (String) mData.get(KEY_AD_UNIT_ID);
            if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(adUnitId)) {
                invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                InLocoMediaOptions options = InLocoMediaOptions.getInstance(context);
                options.setAdsKey(appId);
                InLocoMedia.init(context, options);

                mInterstitial = new InterstitialAd(context);
                mInterstitial.setInterstitialAdListener(new InterstitialAdListener());
                mInterstitial.loadAd(getAdRequest(adUnitId));
            }
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mInterstitial != null) {
            result = mInterstitial.isLoaded();
        }
        return result;
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        if (mInterstitial != null) {
            mInterstitial.show();
        }
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    protected AdRequest getAdRequest(String adUnitId) {

        AdRequest adRequest = new AdRequest();
        if (mTargeting != null) {

            UserProfile.Gender gender;
            if (TextUtils.isEmpty(mTargeting.gender)) {
                gender = UserProfile.Gender.UNDEFINED;
            } else if ("male".equals(mTargeting.gender)) {
                gender = UserProfile.Gender.MALE;
            } else if ("female".equals(mTargeting.gender)) {
                gender = UserProfile.Gender.FEMALE;
            } else {
                gender = UserProfile.Gender.UNDEFINED;
            }

            if (mTargeting.age != null && mTargeting.age > 0) {
                int year = Calendar.getInstance().get(Calendar.YEAR) - mTargeting.age;
                adRequest.setUserProfile(new UserProfile(gender, new GregorianCalendar(year, 1, 1).getTime()));
            } else {
                adRequest.setUserProfile(new UserProfile(gender, null));
            }
        }
        adRequest.setAdUnitId(adUnitId);
        return adRequest;
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // InterstitialAdListener
    //----------------------------------------------------------------------------------------------

    public class InterstitialAdListener extends com.inlocomedia.android.ads.interstitial.InterstitialAdListener {

        @Override
        public void onAdReady(final InterstitialAd ad) {

            Log.v(TAG, "onAdReady");
            invokeLoadFinish();
        }

        @Override
        public void onAdError(InterstitialAd ad, AdError error) {

            Log.v(TAG, "onAdError");
            Map errorData = new HashMap();
            errorData.put("adError", error.toString());
            invokeLoadFail(PubnativeException.extraException(PubnativeException.INTERSTITIAL_LOADING, errorData));
        }

        @Override
        public void onAdOpened(InterstitialAd ad) {

            Log.v(TAG, "onAdOpened");
            invokeShow();
        }

        @Override
        public void onAdClosed(InterstitialAd ad) {

            Log.v(TAG, "onAdClosed");
            invokeHide();
        }

        @Override
        public void onAdLeftApplication(InterstitialAd ad) {

            Log.v(TAG, "onAdLeftApplication");
            invokeClick();
        }
    }
}
