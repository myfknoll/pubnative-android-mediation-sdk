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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.squareup.picasso.Picasso;

import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.ArrayList;
import java.util.List;

public class AdMobNativeAppInstallAdModel extends PubnativeAdModel {

    public static final String TAG = AdMobNativeAppInstallAdModel.class.getSimpleName();

    protected NativeAppInstallAd mNativeAd;

    public AdMobNativeAppInstallAdModel(NativeAppInstallAd nativeAd) {

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
        if (mNativeAd != null && mNativeAd.getIcon() != null) {
            result = mNativeAd.getIcon().getUri().toString();
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
    public void startTracking(Context context, View adView) {

        Log.v(TAG, "startTracking");

        ((NativeAppInstallAdView)adView).setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        ((NativeAppInstallAdView)adView).setImageView(adView.findViewById(R.id.appinstall_image));
        ((NativeAppInstallAdView)adView).setBodyView(adView.findViewById(R.id.appinstall_body));
        ((NativeAppInstallAdView)adView).setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        ((NativeAppInstallAdView)adView).setIconView(adView.findViewById(R.id.appinstall_app_icon));
        ((NativeAppInstallAdView)adView).setPriceView(adView.findViewById(R.id.appinstall_price));
        ((NativeAppInstallAdView)adView).setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        ((NativeAppInstallAdView)adView).setStoreView(adView.findViewById(R.id.appinstall_store));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) ((NativeAppInstallAdView)adView).getHeadlineView()).setText(mNativeAd.getHeadline());
        ((TextView) ((NativeAppInstallAdView)adView).getBodyView()).setText(mNativeAd.getBody());
        ((Button) ((NativeAppInstallAdView)adView).getCallToActionView()).setText(mNativeAd.getCallToAction());

        Picasso.with(context).load(mNativeAd.getIcon().getUri()).into((ImageView) ((NativeAppInstallAdView)adView).getIconView());

        List<NativeAd.Image> images = mNativeAd.getImages();

        if (images.size() > 0) {
            Picasso.with(context).load(images.get(0).getUri()).into((ImageView) ((NativeAppInstallAdView)adView).getImageView());
        }

        // Some aren't guaranteed, however, and should be checked.
        if (mNativeAd.getPrice() == null) {
            ((NativeAppInstallAdView)adView).getPriceView().setVisibility(View.INVISIBLE);
        } else {
            ((NativeAppInstallAdView)adView).getPriceView().setVisibility(View.VISIBLE);
            ((TextView) ((NativeAppInstallAdView)adView).getPriceView()).setText(mNativeAd.getPrice());
        }

        if (mNativeAd.getStore() == null) {
            ((NativeAppInstallAdView)adView).getStoreView().setVisibility(View.INVISIBLE);
        } else {
            ((NativeAppInstallAdView)adView).getStoreView().setVisibility(View.VISIBLE);
            ((TextView) ((NativeAppInstallAdView)adView).getStoreView()).setText(mNativeAd.getStore());
        }

        if (mNativeAd.getStarRating() == null) {
            ((NativeAppInstallAdView)adView).getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) ((NativeAppInstallAdView)adView).getStarRatingView())
                    .setRating(mNativeAd.getStarRating().floatValue());
            ((NativeAppInstallAdView)adView).getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        ((NativeAppInstallAdView)adView).setNativeAd(mNativeAd);

    }

    @Override
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
    }

}
