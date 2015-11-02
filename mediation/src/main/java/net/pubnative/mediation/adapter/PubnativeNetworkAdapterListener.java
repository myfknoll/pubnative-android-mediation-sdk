package net.pubnative.mediation.adapter;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public interface PubnativeNetworkAdapterListener {

    /**
     * Invoked when PubnativeNetworkAdapter starts the request with valid params.
     *
     * @param adapter Object used for requesting the ad.
     */
    void onAdapterRequestStarted(PubnativeNetworkAdapter adapter);

    /**
     * Invoked when ad was received successfully from the network.
     *
     * @param adapter Object used for requesting the ad.
     * @param ad      Loaded ad object.
     */
    void onAdapterRequestLoaded(PubnativeNetworkAdapter adapter, PubnativeAdModel ad);

    /**
     * Invoked when ad request is failed or when networks gives no ad.
     *
     * @param adapter   Object used for requesting the ad.
     * @param exception Exception raised with proper message to indicate request failure.
     */
    void onAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception);
}
