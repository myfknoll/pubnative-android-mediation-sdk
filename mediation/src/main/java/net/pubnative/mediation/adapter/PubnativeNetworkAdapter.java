package net.pubnative.mediation.adapter;

import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.PubnativeNetworkModel;

import java.util.ArrayList;

public abstract class PubnativeNetworkAdapter
{
    protected PubnativeNetworkAdapterListener listener;
    protected PubnativeNetworkModel networkModel;
    
    public PubnativeNetworkAdapter(PubnativeNetworkModel networkModel)
    {
        this.networkModel = networkModel;
    }
    
    public void doRequest(PubnativeNetworkAdapterListener listener)
    {
        if(this.listener != null)
        {
            this.listener = listener;
            this.invokeStart();
            this.request();
        }
        else
        {
            new RuntimeException("PubnativeNetworkAdapter.doRequest - should specify a listener");
        }
    }
    
    public abstract void request();
    
    protected void invokeStart()
    {
        if(this.listener != null)
        {
            this.listener.onAdapterRequestStarted(this);
        }
    }
    
    protected void invokeLoaded(ArrayList<PubnativeAdModel> networkAds)
    {
        if(this.listener != null)
        {
            this.listener.onAdapterRequestLoaded(this, networkAds);
        }
    }
    
    protected void invokeFailed(Exception exception)
    {
        if(this.listener != null)
        {
            this.listener.onAdapterRequestFailed(this, exception);
        }
    }
}
