package net.pubnative.mediation.adapter;

import net.pubnative.mediation.model.PubnativeAdModel;

import java.util.List;

public interface PubnativeNetworkAdapterListener
{
    void onAdapterRequestStarted(PubnativeNetworkAdapter adapter);

    void onAdapterRequestLoaded(PubnativeNetworkAdapter adapter, List<PubnativeAdModel> ads);

    void onAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception);
}
