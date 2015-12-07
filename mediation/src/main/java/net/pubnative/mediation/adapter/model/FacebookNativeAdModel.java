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

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.ImpressionListener;
import com.facebook.ads.NativeAd;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public class FacebookNativeAdModel extends PubnativeAdModel implements ImpressionListener, AdListener {

    protected NativeAd nativeAd;

    public FacebookNativeAdModel(NativeAd nativeAd) {
        if (nativeAd != null) {
            this.nativeAd = nativeAd;
            this.nativeAd.setAdListener(this);
            this.nativeAd.setImpressionListener(this);
        }
    }

    @Override
    public String getTitle() {
        String result = null;
        if (this.nativeAd != null) {
            result = this.nativeAd.getAdTitle();
        }
        return result;
    }

    @Override
    public String getDescription() {
        String result = null;
        if (this.nativeAd != null) {
            result = this.nativeAd.getAdBody();
        }
        return result;
    }

    @Override
    public String getIconUrl() {
        String iconUrl = null;
        if (this.nativeAd != null && this.nativeAd.getAdIcon() != null) {
            iconUrl = this.nativeAd.getAdIcon().getUrl();
        }
        return iconUrl;
    }

    @Override
    public String getBannerUrl() {
        String bannerUrl = null;
        if (this.nativeAd != null && this.nativeAd.getAdCoverImage() != null) {
            bannerUrl = this.nativeAd.getAdCoverImage().getUrl();
        }
        return bannerUrl;
    }

    @Override
    public String getCallToAction() {
        String result = null;
        if (this.nativeAd != null) {
            result = this.nativeAd.getAdCallToAction();
        }
        return result;
    }

    @Override
    public float getStarRating() {
        float starRating = 0;
        if (this.nativeAd != null) {
            NativeAd.Rating rating = this.nativeAd.getAdStarRating();
            if (rating != null) {
                double ratingScale = rating.getScale();
                double ratingValue = rating.getValue();
                starRating = (float) ((ratingValue / ratingScale) * 5);
            }
        }
        return starRating;
    }

    @Override
    public View getAdvertisingDisclosureView(Context context) {
        View result = null;
        if (context != null && this.nativeAd != null) {
            this.context = context;
            return new AdChoicesView(this.context, this.nativeAd);
        }
        return result;
    }

    @Override
    public void startTracking(Context context, View adView) {
        if (context != null && this.nativeAd != null && adView != null) {
            this.context = context;
            this.nativeAd.registerViewForInteraction(adView);
        }
    }

    @Override
    public void stopTracking(Context context, View adView) {
        if (this.nativeAd != null) {
            this.nativeAd.unregisterView();
        }
    }

    // Facebook

    @Override
    public void onLoggingImpression(Ad ad) {
        this.invokeOnAdImpressionConfirmed();
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        // Do nothing
    }

    @Override
    public void onAdLoaded(Ad ad) {
        // Do nothing
    }

    @Override
    public void onAdClicked(Ad ad) {
        this.invokeOnAdClick();
    }
}
