package net.pubnative.mediation.request;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.text.TextUtils;

import net.pubnative.mediation.config.PubnativeConfigManager;
import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.PubnativeConfigModel;

import java.util.ArrayList;

public class PubnativeNetworkRequest
{
    protected PubnativeNetworkRequestListener listener;
    
    public PubnativeNetworkRequest()
    {
        // Do some initialization here
    }
    
    public void request(Context context, String app_token, String placement_id, PubnativeNetworkRequestListener listener)
    {
        if(listener == null)
        {
            // Just drop the call
            return;
        }

        this.listener = listener;


        if(context != null && !TextUtils.isEmpty(app_token) && !TextUtils.isEmpty(placement_id))
        {
            this.invokeStart();

            PubnativeConfigModel config = PubnativeConfigManager.config(context, app_token);
            if(config != null)
            {
                // TODO: Complete request
            }
            else
            {
                this.invokeFail(new NetworkErrorException("PubnativeNetworkRequest.request"));
            }
        }
        else
        {
            this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.request"));
        }
    }
    
    protected void invokeStart()
    {
        if(this.listener != null)
        {
            this.listener.onRequestStarted(this);
        }
    }
    
    protected void invokeLoad(ArrayList<PubnativeAdModel> ads)
    {
        if(this.listener != null)
        {
            this.listener.onRequestLoaded(this, ads);
        }
    }
    
    protected void invokeFail(Exception exception)
    {
        if(this.listener != null)
        {
            this.listener.onRequestFailed(this, exception);
        }
    }
}
