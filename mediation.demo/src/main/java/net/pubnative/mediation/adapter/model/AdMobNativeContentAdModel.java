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

import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.squareup.picasso.Picasso;

import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.request.model.PubnativeAdModel;

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

        LayoutInflater inflater = (LayoutInflater) adView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NativeContentAdView view = (NativeContentAdView) inflater .inflate(R.layout.ad_app_install, null);

        view.setHeadlineView(view.findViewById(R.id.appinstall_headline));
        view.setImageView(view.findViewById(R.id.appinstall_image));
        view.setBodyView(view.findViewById(R.id.appinstall_body));
        view.setCallToActionView(view.findViewById(R.id.appinstall_call_to_action));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) view.getHeadlineView()).setText(getTitle());
        ((TextView) view.getBodyView()).setText(getDescription());
        ((Button) view.getCallToActionView()).setText(getCallToAction());
        Picasso.with(mContext).load(getBannerUrl()).into((ImageView) view.getImageView());

        // Assign native ad object to the native view.

        view.setNativeAd(mNativeAd);
        ((ViewGroup) adView).addView(view);

    }

    @Override
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
    }
}
