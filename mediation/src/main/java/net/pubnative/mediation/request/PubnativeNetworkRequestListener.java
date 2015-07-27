package net.pubnative.mediation.request;

import java.util.ArrayList;

import net.pubnative.mediation.model.PubnativeAdModel;

public interface PubnativeNetworkRequestListener
{
    public void onRequestStarted(PubnativeNetworkRequest request);
    public void onRequestLoaded(PubnativeNetworkRequest request, ArrayList<PubnativeAdModel> ads);
    public void onRequestFailed(PubnativeNetworkRequest request, Exception exception);
}
