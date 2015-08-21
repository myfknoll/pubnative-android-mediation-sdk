package net.pubnative.mediation.request;

import net.pubnative.mediation.model.PubnativeAdModel;

public interface PubnativeNetworkRequestListener
{
    void onRequestStarted(PubnativeNetworkRequest request);

    void onRequestLoaded(PubnativeNetworkRequest request, PubnativeAdModel ad);

    void onRequestFailed(PubnativeNetworkRequest request, Exception exception);
}
