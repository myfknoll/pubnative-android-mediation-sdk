package net.pubnative.mediation.model.network;

import android.content.Context;
import android.view.View;

import com.facebook.ads.NativeAd;

import net.pubnative.mediation.model.PubnativeAdModel;

/**
 * Created by rahul on 6/8/15.
 */
public class FacebookNativeAdModel extends PubnativeAdModel
{
    private NativeAd nativeAd;

    public FacebookNativeAdModel(NativeAd nativeAd)
    {
        this.nativeAd = nativeAd;
    }

    @Override
    protected String getTitle()
    {
        return nativeAd.getAdTitle();
    }

    @Override
    protected String getDescription()
    {
        return nativeAd.getAdBody();
    }

    @Override
    protected String getIconUrl()
    {
        String iconUrl = null;
        if(nativeAd.getAdIcon() != null)
        {
            iconUrl = nativeAd.getAdIcon().getUrl();
        }
        return iconUrl;
    }

    @Override
    protected String getBannerUrl()
    {
        String bannerUrl = null;
        if(nativeAd.getAdCoverImage() != null)
        {
            bannerUrl = nativeAd.getAdCoverImage().getUrl();
        }
        return bannerUrl;
    }

    @Override
    protected String getCallToAction()
    {
        return nativeAd.getAdCallToAction();
    }

    @Override
    protected float getStarRating()
    {
        float starRating = 0;
        if (this.nativeAd != null)
        {
            NativeAd.Rating rating = nativeAd.getAdStarRating();
            if (rating != null)
            {
                double rating_scale = rating.getScale();
                double rating_value = rating.getValue();
                starRating = (float) ((rating_value / rating_scale) * 5);
            }
        }
        return starRating;
    }

    @Override
    protected void registerAdView(Context context, View adView)
    {
        nativeAd.registerViewForInteraction(adView);
        // TODO: register view for interactions.
        // TODO: Planning to write a method inside super class to use generally.
    }

    @Override
    protected void unregisterAdView(Context context, View adView)
    {
        nativeAd.unregisterView();
        // TODO: Method to remove callback bound with adView.
    }
}
