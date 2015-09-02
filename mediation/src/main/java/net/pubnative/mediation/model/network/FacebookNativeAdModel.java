package net.pubnative.mediation.model.network;

import android.content.Context;
import android.view.View;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.ImpressionListener;
import com.facebook.ads.NativeAd;

import net.pubnative.mediation.model.PubnativeAdModel;

/**
 * Created by rahul on 6/8/15.
 */
public class FacebookNativeAdModel extends PubnativeAdModel implements ImpressionListener, AdListener
{
    protected NativeAd nativeAd;

    public FacebookNativeAdModel(NativeAd nativeAd)
    {
        if (nativeAd != null)
        {
            this.nativeAd = nativeAd;
            this.nativeAd.setAdListener(this);
            this.nativeAd.setImpressionListener(this);
        }
    }

    @Override
    public String getTitle()
    {
        String result = null;
        if (this.nativeAd != null)
        {
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
        if (this.nativeAd != null && this.nativeAd.getAdIcon() != null)
        {
            iconUrl = this.nativeAd.getAdIcon().getUrl();
        }
        return iconUrl;
    }

    @Override
    public String getBannerUrl()
    {
        String bannerUrl = null;
        if (this.nativeAd != null && this.nativeAd.getAdCoverImage() != null)
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

    @Override
    public void startTracking(Context context, View adView)
    {
        if (context != null && this.nativeAd != null && adView != null)
        {
            this.context = context;
            this.nativeAd.registerViewForInteraction(adView);
        }
    }

    @Override
    public void stopTracking(Context context, View adView)
    {
        if (this.nativeAd != null)
        {
            this.nativeAd.unregisterView();
        }
    }

    // Facebook

    @Override
    public void onLoggingImpression(Ad ad)
    {
        this.invokeOnAdImpressionConfirmed();
    }

    @Override
    public void onError(Ad ad, AdError adError)
    {
        // Do nothing
    }

    @Override
    public void onAdLoaded(Ad ad)
    {
        // Do nothing
    }

    @Override
    public void onAdClicked(Ad ad)
    {
        this.invokeOnAdClick();
    }
}
