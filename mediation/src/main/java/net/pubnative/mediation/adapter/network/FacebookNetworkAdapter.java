package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.model.network.FacebookNativeAdModel;

import java.util.Map;

public class FacebookNetworkAdapter extends PubnativeNetworkAdapter implements AdListener
{
    protected static final String KEY_PLACEMENT_ID = "placement_id";

    protected  NativeAd nativeAd;

    public FacebookNetworkAdapter(Map data)
    {
        super(data);
    }

    @Override
    public void request(Context context)
    {
        if (data != null && data.containsKey(KEY_PLACEMENT_ID))
        {
            String placementId = (String) data.get(KEY_PLACEMENT_ID);
            if (!TextUtils.isEmpty(placementId))
            {
                this.createRequest(context, placementId);
            }
            else
            {
                invokeFailed(new Exception("Invalid placement_id provided."));
            }
        }
        else
        {
            invokeFailed(new Exception("No placement id provided."));
        }
    }

    protected void createRequest(Context context, String placementId)
    {
        this.nativeAd = new NativeAd(context, placementId);
        this.nativeAd.setAdListener(this);
        this.nativeAd.loadAd();
    }

    @Override
    public void onError(Ad ad, AdError adError)
    {
        if(ad == this.nativeAd)
        {
            String errorMessage = "Pubnative - Facebook adapter error: Unknown error";
            if (adError != null)
            {
                errorMessage = adError.getErrorMessage();
            }
            this.invokeFailed(new Exception(errorMessage));
        }
    }

    @Override
    public void onAdLoaded(Ad ad)
    {
        if(ad == this.nativeAd)
        {
            FacebookNativeAdModel wrapModel = new FacebookNativeAdModel((NativeAd) ad);
            this.invokeLoaded(wrapModel);
        }
    }

    @Override
    public void onAdClicked(Ad ad)
    {
        // Do nothing
    }
}
