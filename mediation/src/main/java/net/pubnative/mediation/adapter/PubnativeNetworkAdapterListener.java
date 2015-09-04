package net.pubnative.mediation.adapter;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public interface PubnativeNetworkAdapterListener
{
    void onAdapterRequestStarted(PubnativeNetworkAdapter adapter);

    void onAdapterRequestLoaded(PubnativeNetworkAdapter adapter, PubnativeAdModel ad);

    void onAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception);
}
