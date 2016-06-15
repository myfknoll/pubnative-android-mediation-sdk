package net.pubnative.mediation.adapter.network;

import net.pubnative.mediation.adapter.PubnativeNetworkHub;

public class RTBNetworkAdapter extends PubnativeNetworkHub {

    @Override
    public PubnativeNetworkRequestAdapter getRequestAdapter() {

        return new RTBNetworkRequestAdapter(mNetworkData);
    }

    @Override
    public PubnativeNetworkInterstitialAdapter getInterstitialAdapter() {

        return null;
    }

    @Override
    public PubnativeNetworkFeedBannerAdapter getFeedBannerAdapter() {

        return null;
    }
}
