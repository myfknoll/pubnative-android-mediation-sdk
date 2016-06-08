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

import com.inlocomedia.android.InLocoMedia;
import com.inlocomedia.android.InLocoMediaOptions;
import com.inlocomedia.android.ads.AdError;
import com.inlocomedia.android.ads.AdRequest;
import com.inlocomedia.android.ads.AdType;
import com.inlocomedia.android.ads.AdView;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class InLocoMediaNetworkFeedBannerAdapter extends PubnativeNetworkFeedBannerAdapter {

    private static final String  TAG = InLocoMediaNetworkFeedBannerAdapter.class.getSimpleName();
    //==============================================================================================
    // Properties
    //==============================================================================================
    protected static final String  KEY_APP_ID     = "app_id";
    protected static final String  KEY_AD_UNIT_ID = "ad_unit_id";
    private                AdView  mFeedBanner;

    /**
     * Creates a new instance of InLocoMediaNetworkFeedBannerAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public InLocoMediaNetworkFeedBannerAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context != null && mData != null) {
            String appId = (String) mData.get(KEY_APP_ID);
            String adUnitId = (String) mData.get(KEY_AD_UNIT_ID);
            if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(adUnitId)) {
                invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                InLocoMediaOptions options = InLocoMediaOptions.getInstance(context);
                options.setAdsKey(appId);
                InLocoMedia.init(context, options);

                mFeedBanner = new AdView(context);
                mFeedBanner.setType(AdType.DISPLAY_BANNER_MEDIUM_RECTANGLE);
                mFeedBanner.setAdListener(new AdViewListener());
                AdRequest adRequest = new AdRequest();
                adRequest.setAdUnitId(adUnitId);
                mFeedBanner.loadAd(adRequest);
            }
        } else {
            invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mFeedBanner != null) {
            result = mFeedBanner.isLoaded();
        }
        return result;
    }

    @Override
    public void show(ViewGroup container) {

        Log.v(TAG, "show");
        container.addView(mFeedBanner, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        invokeShow();
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        if (mFeedBanner != null) {
            mFeedBanner.destroy();
        }
    }

    @Override
    public void hide() {
        if (mFeedBanner.getParent() != null) {
            ((ViewGroup) mFeedBanner.getParent()).removeView(mFeedBanner);
        }
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // AdViewListener
    //----------------------------------------------------------------------------------------------
    public class AdViewListener extends com.inlocomedia.android.ads.AdViewListener {
        public void onAdViewReady(AdView adView) {

            Log.v(TAG, "onAdViewReady");
            invokeLoadFinish(InLocoMediaNetworkFeedBannerAdapter.this);
        }

        @Override
        public void onAdError(AdView adView, AdError adError) {

            Log.v(TAG, "onAdError");
            Map errorData = new HashMap();
            errorData.put("adError", adError);
            invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, errorData));
        }

        public void onAdLeftApplication(AdView adView) {

            Log.v(TAG, "onAdLeftApplication");
            invokeClick();
        }
    }
}
