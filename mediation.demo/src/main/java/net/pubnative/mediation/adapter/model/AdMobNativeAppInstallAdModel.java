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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.squareup.picasso.Picasso;

import net.pubnative.mediation.adapter.widget.PubnativeNativeAdView;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.List;

public class AdMobNativeAppInstallAdModel extends PubnativeAdModel {

    public static final String TAG = AdMobNativeAppInstallAdModel.class.getSimpleName();

    protected NativeAppInstallAd mNativeAd;

    public AdMobNativeAppInstallAdModel(NativeAppInstallAd nativeAd) {

            mNativeAd = nativeAd;
    }

    //==============================================================================================
    // PubnativeAdModel methods
    //==============================================================================================
    // Fields
    //----------------------------------------------------------------------------------------------

    @Override
    public String getTitle() {

        Log.v(TAG, "getTitle");

        String result = null;
        if (mNativeAd != null) {
            result = String.valueOf(mNativeAd.getHeadline());
        }

        return result;
    }

    @Override
    public String getDescription() {

        Log.v(TAG, "getDescription");

        String result = null;
        if (mNativeAd != null) {
            result = String.valueOf(mNativeAd.getBody());
        }

        return result;
    }

    @Override
    public String getIconUrl() {

        Log.v(TAG, "getIconUrl");

        String result = null;
        if (mNativeAd != null && mNativeAd.getIcon() != null) {
            result = mNativeAd.getIcon().getUri().toString();
        }

        return result;
    }

    @Override
    public String getBannerUrl() {

        Log.v(TAG, "getBannerUrl");

        String result = null;
        if (mNativeAd != null && mNativeAd.getImages() != null && mNativeAd.getImages().size() > 0) {
            result = mNativeAd.getImages().get(0).getUri().toString();
        }

        return result;
    }

    @Override
    public String getCallToAction() {

        Log.v(TAG, "getCallToAction");

        String result = null;
        if (mNativeAd != null) {
            result = mNativeAd.getCallToAction().toString();
        }

        return result;
    }

    @Override
    public float getStarRating() {

        Log.v(TAG, "getStarRating");

        float starRating = 0;
        if (mNativeAd != null) {
            starRating = mNativeAd.getStarRating().floatValue();
        }

        return starRating;
    }

    @Override
    public View getAdvertisingDisclosureView(Context context) {

        return null;
    }

    @Override
    public Object getNativeAd() {

        return mNativeAd;
    }

    // Tracking
    //----------------------------------------------------------------------------------------------

    @Override
    public void startTracking(Context context, View adView) {

        Log.v(TAG, "startTracking");
    }

    @Override
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
    }

}
