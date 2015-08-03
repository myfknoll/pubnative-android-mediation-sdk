package net.pubnative.mediation.request;

import net.pubnative.mediation.model.PubnativeAdModel;

import java.util.List;

public interface PubnativeNetworkRequestListener
{
    void onRequestStarted(PubnativeNetworkRequest request);

    void onRequestLoaded(PubnativeNetworkRequest request, List<PubnativeAdModel> ads);

    void onRequestFailed(PubnativeNetworkRequest request, Exception exception);
}
