package net.pubnative.mediation.adapter;

import java.util.ArrayList;

import net.pubnative.mediation.model.PubnativeAdModel;

public interface PubnativeNetworkAdapterListener
{
    public void onAdapterRequestStarted(PubnativeNetworkAdapter adapter);
    public void onAdapterRequestLoaded(PubnativeNetworkAdapter adapter, ArrayList<PubnativeAdModel> ads);
    public void onAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception);
}
