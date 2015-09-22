package net.pubnative.mediation.adapter.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import net.pubnative.mediation.adapter.ViewImpressionTracker;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.Map;

/**
 * Created by rahul on 16/9/15.
 */
public class LoopMeNativeAdModel extends PubnativeAdModel implements View.OnClickListener, ViewImpressionTracker.OnImpressionListener
{
    private static final String KEY_CONTENT = "content";

    protected Map     image_url;
    protected Map     icon_url;
    protected String  type;
    protected String  adid;
    protected String  title;
    protected String  description;
    protected String  button_text;
    protected String  click_url;
    protected float   rating;

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
        String result = null;
        if (icon_url != null)
        {
            String url = (String) icon_url.get(KEY_CONTENT);
            if (!TextUtils.isEmpty(url))
            {
                result = url;
            }
        }
        return result;
    }

    @Override
    public String getBannerUrl()
    {
        String result = null;
        if (image_url != null)
        {
            String url = (String) image_url.get(KEY_CONTENT);
            if (!TextUtils.isEmpty(url))
            {
                result = url;
            }
        }
        return result;
    }

    @Override
    public String getCallToAction()
    {
        return button_text;
    }

    @Override
    public float getStarRating()
    {
        return rating;
    }

    @Override
    public void startTracking(Context context, View adView)
    {
        this.context = context;

        if (adView != null)
        {
            // enable click listener
            adView.setOnClickListener(this);

            // enable impression tracking
            ViewImpressionTracker.startTracking(this, adView);
        }
    }

    @Override
    public void stopTracking(Context context, View adView)
    {
        // do nothing
    }

    @Override
    public void onClick(View view)
    {
        this.invokeOnAdClick();

        if (context != null && !TextUtils.isEmpty(this.click_url))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.click_url));
            context.startActivity(intent);
        }
    }

    @Override
    public void onAdImpressionConfirmed()
    {
        // impression detected
        this.invokeOnAdImpressionConfirmed();
    }
}
