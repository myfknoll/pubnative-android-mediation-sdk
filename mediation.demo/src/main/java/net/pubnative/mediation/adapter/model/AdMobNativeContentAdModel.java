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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.squareup.picasso.Picasso;

import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.List;

public class AdMobNativeContentAdModel extends PubnativeAdModel {

    public static final String TAG = AdMobNativeContentAdModel.class.getSimpleName();

    protected NativeContentAd mNativeAd;

    public AdMobNativeContentAdModel(NativeContentAd nativeAd) {

        if (nativeAd != null) {
            mNativeAd = nativeAd;
        }
    }

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
        if (mNativeAd != null && mNativeAd.getImages() != null) {
            if (mNativeAd.getImages().size() > 0) {
                result = mNativeAd.getImages().get(0).getUri().toString();
            }
        }
        return result;
    }

    @Override
    public String getBannerUrl() {

        Log.v(TAG, "getBannerUrl");

        String result = null;
        if (mNativeAd != null && mNativeAd.getImages() != null) {
            if (mNativeAd.getImages().size() > 0) {
                result = mNativeAd.getImages().get(0).getUri().toString();
            }
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

        return starRating;
    }

    @Override
    public View getAdvertisingDisclosureView(Context context) {

        return null;
    }

    @Override
    public void startTracking(Context context, View adView) {

        Log.v(TAG, "startTracking");

        ((NativeContentAdView)adView).setHeadlineView(adView.findViewById(R.id.contentad_headline));
        ((NativeContentAdView)adView).setImageView(adView.findViewById(R.id.contentad_image));
        ((NativeContentAdView)adView).setBodyView(adView.findViewById(R.id.contentad_body));
        ((NativeContentAdView)adView).setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
        ((NativeContentAdView)adView).setLogoView(adView.findViewById(R.id.contentad_logo));
        ((NativeContentAdView)adView).setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) ((NativeContentAdView)adView).getHeadlineView()).setText(mNativeAd.getHeadline());
        ((TextView) ((NativeContentAdView)adView).getBodyView()).setText(mNativeAd.getBody());
        ((TextView) ((NativeContentAdView)adView).getCallToActionView()).setText(mNativeAd.getCallToAction());
        ((TextView) ((NativeContentAdView)adView).getAdvertiserView()).setText(mNativeAd.getAdvertiser());

        List<NativeAd.Image> images = mNativeAd.getImages();

        if (images.size() > 0) {
            Picasso.with(context).load(images.get(0).getUri()).into((ImageView) ((NativeContentAdView)adView).getImageView());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = mNativeAd.getLogo();

        if (logoImage == null) {
            ((NativeContentAdView)adView).getLogoView().setVisibility(View.INVISIBLE);
        } else {
            Picasso.with(context).load(logoImage.getUri()).into((ImageView) ((NativeContentAdView)adView).getLogoView());
            ((NativeContentAdView)adView).getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        ((NativeContentAdView)adView).setNativeAd(mNativeAd);

    }

    @Override
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
    }
}
