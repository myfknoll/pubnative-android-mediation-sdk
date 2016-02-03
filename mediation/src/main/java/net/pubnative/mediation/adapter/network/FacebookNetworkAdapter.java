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

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.model.FacebookNativeAdModel;

import java.util.Map;

public class FacebookNetworkAdapter extends PubnativeNetworkAdapter implements AdListener {

    protected static final String KEY_PLACEMENT_ID = "placement_id";

    protected NativeAd nativeAd;

    public FacebookNetworkAdapter(Map data) {

        super(data);
    }

    @Override
    public void request(Context context) {

        if (context != null && data != null) {
            String placementId = (String) data.get(KEY_PLACEMENT_ID);
            if (!TextUtils.isEmpty(placementId)) {
                this.createRequest(context, placementId);
            } else {
                invokeFailed(new IllegalArgumentException("Invalid placement_id provided."));
            }
        } else {
            invokeFailed(new IllegalArgumentException("No context or adapter data provided."));
        }
    }

    protected void createRequest(Context context, String placementId) {

        this.nativeAd = new NativeAd(context, placementId);
        this.nativeAd.setAdListener(this);
        this.nativeAd.loadAd();
    }

    @Override
    public void onError(Ad ad, AdError adError) {

        if (ad == this.nativeAd) {
            if (adError != null) {
                if (adError == AdError.NO_FILL) {
                    this.invokeLoaded(null);
                } else {
                    this.invokeFailed(new Exception("Facebook adapter error: " + adError.getErrorCode() + " - " + adError.getErrorMessage()));
                }
            } else {
                this.invokeFailed(new Exception("Facebook adapter error: Unknown"));
            }
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {

        if (ad == this.nativeAd) {
            FacebookNativeAdModel wrapModel = new FacebookNativeAdModel((NativeAd) ad);
            this.invokeLoaded(wrapModel);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
        // Do nothing
    }
}
