package net.pubnative.mediation.adapter.network;

import android.content.Context;

import net.pubnative.library.PubnativeContract.Request;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.request.AdRequest;
import net.pubnative.library.request.AdRequestListener;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.network.PubnativeLibraryAdModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PubnativeLibraryNetworkAdapter extends PubnativeNetworkAdapter implements AdRequestListener
{
    protected final static String APP_TOKEN_KEY = "app_token";
    public PubnativeLibraryNetworkAdapter(Map data)
    {
        super(data);
    }

    @Override
    public void request(Context context)
    {
        AdRequest request = new AdRequest(context);
        if(data.containsKey(APP_TOKEN_KEY))
        {
            String app_token = (String) data.get(APP_TOKEN_KEY);
            request.setParameter(Request.APP_TOKEN, app_token);
        }
        request.setParameter(Request.AD_COUNT, this.ad_count.toString());
        request.start(AdRequest.Endpoint.NATIVE, this);
    }

    @Override
    public void onAdRequestStarted(AdRequest request)
    {
        // Do nothing
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads)
    {
        List<PubnativeAdModel> wrapAds = new ArrayList<>();
        for(NativeAdModel model : ads)
        {
            wrapAds.add(new PubnativeLibraryAdModel(model));
        }
        this.invokeLoaded(wrapAds);
    }

    @Override
    public void onAdRequestFailed(AdRequest request, Exception ex)
    {
        this.invokeFailed(ex);
    }
}
