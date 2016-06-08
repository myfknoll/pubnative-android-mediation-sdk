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
import android.view.LayoutInflater;

import com.inlocomedia.android.InLocoMedia;
import com.inlocomedia.android.InLocoMediaOptions;
import com.inlocomedia.android.ads.AdRequest;
import com.inlocomedia.android.ads.AdView;
import com.inlocomedia.android.ads.AdViewListener;

import net.pubnative.mediation.adapter.model.InLocoMediaNativeAdModel;
import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class InLocoMediaNetworkRequestAdapter extends PubnativeNetworkRequestAdapter {

    private   static       String   TAG            = InLocoMediaNetworkRequestAdapter.class.getSimpleName();
    protected static final String   KEY_APP_ID     = "app_id";
    protected static final String   KEY_AD_UNIT_ID = "ad_unit_id";
    protected              AdView   mAdView;

    /**
     * Creates a new instance of InLocoMediaNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public InLocoMediaNetworkRequestAdapter(Map data) {

        super(data);
    }
    //==============================================================================================
    // PubnativeNetworkAdapter methods
    //==============================================================================================

    @Override
    protected void request(Context context) {

        Log.v(TAG, "request");
        if (context != null && mData != null) {
            String appId = (String) mData.get(KEY_APP_ID);
            String adUnitId = (String) mData.get(KEY_AD_UNIT_ID);
            if (TextUtils.isEmpty(appId) || TextUtils.isEmpty(adUnitId)) {
                invokeFailed(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
            } else {
                createRequest(context, appId, adUnitId);
            }
        } else {
            invokeFailed(PubnativeException.ADAPTER_MISSING_DATA);
        }
    }

    //==============================================================================================
    // InLocoMediaNetworkAdapter methods
    //==============================================================================================
    protected void createRequest(Context context, String appId, String adUnitId) {

        Log.v(TAG, "createRequest");
        InLocoMediaOptions options = InLocoMediaOptions.getInstance(context);
        options.setAdsKey(appId);
        InLocoMedia.init(context, options);

        AdRequest adRequest = new AdRequest();
        adRequest.setAdUnitId(adUnitId);
        mAdView = (AdView) ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.inlocomedia_native, null);
        mAdView.setAdListener(new NativeAdListener());
        mAdView.loadAd(adRequest);
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // AdViewListener
    //----------------------------------------------------------------------------------------------
    protected class NativeAdListener extends AdViewListener {

        @Override
        public void onAdViewReady(AdView adView) {

            Log.v(TAG, "onAdViewReady");
            InLocoMediaNativeAdModel wrapper = new InLocoMediaNativeAdModel(adView);
            invokeLoaded(wrapper);
        }

        @Override
        public void onAdError(AdView adView, com.inlocomedia.android.ads.AdError adError) {

            Log.v(TAG, "onAdError");
            Map errorData = new HashMap();
            errorData.put("adError", adError.toString());
            invokeFailed(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, errorData));
        }
    }
}
