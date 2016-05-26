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
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdBanner;
import com.flurry.android.ads.FlurryAdBannerListener;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdTargeting;
import com.flurry.android.ads.FlurryGender;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class YahooNetworkFeedBannerAdapter extends PubnativeNetworkFeedBannerAdapter
        implements FlurryAdBannerListener {

    private static final String TAG = YahooNetworkFeedBannerAdapter.class.getSimpleName();

    private FlurryAdBanner mFeedBanner = null;
    private RelativeLayout mAd         = null;
    private boolean        mIsLoaded   = false;

    /**
     * Creates a new instance of YahooNetworkFeedBannerAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public YahooNetworkFeedBannerAdapter(Map data) {

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
                if(mAd == null) {
                    mAd = new RelativeLayout(context);
                    mAd.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                mFeedBanner = new FlurryAdBanner(context, mAd, adSpaceName);
                mFeedBanner.setListener(this);
                // Add targeting
                FlurryAdTargeting targeting = getTargeting();
                if (targeting != null) {
                    mFeedBanner.setTargeting(targeting);
                }
                mFeedBanner.fetchAd();
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
        if (mFeedBanner != null) {
            result = mIsLoaded;
        }
        return result;
    }

    @Override
    public void show(ViewGroup container) {

        Log.v(TAG, "show");
        if (mFeedBanner != null && isReady()) {
            mFeedBanner.displayAd();
        }
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mFeedBanner != null) {
            mFeedBanner.destroy();
        }
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // FlurryAdBannerListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onFetched(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onFetched");
        mIsLoaded = true;
        invokeLoadFinish(this);
    }

    @Override
    public void onRendered(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onRendered");
        invokeShow();
    }

    @Override
    public void onShowFullscreen(FlurryAdBanner flurryAdBanner) {
        Log.v(TAG, "onShowFullscreen");
    }

    @Override
    public void onCloseFullscreen(FlurryAdBanner flurryAdBanner) {
        Log.v(TAG, "onCloseFullscreen");
    }

    @Override
    public void onAppExit(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onAppExit");
    }

    @Override
    public void onClicked(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onClicked");
        invokeClick();
    }

    @Override
    public void onVideoCompleted(FlurryAdBanner flurryAdBanner) {

        Log.v(TAG, "onVideoCompleted");
    }

    @Override
    public void onError(FlurryAdBanner flurryAdBanner, FlurryAdErrorType flurryAdErrorType, int errorCode) {

        Log.v(TAG, "onError: " + errorCode);
        Map errorData = new HashMap();
        errorData.put("errorCode", errorCode);
        errorData.put("flurryAdErrorType", flurryAdErrorType);
        invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, errorData));
    }
}
