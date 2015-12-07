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
import android.view.View;

import net.pubnative.library.model.NativeAdModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;

public class PubnativeLibraryAdModel extends PubnativeAdModel implements View.OnClickListener, NativeAdModel.Listener {

    protected NativeAdModel model = null;

    public PubnativeLibraryAdModel(NativeAdModel model) {
        this.model = model;
    }

    @Override
    public String getTitle() {
        String result = null;
        if (model != null) {
            result = model.title;
        }
        return result;
    }

    @Override
    public String getDescription() {
        String result = null;
        if (model != null) {
            result = model.description;
        }
        return result;
    }

    @Override
    public String getIconUrl() {
        String result = null;
        if (model != null) {
            result = model.iconUrl;
        }
        return result;
    }

    @Override
    public String getBannerUrl() {
        String result = null;
        if (model != null) {
            result = model.bannerUrl;
        }
        return result;
    }

    @Override
    public String getCallToAction() {
        String result = null;
        if (model != null) {
            result = model.ctaText;
        }
        return result;
    }

    @Override
    public float getStarRating() {
        return model.getStoreRating();
    }

    @Override
    public View getAdvertisingDisclosureView(Context context) {
        return null;
    }

    @Override
    public void startTracking(Context context, View adView) {
        if (this.model != null && context != null && adView != null) {
            this.context = context;
            adView.setOnClickListener(this);
            this.model.confirmImpressionAutomatically(context, adView, this);
        }
    }

    @Override
    public void stopTracking(Context context, View adView) {
        // Do nothing
    }

    @Override
    public void onClick(View view) {
        this.invokeOnAdClick();
        this.model.open(this.context);
    }

    // Pubnative NativeAdModel.Listener
    @Override
    public void onAdImpression(NativeAdModel model) {
        this.invokeOnAdImpressionConfirmed();
    }
}
