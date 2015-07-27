package net.pubnative.mediation.config;

import net.pubnative.mediation.model.PubnativeConfigModel;

public interface PubnativeConfigRequestListener
{
    public void onRequestStarted(PubnativeConfigRequest configRequest);
    public void onRequestLoaded(PubnativeConfigRequest configRequest, PubnativeConfigModel ads);
    public void onRequestFailed(PubnativeConfigRequest configRequest, Exception exception);
}
