package net.pubnative.mediation.model;

/**
 * Created by davidmartin on 20/08/15.
 */
public interface PubnativeAdModelListener
{
    void onAdImpressionConfirmed(PubnativeAdModel model);

    void onAdClick(PubnativeAdModel model);
}
