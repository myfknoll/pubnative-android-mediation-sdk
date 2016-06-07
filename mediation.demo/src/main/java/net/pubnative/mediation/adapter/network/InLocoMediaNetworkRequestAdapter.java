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
import android.view.View;

import com.inlocomedia.android.InLocoMedia;
import com.inlocomedia.android.InLocoMediaOptions;
import com.inlocomedia.android.ads.AdRequest;
import com.inlocomedia.android.ads.AdView;
import com.inlocomedia.android.ads.AdViewListener;

import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class InLocoMediaNetworkRequestAdapter extends PubnativeNetworkRequestAdapter {

    private static         String   TAG                         = InLocoMediaNetworkRequestAdapter.class.getSimpleName();
    protected static final String   KEY_PLACEMENT_ID            = "placement_id";
    private                AdView   mAdView;

    /**
     * Creates a new instance of FacebookNetworkRequestAdapter
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
            String placementId = (String) mData.get(KEY_PLACEMENT_ID);
            if (TextUtils.isEmpty(placementId)) {
                createRequest(context, placementId);
            } else {
                invokeFailed(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
            }
        } else {
            invokeFailed(PubnativeException.ADAPTER_MISSING_DATA);
        }
    }

    //==============================================================================================
    // FacebookNetworkAdapter methods
    //==============================================================================================
    protected void createRequest(Context context, String placementId) {

        Log.v(TAG, "createRequest");
        InLocoMediaOptions options = InLocoMediaOptions.getInstance(context);
        options.setAdsKey("48278de5099d4832f2f6e048e1b5981675c0c758e17f585b0aec30a11a1126c3");
        //options.setLogEnabled(true);
        options.setDevelopmentDevices("F6855A5FD44796EBD1A76E69318657D");

        InLocoMedia.init(context, options);

        AdRequest adRequest = new AdRequest();
        adRequest.setAdUnitId("322021666fad75102a9d2b75bf25a41a550812c7328115cf8ae4b7ea9a5a6b6e");
        //322021666fad75102a9d2b75bf25a41a550812c7328115cf8ae4b7ea9a5a6b6e
        mAdView = (AdView) ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.inlocomedia_native, null);

        mAdView.setAdListener(new AdViewListener() {
            public void onAdViewReady(AdView adView) {
                /**
                 * The AdView is ready to be shown
                 */
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdError(AdView adView, com.inlocomedia.android.ads.AdError adError) {
                /**
                 * The AdView load has failed. Check the AdErrors to discover the reason
                 */
                // Show the DisplayAdActivity with the AdError
                Log.v(TAG, "testing");
            }
        });

        mAdView.loadAd(adRequest);
    }
}
