package net.pubnative.mediation.model;

import android.content.Context;
import android.view.View;

/**
 * Created by davidmartin on 06/08/15.
 */
public class PubnativeCacheAdModel extends PubnativeAdModel
{
    protected String title;
    protected String description;
    protected String icon_url;
    protected String banner_url;
    protected String call_to_action;

    public PubnativeCacheAdModel(PubnativeAdModel model)
    {
        this.title = model.getTitle();
        this.description = model.getDescription();
        this.icon_url = model.getIconUrl();
        this.banner_url = model.getBannerUrl();
        this.call_to_action = model.getCallToAction();
    }

    @Override
    protected String getTitle()
    {
        return title;
    }

    @Override
    protected String getDescription()
    {
        return description;
    }

    @Override
    protected String getIconUrl()
    {
        return icon_url;
    }

    @Override
    protected String getBannerUrl()
    {
        return banner_url;
    }

    @Override
    protected String getCallToAction()
    {
        return call_to_action;
    }

    @Override
    protected void registerAdView(Context context, View adView)
    {
        // Do nothing
    }

    @Override
    protected void unregisterAdView(Context context, View adView)
    {
        // Do nothing
    }
}
