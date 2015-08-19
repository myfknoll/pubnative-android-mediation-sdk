package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdsManager;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.network.FacebookNativeAdModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FacebookNetworkAdapter extends PubnativeNetworkAdapter implements NativeAdsManager.Listener
{
    protected static final String KEY_PLACEMENT_ID = "placement_id";

    protected  NativeAdsManager nativeAdsManager;

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
                this.createRequest(context, placementId, ad_count);
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

    protected void createRequest(Context context, String placementId, int adCount)
    {
        this.nativeAdsManager = new NativeAdsManager(context, placementId, adCount);
        this.nativeAdsManager.setListener(this);
        this.nativeAdsManager.loadAds();
    }

    @Override
    public void onAdsLoaded()
    {
        List<PubnativeAdModel> adModelList = new ArrayList();
        if (this.nativeAdsManager != null)
        {
            for (int count = 0; count < ad_count; count++)
            {
                NativeAd nativeAd = this.nativeAdsManager.nextNativeAd();
                if (nativeAd != null)
                {
                    adModelList.add(new FacebookNativeAdModel(nativeAd));
                }
                else
                {
                    break;
                }
            }
        }

        this.invokeLoaded(adModelList);
    }

    @Override
    public void onAdError(AdError adError)
    {
        String errorMessage = "Pubnative - Facebook adapter error: Unknown error";
        if (adError != null)
        {
            errorMessage = adError.getErrorMessage();
        }

        this.invokeFailed(new Exception(errorMessage));
    }
}
