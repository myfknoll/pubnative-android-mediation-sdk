package net.pubnative.mediation.request.model;

import android.content.Context;
import android.view.View;

import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;
import net.pubnative.mediation.insights.PubnativeInsightsManager;

public abstract class PubnativeAdModel {

    protected boolean                   impressionTracked     = false;
    protected boolean                   clickTracked          = false;
    protected PubnativeInsightDataModel trackingInfoModel     = null;
    protected PubnativeAdModelListener  listener              = null;
    protected String                    impressionTrackingURL = null;
    protected String                    clickTrackingURL      = null;
    protected Context                   context               = null;

    public void setListener(PubnativeAdModelListener listener) {
        this.listener = listener;
    }

    // abstract get methods.
    public abstract String getTitle();

    public abstract String getDescription();

    public abstract String getIconUrl();

    public abstract String getBannerUrl();

    public abstract String getCallToAction();

    public abstract float getStarRating();

    public abstract View getPrivacyView();

    // abstract methods for actions on adView
    public abstract void startTracking(Context context, View adView);

    public abstract void stopTracking(Context context, View adView);

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
