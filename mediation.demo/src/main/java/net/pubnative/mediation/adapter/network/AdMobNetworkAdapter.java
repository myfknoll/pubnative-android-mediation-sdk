package net.pubnative.mediation.adapter.network;

import net.pubnative.mediation.adapter.PubnativeNetworkHub;

public class AdMobNetworkAdapter extends PubnativeNetworkHub {

    @Override
    public PubnativeNetworkRequestAdapter getRequestAdapter() {

        return new AdMobNetworkRequestAdapter(mNetworkData);
    }

    @Override
    public PubnativeNetworkInterstitialAdapter getInterstitialAdapter() {

        return new AdMobNetworkInterstitialAdapter(mNetworkData);
    }

    @Override
    public PubnativeNetworkFeedBannerAdapter getFeedBannerAdapter() {
        return null;
    }
}
