package net.pubnative.mediation.adapter;

import java.util.ArrayList;

import net.pubnative.mediation.model.PubnativeAdModel;
import net.pubnative.mediation.model.PubnativeNetworkModel;

public abstract class PubnativeNetworkAdapter
{
    private PubnativeNetworkAdapterListener listener;
    private PubnativeNetworkModel networkModel;
    
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
    
    private void invokeStart()
    {
        if(this.listener != null)
        {
            this.listener.onAdapterRequestStarted(this);
        }
    }
    
    private void invokeLoaded(ArrayList<PubnativeAdModel> networkAds)
    {
        if(this.listener != null)
        {
            this.listener.onAdapterRequestLoaded(this, networkAds);
        }
    }
    
    private void invokeFailed(Exception exception)
    {
        if(this.listener != null)
        {
            this.listener.onAdapterRequestFailed(this, exception);
        }
    }
}
