// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
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

package net.pubnative.library.models;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import net.pubnative.URLDriller;
import net.pubnative.library.tracking.PubnativeImpressionTracker;

import java.util.List;


public class PubnativeAdModel implements PubnativeImpressionTracker.Listener,
                                         URLDriller.Listener {

    private static String                TAG                 = PubnativeAdModel.class.getSimpleName();
    protected      String                title               = null;
    protected      String                description         = null;
    protected      String                cta_text            = null;
    protected      String                icon_url            = null;
    protected      String                banner_url          = null;
    protected      String                click_url           = null;
    protected      String                revenue_model       = null;
    protected      String                type                = null;
    protected      String                portrait_banner_url = null;
    protected      List<PubnativeBeacon> beacons             = null;

    //==============================================================================================
    // Listener
    //==============================================================================================

    /**
     * Interface definition for callbacks to be invoked when impression confirmed/failed, ad clicked/clickfailed
     */
    public interface Listener {

        /**
         * Called when impression is confirmed
         *
         * @param pubnativeAdModel PubnativeAdModel impression that was confirmed
         * @param view             The view where impression confirmed
         */
        void onPubnativeAdModelImpression(PubnativeAdModel pubnativeAdModel, View view);

        /**
         * Called when click is confirmed
         *
         * @param pubnativeAdModel PubnativeAdModel that detected the click
         * @param view             The view that was clicked
         */
        void onPubnativeAdModelClick(PubnativeAdModel pubnativeAdModel, View view);

        /**
         * Called before the model opens the offer
         *
         * @param pubnativeAdModel PubnativeAdModel which's offer will be opened
         */
        void onPubnativeAdModelOpenOffer(PubnativeAdModel pubnativeAdModel);
    }

    protected transient Listener mListener;
    //==============================================================================================
    // Fields
    //==============================================================================================

    public String getTitle() {

        Log.v(TAG, "getTitle");
        return title;
    }

    public String getDescription() {

        Log.v(TAG, "getDescription");
        return description;
    }

    public String getCtaText() {

        Log.v(TAG, "getCtaText");
        return cta_text;
    }

    public String getIconUrl() {

        Log.v(TAG, "getIconUrl");
        return icon_url;
    }

    public String getBannerUrl() {

        Log.v(TAG, "getBannerUrl");
        return banner_url;
    }

    public String getClickUrl() {

        Log.v(TAG, "getClickUrl");
        return click_url;
    }

    public String getRevenueModel() {

        Log.v(TAG, "getRevenueModel");
        return revenue_model;
    }

    public List<PubnativeBeacon> getBeacons() {

        Log.v(TAG, "getBeacons");
        return beacons;
    }

    public String getType() {

        Log.v(TAG, "getType");
        return type;
    }

    public String getPortraitBannerUrl() {

        Log.v(TAG, "getPortraitBannerUrl");
        return portrait_banner_url;
    }
    //==============================================================================================
    // Helpers
    //==============================================================================================

    /**
     * This function will return the Beacon URL on the bases of beacon type.
     * It will traverse all beacons and search for <code><beaconType<code/> beaconType.
     *
     * @param beaconType type of beacon
     *
     * @return return Beacon URL or null otherwise.
     */
    public String getBeacon(String beaconType) {

        Log.v(TAG, "getBeacon: " + beaconType);
        String beaconUrl = null;
        if (!TextUtils.isEmpty(beaconType) && beacons != null) {
            for (PubnativeBeacon beacon : beacons) {
                if (beaconType.equalsIgnoreCase(beacon.type)) {
                    beaconUrl = beacon.url;
                    break;
                }
            }
        }
        return beaconUrl;
    }

    //==============================================================================================
    // Tracking
    //==============================================================================================
    private transient PubnativeImpressionTracker mPubnativeAdTracker    = null;
    private transient boolean                    mIsImpressionConfirmed = false;
    private transient View                       mClickableView         = null;

    /**
     * Start tracking of ad view
     *
     * @param view     ad view
     * @param listener listener for callbacks
     */
    public void startTracking(View view, Listener listener) {

        Log.v(TAG, "startTracking(View, Listener)");
        startTracking(view, view, listener);
    }

    /**
     * start tracking of your ad view
     *
     * @param view          ad view
     * @param clickableView clickable view
     * @param listener      listener for callbacks
     */
    public void startTracking(View view, View clickableView, Listener listener) {

        Log.v(TAG, "startTracking");
        mListener = listener;
        // Impression tracking
        String impressionURL = getBeacon(PubnativeBeacon.BeaconType.IMPRESSION);
        if (TextUtils.isEmpty((impressionURL))) {
            Log.e(TAG, "startTracking - Error: impression url is empty, impression cannot be tracked");
        } else if (mIsImpressionConfirmed) {
            Log.v(TAG, "startTracking - impression is already confirmed, dropping impression tracking");
        } else {
            if (mPubnativeAdTracker == null) {
                mPubnativeAdTracker = new PubnativeImpressionTracker(view, impressionURL, this);
            }
            mPubnativeAdTracker.startTracking();
        }
        // Click tracking
        if (TextUtils.isEmpty(getClickUrl())) {
            Log.e(TAG, "startTracking - Error: click url is empty, clicks won't be tracked");
        } else {
            mClickableView = clickableView;
            mClickableView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Log.v(TAG, "onClick");
                    URLDriller driller = new URLDriller();
                    driller.setListener(PubnativeAdModel.this);
                    driller.drill(mClickableView.getContext(), getClickUrl());
                }
            });
        }
    }

    /**
     * stop tracking of ad view
     */
    public void stopTracking() {

        Log.v(TAG, "stopTracking");
        mPubnativeAdTracker.stopTracking();
        if (mClickableView != null) {
            mClickableView.setOnClickListener(null);
        }
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================
    protected void invokeOnImpression(View view) {

        Log.v(TAG, "invokeOnImpression");
        mIsImpressionConfirmed = true;
        if (mListener != null) {
            mListener.onPubnativeAdModelImpression(PubnativeAdModel.this, view);
        }
    }

    protected void invokeOnClick(View view) {

        Log.v(TAG, "invokeOnClick");
        if (mListener != null) {
            mListener.onPubnativeAdModelClick(PubnativeAdModel.this, view);
        }
    }

    protected void invokeOnOpenOffer() {

        Log.v(TAG, "invokeOnOpenOffer");
        if (mListener != null) {
            mListener.onPubnativeAdModelOpenOffer(this);
        }
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PubnativeImpressionTracker.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onTrackerImpression(View view) {

        Log.v(TAG, "onTrackerImpression");
        invokeOnImpression(view);
    }

    // URLDriller.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onURLDrillerStart(String url) {

        Log.v(TAG, "onURLDrillerStart: " + url);
    }

    @Override
    public void onURLDrillerRedirect(String url) {

        Log.v(TAG, "onURLDrillerRedirect: " + url);
    }

    @Override
    public void onURLDrillerFinish(String url) {

        Log.v(TAG, "onURLDrillerFinish: " + url);
        invokeOnOpenOffer();
    }

    @Override
    public void onURLDrillerFail(String url, Exception exception) {

        Log.v(TAG, "onURLDrillerFail: " + exception);
    }
}
