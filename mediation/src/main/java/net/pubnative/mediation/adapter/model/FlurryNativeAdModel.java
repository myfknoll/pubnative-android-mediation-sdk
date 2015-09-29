package net.pubnative.mediation.adapter.model;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeAsset;

import net.pubnative.mediation.request.model.PubnativeAdModel;

/**
 * Created by rahul on 26/8/15.
 */
public class FlurryNativeAdModel extends PubnativeAdModel implements View.OnClickListener
{
    FlurryAdNative flurryAdNative;

    public FlurryNativeAdModel(FlurryAdNative flurryAdNative)
    {
        this.flurryAdNative = flurryAdNative;
    }

    protected String getStringValueOfAsset(String key)
    {
        String result = null;
        if (this.flurryAdNative != null && !TextUtils.isEmpty(key))
        {
            FlurryAdNativeAsset asset = this.flurryAdNative.getAsset(key);
            if (asset != null && !TextUtils.isEmpty(asset.getValue()))
            {
                result = asset.getValue();
            }
        }
        return result;
    }

    @Override
    public String getTitle()
    {
        // The Ad headline, typically a single line. Type: STRING
        return getStringValueOfAsset("headline");
    }

    @Override
    public String getDescription()
    {
        // The call to action summary of the advertisement. Type: STRING
        return getStringValueOfAsset("summary");
    }

    @Override
    public String getIconUrl()
    {
        // The square high-quality image of the sponsored logo.
        // Currently, it is a starburst.png, always present, size: 40 x 40px
        return getStringValueOfAsset("secOrigImg");
    }

    @Override
    public String getBannerUrl()
    {
        // The secured high-quality image, size: 1200px x 627px
        return getStringValueOfAsset("secHqImage");
    }

    @Override
    public String getCallToAction()
    {
        /**
         * Yahoo currently does not provide the short Call To Action (CTA)
         * asset or string at this time. Instead, you can create your own
         * CTA for each ad. For an ad that contains app install specific assets like
         * “appCategory” or “appRating”, the CTA could be ‘Install Now’.
         * For an ad that does not contain app specific assets, the CTA could be ‘Read More’.
         */
        String result = "Read More";
        if (getStringValueOfAsset("appCategory") != null || getStringValueOfAsset("appRating") != null)
        {
            result = "Install Now";
        }
        return result;
    }

    @Override
    public float getStarRating()
    {
        float result = 0;
        String appRating = getStringValueOfAsset("appRating");
        if (appRating != null)
        {
            String[] parts = appRating.split("/");
            if (parts.length == 2)
            {
                try
                {
                    int ratingVal = Integer.parseInt(parts[0]);
                    int scaleVal = Integer.parseInt(parts[1]);
                    if (scaleVal != 0)
                    {
                        result = (ratingVal / scaleVal) * 5;
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Error while parsing star rating :" + e);
                }
            }
        }
        return result;
    }

    @Override
    public void startTracking(Context context, View adView)
    {
        if (this.flurryAdNative != null && adView != null)
        {
            adView.setOnClickListener(this);
            this.flurryAdNative.setTrackingView(adView);
        }
    }

    @Override
    public void stopTracking(Context context, View adView)
    {
        if (this.flurryAdNative != null)
        {
            this.flurryAdNative.removeTrackingView();
        }

    }

    @Override
    public void onClick(View view)
    {
        this.invokeOnAdClick();
    }
}
