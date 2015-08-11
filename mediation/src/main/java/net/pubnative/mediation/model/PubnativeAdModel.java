package net.pubnative.mediation.model;

import android.content.Context;
import android.view.View;

public abstract class PubnativeAdModel
{
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
}
