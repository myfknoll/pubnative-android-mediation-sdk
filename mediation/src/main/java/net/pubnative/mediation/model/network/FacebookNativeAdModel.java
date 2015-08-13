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
    public String getTitle()
    {
        String result = null;
        if (this.nativeAd != null) {
            result = this.nativeAd.getAdTitle();
        }
        return result;
    }

    @Override
    public String getDescription()
    {
        String result = null;
        if (this.nativeAd != null)
        {
            result = this.nativeAd.getAdBody();
        }
        return result;
    }

    @Override
    public String getIconUrl()
    {
        String iconUrl = null;
        if(this.nativeAd != null && this.nativeAd.getAdIcon() != null)
        {
            iconUrl = this.nativeAd.getAdIcon().getUrl();
        }
        return iconUrl;
    }

    @Override
    public String getBannerUrl()
    {
        String bannerUrl = null;
        if(this.nativeAd != null && this.nativeAd.getAdCoverImage() != null)
        {
            bannerUrl = this.nativeAd.getAdCoverImage().getUrl();
        }
        return bannerUrl;
    }

    @Override
    public String getCallToAction()
    {
        String result = null;
        if (this.nativeAd != null)
        {
            result = this.nativeAd.getAdCallToAction();
        }
        return result;
    }

    @Override
    public float getStarRating()
    {
        float starRating = 0;
        if (this.nativeAd != null)
        {
            NativeAd.Rating rating = this.nativeAd.getAdStarRating();
            if (rating != null)
            {
                double ratingScale = rating.getScale();
                double ratingValue = rating.getValue();
                starRating = (float) ((ratingValue / ratingScale) * 5);
            }
        }
        return starRating;
    }

    public void registerAdView(Context context, View adView)
    {
        if (this.nativeAd != null && context != null && adView != null)
        {
            this.nativeAd.registerViewForInteraction(adView);
        }
        // TODO: register view for interactions.
        // TODO: Planning to write a method inside super class to use generally.
    }

    @Override
    public void unregisterAdView(Context context, View adView)
    {
        if (this.nativeAd != null)
        {
            this.nativeAd.unregisterView();
        }
        // TODO: Method to remove callback bound with adView.
    }
}
