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

package net.pubnative.mediation.adapter.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.inlocomedia.android.ads.AdView;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public class InLocoMediaNativeAdModel extends PubnativeAdModel {

    public static final String TAG = InLocoMediaNativeAdModel.class.getSimpleName();

    protected ViewGroup mAdView;

    public InLocoMediaNativeAdModel(AdView adView) {

        mAdView = adView;
    }

    //==============================================================================================
    // PubnativeAdModel methods
    //==============================================================================================
    // Fields
    //----------------------------------------------------------------------------------------------

    @Override
    public String getTitle() {

        Log.v(TAG, "getTitle");
        return null;
    }

    @Override
    public String getDescription() {

        Log.v(TAG, "getDescription");
        return null;
    }

    @Override
    public String getIconUrl() {

        Log.v(TAG, "getIconUrl");
        return null;
    }

    @Override
    public String getBannerUrl() {

        Log.v(TAG, "getBannerUrl");
        return null;
    }

    @Override
    public String getCallToAction() {

        Log.v(TAG, "getCallToAction");
        return null;
    }

    @Override
    public float getStarRating() {

        Log.v(TAG, "getStarRating");
        return 0;
    }

    @Override
    public View getAdvertisingDisclosureView(Context context) {

        Log.v(TAG, "getAdvertisingDisclosureView");
        return null;
    }

    @Override
    public void startTracking(Context context, ViewGroup adView) {

    }

    @Override
    public void stopTracking() {

    }

    @Override
    public ViewGroup getAdView() {

        Log.v(TAG, "getAdView");
        return mAdView;
    }

}
