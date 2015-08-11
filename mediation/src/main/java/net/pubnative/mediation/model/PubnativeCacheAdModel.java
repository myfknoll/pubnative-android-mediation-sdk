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
    protected float star_rating;

    public PubnativeCacheAdModel(PubnativeAdModel model)
    {
        this.title = model.getTitle();
        this.description = model.getDescription();
        this.icon_url = model.getIconUrl();
        this.banner_url = model.getBannerUrl();
        this.call_to_action = model.getCallToAction();
        this.star_rating = model.getStarRating();
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String getIconUrl()
    {
        return icon_url;
    }

    @Override
    public String getBannerUrl()
    {
        return banner_url;
    }

    @Override
    public String getCallToAction()
    {
        return call_to_action;
    }

    @Override
    public float getStarRating()
    {
        return star_rating;
    }

    @Override
    public void registerAdView(Context context, View adView)
    {
        // Do nothing
    }

    @Override
    public void unregisterAdView(Context context, View adView)
    {
        // Do nothing
    }
}
