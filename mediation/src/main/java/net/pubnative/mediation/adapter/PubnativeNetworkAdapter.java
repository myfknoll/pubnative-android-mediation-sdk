package net.pubnative.mediation.adapter;

import android.content.Context;
import android.os.Handler;

import net.pubnative.mediation.model.PubnativeAdModel;

import java.util.Map;

public abstract class PubnativeNetworkAdapter
{
    protected PubnativeNetworkAdapterListener listener;
    protected PubnativeNetworkAdapterRunnable timeoutRunnable;
    protected Map                             data;
    protected Handler                         handler;

    protected class PubnativeNetworkAdapterRunnable implements Runnable
    {
        private PubnativeNetworkAdapter adapter;
        public PubnativeNetworkAdapterRunnable (PubnativeNetworkAdapter adapter)
        {
            this.adapter = adapter;
        }

        @Override
        public void run ()
        {
            // Invoke failed and avoid more callbacks by setting listener to null
            this.adapter.invokeFailed(new Exception("PubnativeNetworkAdapter.doRequest - adapter timeout"));
            this.adapter.listener = null;
        }
    }

    public PubnativeNetworkAdapter (Map data)
    {
        this.data = data;
    }

    public void doRequest (Context context, int timeoutInMillis, PubnativeNetworkAdapterListener listener)
    {
        if (context != null)
        {
            if (listener != null)
            {
                this.listener = listener;
                this.invokeStart();
                if (this.handler == null)
                {
                    this.handler = new Handler();
                }
                if(timeoutInMillis > 0)
                {
                    this.timeoutRunnable = new PubnativeNetworkAdapterRunnable(this);
                    this.handler.postDelayed(this.timeoutRunnable, timeoutInMillis);
                }
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

    public abstract void request (Context context);

    // Helpers

    protected void cancelTimeout()
    {
        if(this.handler != null && this.timeoutRunnable != null)
        {
            this.handler.removeCallbacks(this.timeoutRunnable);
        }
    }

    protected void invokeStart ()
    {
        if (this.listener != null)
        {
            this.listener.onAdapterRequestStarted(this);
        }
    }

    protected void invokeLoaded (PubnativeAdModel ad)
    {
        this.cancelTimeout();
        if (this.listener != null)
        {
            this.listener.onAdapterRequestLoaded(this, ad);
        }
    }

    protected void invokeFailed (Exception exception)
    {
        this.cancelTimeout();
        if (this.listener != null)
        {
            this.listener.onAdapterRequestFailed(this, exception);
        }
    }
}
