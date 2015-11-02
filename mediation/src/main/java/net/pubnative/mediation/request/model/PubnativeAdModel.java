package net.pubnative.mediation.request.model;

import android.content.Context;
import android.view.View;

import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;
import net.pubnative.mediation.insights.PubnativeInsightsManager;

public abstract class PubnativeAdModel {
    protected boolean impressionTracked = false;
    protected boolean clickTracked = false;
    protected PubnativeInsightDataModel trackingInfoModel = null;
    protected PubnativeAdModelListener listener = null;
    protected String impressionTrackingURL = null;
    protected String clickTrackingURL = null;
    protected Context context = null;

    public void setListener(PubnativeAdModelListener listener) {
        this.listener = listener;
    }

    /**
     * gets title of the current ad
     * @return short string with ad title
     */
    public abstract String getTitle();

    /**
     * gets description of the current ad
     * @return long string with ad details
     */
    public abstract String getDescription();

    /**
     * gets the URL where to download the ad icon from
     * @return icon URL string
     */
    public abstract String getIconUrl();

    /**
     * gets the URL where to download the ad banner from
     * @return banner URL string
     */
    public abstract String getBannerUrl();

    /**
     * gets the call to action string (download, free, etc)
     * @return call to action string
     */
    public abstract String getCallToAction();

    /**
     * gets the star rating in a base of 5 stars
     * @return float with value between 0.0 and 5.0
     */
    public abstract float getStarRating();

    /**
     * gets the advertising disclosure item for the current network (Ad choices, Sponsor label, etc)
     * @param context context
     * @return Disclosure view to be added on top of the ad.
     */
    public abstract View getAdvertisingDisclosureView(Context context);

    /**
     * Start tracking a view to automatically confirm impressions and handle clicks
     * @param context context
     * @param adView view that will handle clicks and will be tracked to confirm impression
     */
    public abstract void startTracking(Context context, View adView);

    /**
     * Stop using the view for confirming impression and handle clicks
     * @param context context
     * @param adView view that will be removed as tracking view.
     */
    public abstract void stopTracking(Context context, View adView);

    /**
     * Sets extended tracking (used to initialize the view)
     * @param trackingInfoModel tracking model
     * @param impressionURL composed tracking impression url
     * @param clickURL composed tracking click url
     */
    public void setTrackingInfo(PubnativeInsightDataModel trackingInfoModel, String impressionURL, String clickURL) {
        this.impressionTrackingURL = impressionURL;
        this.clickTrackingURL = clickURL;
        this.trackingInfoModel = trackingInfoModel;
        this.setTrackingCreative();
    }

    protected void setTrackingCreative() {
        if (this.trackingInfoModel != null) {
            this.trackingInfoModel.creative_url = this.getBannerUrl();
            if (PubnativePlacementModel.AdFormatCode.NATIVE_ICON.equals(this.trackingInfoModel.ad_format_code)) {
                this.trackingInfoModel.creative_url = this.getIconUrl();
            }
        }
    }

    protected void invokeOnAdImpressionConfirmed() {
        if (!this.impressionTracked) {
            this.impressionTracked = true;
            if (this.context != null && this.trackingInfoModel != null) {
                PubnativeDeliveryManager.logImpression(this.context, this.trackingInfoModel.placement_name);
                PubnativeInsightsManager.trackData(this.context, this.impressionTrackingURL, this.trackingInfoModel);
            }
            if (this.listener != null) {
                this.listener.onAdImpressionConfirmed(this);
            }
        }
    }

    protected void invokeOnAdClick() {
        if (!this.clickTracked) {
            this.clickTracked = true;
            if (this.context != null && this.trackingInfoModel != null) {
                PubnativeInsightsManager.trackData(this.context, this.clickTrackingURL, this.trackingInfoModel);
            }
            if (this.listener != null) {
                this.listener.onAdClick(this);
            }
        }
    }
}
