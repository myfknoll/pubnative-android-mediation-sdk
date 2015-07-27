package net.pubnative.mediation.request;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import net.pubnative.mediation.model.PubnativeAdModel;

public class PubnativeNetworkRequest
{
    private WeakReference<PubnativeNetworkRequestListener> listener;
    
    public PubnativeNetworkRequest()
    {
        // Do some initialization here
    }
    
    public void request(String app_token, PubnativeNetworkRequestListener listener)
    {
        if(listener != null)
        {
            this.listener = new WeakReference<PubnativeNetworkRequestListener>(listener);
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
            throw new RuntimeException("PubnativeNetworkRequest.request - should specify a listener");
        }
    }
    
    private void invokeStart()
    {
        if(this.listener.get() != null)
        {
            this.listener.get().onRequestStarted(this);
        }
    }
    
    private void invokeLoad(ArrayList<PubnativeAdModel> ads)
    {
        if(this.listener.get() != null)
        {
            this.listener.get().onRequestLoaded(this, ads);
        }
    }
    
    private void invokeFail(Exception exception)
    {
        if(this.listener.get() != null)
        {
            this.listener.get().onRequestFailed(this, exception);
        }
    }
}
