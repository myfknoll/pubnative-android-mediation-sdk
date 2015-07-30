package net.pubnative.mediation.config;

import net.pubnative.mediation.model.PubnativeConfigModel;

public class PubnativeConfigRequest
{
    protected PubnativeConfigRequestListener listener;

    public void request(String app_token, PubnativeConfigRequestListener listener)
    {
        if(listener != null)
        {
            this.listener = listener;
            if(app_token != null && !app_token.isEmpty())
            {
                this.invokeStart();
                //Get the config file from wherever it's necessary
            }
            else   
            {
                this.invokeFailed(new IllegalArgumentException("PubnativeConfigRequest - app_token cannot be null or empty"));
            }
        }
        else
        {
            throw new RuntimeException("PubnativeConfigRequest.request - should specify a listener");
        }
    }
    
    protected void invokeStart()
    {
        if(this.listener != null)
        {
            this.listener.onRequestStarted(this);
        }
    }
    
    protected void invokeLoaded(PubnativeConfigModel config)
    {
        if(this.listener != null)
        {
            this.listener.onRequestLoaded(this, config);
        }
    }
    
    protected void invokeFailed(Exception exception)
    {
        if(this.listener != null)
        {
            this.listener.onRequestFailed(this, exception);
        }
    }
}
