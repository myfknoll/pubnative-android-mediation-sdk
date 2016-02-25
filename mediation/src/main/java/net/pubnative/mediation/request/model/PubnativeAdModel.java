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

package net.pubnative.mediation.request.model;

import android.content.Context;
import android.view.View;

import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;
import net.pubnative.mediation.insights.PubnativeInsightsManager;

import java.util.Map;

public abstract class PubnativeAdModel {

    // Model
    protected Context                  mContext = null;
    protected PubnativeAdModelListener mListener = null;

    // Tracking
    protected PubnativeInsightDataModel mTrackingInfoModel      = null;
    protected String                    mImpressionTrackingURL  = null;
    protected String                    mClickTrackingURL       = null;
    protected String                    mAppToken               = null;
    protected String                    mRequestID              = null;
    protected Long                      mPlacementResponseTime  = null;
    protected Long                      mNetworkResponseTime    = null;
    protected Map<String, String>       mImpressionParameters   = null;
    protected Map<String, String>       mClickParameters        = null;

    // Private
    protected boolean mImpressionTracked = false;
    protected boolean mClickTracked      = false;

    public void setListener(PubnativeAdModelListener listener) {

        mListener = listener;
    }

    /**
     * gets title of the current ad
     *
     * @return short string with ad title
     */
    public abstract String getTitle();

    /**
     * gets description of the current ad
     *
     * @return long string with ad details
     */
    public abstract String getDescription();

    /**
     * gets the URL where to download the ad icon from
     *
     * @return icon URL string
     */
    public abstract String getIconUrl();

    /**
     * gets the URL where to download the ad banner from
     *
     * @return banner URL string
     */
    public abstract String getBannerUrl();

    /**
     * gets the call to action string (download, free, etc)
     *
     * @return call to action string
     */
    public abstract String getCallToAction();

    /**
     * gets the star rating in a base of 5 stars
     *
     * @return float with value between 0.0 and 5.0
     */
    public abstract float getStarRating();

    /**
     * gets the advertising disclosure item for the current network (Ad choices, Sponsor label, etc)
     *
     * @param context context
     *
     * @return Disclosure view to be added on top of the ad.
     */
    public abstract View getAdvertisingDisclosureView(Context context);

    /**
     * Start tracking a view to automatically confirm impressions and handle clicks
     *
     * @param context context
     * @param adView  view that will handle clicks and will be tracked to confirm impression
     */
    public abstract void startTracking(Context context, View adView);

    /**
     * Stop using the view for confirming impression and handle clicks
     *
     * @param context context
     * @param adView  view that will be removed as tracking view.
     */
    public abstract void stopTracking(Context context, View adView);

    /**
     * Sets impression tracking info
     *
     * @param impressionURL impression tracking URL
     * @param parameters    extra parameters to be sent as querystring
     */
    public void setTrackingImpressionData(String impressionURL, Map parameters) {

        mImpressionParameters = parameters;
        mImpressionTrackingURL = impressionURL;
    }

    /**
     * Sets click tracking info
     *
     * @param clickURL   click tracking URL
     * @param parameters extra parameters to be sent as querystring
     */
    public void setTrackingClickData(String clickURL, Map parameters) {

        mClickParameters = parameters;
        mClickTrackingURL = clickURL;
    }

    /**
     * Sets extended tracking (used to initialize the view)
     *
     * @param trackingInfoModel tracking model
     */
    public void setTrackingInfo(PubnativeInsightDataModel trackingInfoModel) {

        mTrackingInfoModel = trackingInfoModel;
        setTrackingCreative();
    }

    protected void setTrackingCreative() {

        if (mTrackingInfoModel != null) {

            mTrackingInfoModel.creative_url = getBannerUrl();

            if (PubnativePlacementModel.AdFormatCode.NATIVE_ICON.equals(mTrackingInfoModel.ad_format_code)) {

                mTrackingInfoModel.creative_url = getIconUrl();
            }
        }
    }

    protected void invokeOnAdImpressionConfirmed() {

        if (!mImpressionTracked) {

            mImpressionTracked = true;

            if (mContext != null && mTrackingInfoModel != null) {

                PubnativeDeliveryManager.logImpression(mContext, mTrackingInfoModel.placement_name);
                PubnativeInsightsManager.trackData(mContext, mImpressionTrackingURL, mImpressionParameters, mTrackingInfoModel);
            }

            if (mListener != null) {

                mListener.onAdImpressionConfirmed(this);
            }
        }
    }

    protected void invokeOnAdClick() {

        if (!mClickTracked) {

            mClickTracked = true;

            if (mContext != null && mTrackingInfoModel != null) {

                PubnativeInsightsManager.trackData(mContext, mClickTrackingURL, mClickParameters, mTrackingInfoModel);
            }

            if (mListener != null) {

                mListener.onAdClick(this);
            }
        }
    }
}
