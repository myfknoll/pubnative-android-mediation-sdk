package net.pubnative.mediation.model;

import android.view.View;

public abstract class PubnativeAdModel
{
    private View adView;

    // abstract get methods.
    protected abstract String getTitle();
    protected abstract String getDescription();
    protected abstract String getIconUrl();
    protected abstract String getBannerUrl();
    protected abstract String getCallToAction();

    // abstract methods for actions on adView
    protected abstract void registerAdView(View adView);
    protected abstract void unregisterAdView(View adView);
}
