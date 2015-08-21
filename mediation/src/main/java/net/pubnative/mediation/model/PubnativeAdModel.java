package net.pubnative.mediation.model;

import android.content.Context;
import android.view.View;

public abstract class PubnativeAdModel
{
    protected boolean                  impressionTracked = false;
    protected boolean                  clickTracked = false;
    protected PubnativeAdModelListener listener;

    public void setListener(PubnativeAdModelListener listener)
    {
        this.listener = listener;
    }

    // abstract get methods.
    public abstract String getTitle();

    public abstract String getDescription();

    public abstract String getIconUrl();

    public abstract String getBannerUrl();

    public abstract String getCallToAction();

    public abstract float getStarRating();

    // abstract methods for actions on adView
    public abstract void registerAdView(Context context, View adView);

    public abstract void unregisterAdView(Context context, View adView);

    protected void invokeOnAdImpressionConfirmed()
    {
        if (this.listener != null && !this.impressionTracked)
        {
            // TODO: Track impression

            this.impressionTracked = true;
            this.listener.onAdImpressionConfirmed(this);
        }
    }

    protected void invokeOnAdClick()
    {
        if (this.listener != null && !this.clickTracked)
        {
            // TODO: Track click

            this.clickTracked = true;
            this.listener.onAdClick(this);
        }
    }
}
