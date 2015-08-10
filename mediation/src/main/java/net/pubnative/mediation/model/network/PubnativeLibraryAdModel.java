package net.pubnative.mediation.model.network;

import android.content.Context;
import android.view.View;

import net.pubnative.library.model.NativeAdModel;
import net.pubnative.mediation.model.PubnativeAdModel;

/**
 * Created by davidmartin on 10/08/15.
 */
public class PubnativeLibraryAdModel extends PubnativeAdModel
{
    private NativeAdModel model;
    public PubnativeLibraryAdModel(NativeAdModel model)
    {
        this.model = model;
    }

    @Override
    protected String getTitle()
    {
        String result = null;
        if(model != null)
        {
            result = model.title;
        }
        return result;
    }

    @Override
    protected String getDescription()
    {
        String result = null;
        if(model != null)
        {
            result = model.description;
        }
        return result;
    }

    @Override
    protected String getIconUrl()
    {
        String result = null;
        if(model != null)
        {
            result = model.iconUrl;
        }
        return result;
    }

    @Override
    protected String getBannerUrl()
    {
        String result = null;
        if(model != null)
        {
            result = model.bannerUrl;
        }
        return result;
    }

    @Override
    protected String getCallToAction()
    {
        String result = null;
        if(model != null)
        {
            result = model.ctaText;
        }
        return result;
    }

    @Override
    protected void registerAdView(Context context, View adView)
    {
        model.confirmImpressionAutomatically(context, adView);
    }

    @Override
    protected void unregisterAdView(Context context, View adView)
    {
        // Do nothing
    }
}
