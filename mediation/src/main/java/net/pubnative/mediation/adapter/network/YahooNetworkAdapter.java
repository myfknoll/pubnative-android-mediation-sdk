package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;

import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdNative;
import com.flurry.android.ads.FlurryAdNativeListener;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.model.network.FlurryNativeAdModel;

import java.util.Map;

public class YahooNetworkAdapter extends PubnativeNetworkAdapter implements FlurryAdNativeListener
{
    public static final String KEY_AD_SPACE_NAME = "ad_space_name";
    public static final String KEY_FLURRY_API_KEY = "api_key";
    private Context context;

    public YahooNetworkAdapter(Map data)
    {
        super(data);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void request(Context context)
    {
        this.context = context;

        if (data != null && data.containsKey(KEY_AD_SPACE_NAME) && data.containsKey(KEY_FLURRY_API_KEY))
        {
            String apiKey = (String) data.get(KEY_FLURRY_API_KEY);
            if (!TextUtils.isEmpty(apiKey))
            {
                String adSpaceName = (String) data.get(KEY_AD_SPACE_NAME);
                if (!TextUtils.isEmpty(adSpaceName))
                {
                    this.createRequest(context, adSpaceName, apiKey);
                }
                else
                {
                    invokeFailed(new Exception("Invalid ad_space_name provided."));
                }
            }
            else
            {
                invokeFailed(new Exception("Invalid api_key provided."));
            }
        }
        else
        {
            invokeFailed(new Exception("No ad_space_name or api_key provided."));
        }
    }

    protected void endFlurrySession(Context context)
    {
        FlurryAgent.onEndSession(context);
    }

    protected void createRequest(Context context, String adSpaceName, String apiKey)
    {
        // configure flurry
        FlurryAgent.setLogEnabled(true);
        // initialize flurry with new apiKey
        FlurryAgent.init(context, apiKey);
        // start/resume session
        if (!FlurryAgent.isSessionActive())
        {
            FlurryAgent.onStartSession(context);
        }
        // Make request
        FlurryAdNative flurryAdNative = new FlurryAdNative(context, adSpaceName);
        flurryAdNative.setListener(this);
        flurryAdNative.fetchAd();
    }

    @Override
    public void onFetched(FlurryAdNative flurryAdNative)
    {
        this.endFlurrySession(this.context);
        FlurryNativeAdModel nativeAdModel = new FlurryNativeAdModel(flurryAdNative);
        this.invokeLoaded(nativeAdModel);
    }

    @Override
    public void onError(FlurryAdNative flurryAdNative, FlurryAdErrorType flurryAdErrorType, int errCode)
    {
        this.endFlurrySession(this.context);
        String errorMessage = "Pubnative - Flurry adapter error: Unknown error";
        if (flurryAdErrorType != null)
        {
            errorMessage = "Pubnative - Flurry adapter Error" +
                    " Type: " + flurryAdErrorType.name() +
                    " Error Code: " + errCode;
        }
        this.invokeFailed(new Exception(errorMessage));
    }

    @Override
    public void onShowFullscreen(FlurryAdNative flurryAdNative)
    {
        // Do nothing for now.
    }

    @Override
    public void onCloseFullscreen(FlurryAdNative flurryAdNative)
    {
        // Do nothing for now.
    }

    @Override
    public void onAppExit(FlurryAdNative flurryAdNative)
    {
        // Do nothing for now.
    }

    @Override
    public void onClicked(FlurryAdNative flurryAdNative)
    {
        // Do nothing for now.
    }

    @Override
    public void onImpressionLogged(FlurryAdNative flurryAdNative)
    {
        // Do nothing for now.
    }
}
