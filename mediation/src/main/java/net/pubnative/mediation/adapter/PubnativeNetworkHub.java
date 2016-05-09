package net.pubnative.mediation.adapter;

import net.pubnative.mediation.adapter.network.PubnativeNetworkInterstitialAdapter;
import net.pubnative.mediation.adapter.network.PubnativeNetworkRequestAdapter;

import java.util.Map;

public abstract class PubnativeNetworkHub {

    protected Map mNetworkData;

    /**
     * Sets the network data to be used by the adapter hub to create the different formats adapters
     *
     * @param data map with the network data required
     */
    public void setNetworkData(Map data) {

        mNetworkData = data;
    }

    /**
     * This method will return the network dependent adapter for requests
     *
     * @return valid PubnativeNetworkRequestAdapter
     */
    public abstract PubnativeNetworkRequestAdapter getRequestAdapter();

    /**
     * Gets the network dependent adapter for interstitials
     *
     * @return valid PubnativeNetworkInterstitialAdapter
     */
    public abstract PubnativeNetworkInterstitialAdapter getInterstitialAdapter();
}
