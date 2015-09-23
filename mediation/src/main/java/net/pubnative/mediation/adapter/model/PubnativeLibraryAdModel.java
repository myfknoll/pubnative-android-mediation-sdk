package net.pubnative.mediation.adapter.model;

import android.content.Context;
import android.view.View;

import net.pubnative.library.model.NativeAdModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;

/**
 * Created by davidmartin on 10/08/15.
 */
public class PubnativeLibraryAdModel extends PubnativeAdModel implements View.OnClickListener, NativeAdModel.Listener
{
    protected NativeAdModel model = null;

    public PubnativeLibraryAdModel(NativeAdModel model)
    {
        this.model = model;
    }

    @Override
    public String getTitle()
    {
        String result = null;
        if (model != null)
        {
            result = model.title;
        }
        return result;
    }

    @Override
    public String getDescription()
    {
        String result = null;
        if (model != null)
        {
            result = model.description;
        }
        return result;
    }

    @Override
    public String getIconUrl()
    {
        String result = null;
        if (model != null)
        {
            result = model.iconUrl;
        }
        return result;
    }

    @Override
    public String getBannerUrl()
    {
        String result = null;
        if (model != null)
        {
            result = model.bannerUrl;
        }
        return result;
    }

    @Override
    public String getCallToAction()
    {
        String result = null;
        if (model != null)
        {
            result = model.ctaText;
        }
        return result;
    }

    @Override
    public float getStarRating()
    {
        float starRating = 0;
        if (this.model != null && this.model.app_details != null)
        {
            starRating = model.app_details.store_rating;
        }
        return starRating;
    }

    @Override
    public void startTracking(Context context, View adView)
    {
        if (this.model != null && context != null && adView != null)
        {
            this.context = context;
            adView.setOnClickListener(this);
            this.model.confirmImpressionAutomatically(context, adView, this);
        }
    }

    @Override
    public void stopTracking(Context context, View adView)
    {
        // Do nothing
    }

    @Override
    public void onClick(View view)
    {
        this.invokeOnAdClick();
        this.model.open(this.context);
    }

    // Pubnative NativeAdModel.Listener
    @Override
    public void onAdImpression(NativeAdModel model)
    {
        this.invokeOnAdImpressionConfirmed();
    }
}
