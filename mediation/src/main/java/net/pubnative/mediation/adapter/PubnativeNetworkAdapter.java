package net.pubnative.mediation.adapter;

import android.content.Context;

import net.pubnative.mediation.model.PubnativeAdModel;

import java.util.Map;

public abstract class PubnativeNetworkAdapter
{
    protected PubnativeNetworkAdapterListener listener;
    protected Map                             data;

    public PubnativeNetworkAdapter(Map data)
    {
        this.data = data;
    }

    public void doRequest(Context context, PubnativeNetworkAdapterListener listener)
    {
        if (context != null)
        {
            if (listener != null)
            {
                this.listener = listener;
                this.invokeStart();
                this.request(context);
            }
            else
            {
                System.out.println("PubnativeNetworkAdapter.doRequest - listener not specified, dropping the call");
            }
        }
        else
        {
            System.out.println("PubnativeNetworkAdapter.doRequest - context not specified, dropping the call");
        }
    }

    public abstract void request(Context context);

    protected void invokeStart()
    {
        if (this.listener != null)
        {
            this.listener.onAdapterRequestStarted(this);
        }
    }

    protected void invokeLoaded(PubnativeAdModel ad)
    {
        if (this.listener != null)
        {
            this.listener.onAdapterRequestLoaded(this, ad);
        }
    }

    protected void invokeFailed(Exception exception)
    {
        if (this.listener != null)
        {
            this.listener.onAdapterRequestFailed(this, exception);
        }
    }
}
