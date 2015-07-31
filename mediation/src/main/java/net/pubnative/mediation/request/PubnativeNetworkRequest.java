package net.pubnative.mediation.request;

import android.content.Context;

import net.pubnative.mediation.model.PubnativeAdModel;

import java.util.ArrayList;

public class PubnativeNetworkRequest
{
    protected PubnativeNetworkRequestListener listener;
    
    public PubnativeNetworkRequest()
    {
        // Do some initialization here
    }
    
    public void request(Context context, String app_token, PubnativeNetworkRequestListener listener)
    {
        if(context != null && listener != null)
        {
            this.listener = listener;
            if(app_token != null && !app_token.isEmpty())
            {
                this.invokeStart();
                // 1. Ask config
                // 2. Take decision
                // 3. Make request
                // 4. Post-check
                // 5. invokeLoad(models)
            }
            else
            {
                this.invokeFail(new IllegalArgumentException("app_token argument cannot be null or empty"));
            }
        }
        else
        {
            throw new RuntimeException("PubnativeNetworkRequest.request - context and listener arguments must be specified");
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
