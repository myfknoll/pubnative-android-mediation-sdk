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

package net.pubnative.mediation.adapter.model;

import android.content.Context;
import android.util.Log;
import android.view.View;

import net.pubnative.library.model.NativeAdModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;

public class PubnativeLibraryAdModel extends PubnativeAdModel implements View.OnClickListener, NativeAdModel.Listener {

    private static String TAG = PubnativeLibraryAdModel.class.getSimpleName();

    protected NativeAdModel mNativeAdModel = null;

    public PubnativeLibraryAdModel(NativeAdModel model) {
        mNativeAdModel = model;
    }

    @Override
    public String getTitle() {

        Log.v(TAG, "getTitle()");

        String result = null;

        if (mNativeAdModel != null) {
            result = mNativeAdModel.title;
        }
        return result;
    }

    @Override
    public String getDescription() {

        Log.v(TAG, "getDescription()");

        String result = null;

        if (mNativeAdModel != null) {
            result = mNativeAdModel.description;
        }
        return result;
    }

    @Override
    public String getIconUrl() {

        Log.v(TAG, "getIconUrl()");

        String result = null;

        if (mNativeAdModel != null) {
            result = mNativeAdModel.iconUrl;
        }
        return result;
    }

    @Override
    public String getBannerUrl() {

        Log.v(TAG, "getBannerUrl()");

        String result = null;

        if (mNativeAdModel != null) {
            result = mNativeAdModel.bannerUrl;
        }
        return result;
    }

    @Override
    public String getCallToAction() {

        Log.v(TAG, "getCallToAction()");

        String result = null;

        if (mNativeAdModel != null) {
            result = mNativeAdModel.ctaText;
        }
        return result;
    }

    @Override
    public float getStarRating() {

        Log.v(TAG, "getStarRating()");

        return mNativeAdModel.getStoreRating();
    }

    @Override
    public View getAdvertisingDisclosureView(Context context) {
        return null;
    }

    @Override
    public void startTracking(Context context, View adView) {

        Log.v(TAG, "startTracking(Context context, View adView)");

        if (mNativeAdModel != null && context != null && adView != null) {
            mContext = context;
            adView.setOnClickListener(this);
            mNativeAdModel.confirmImpressionAutomatically(context, adView, this);
        }
    }

    @Override
    public void stopTracking(Context context, View adView) {

        Log.v(TAG, "stopTracking(Context context, View adView)");

        // Do nothing
    }

    @Override
    public void onClick(View view) {

        Log.v(TAG, "onClick(View view)");

        invokeOnAdClick();
        mNativeAdModel.open(mContext);
    }

    // Pubnative NativeAdModel.Listener
    @Override
    public void onAdImpression(NativeAdModel model) {
        invokeOnAdImpressionConfirmed();
    }
}
