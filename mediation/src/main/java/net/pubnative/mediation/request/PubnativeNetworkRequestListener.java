package net.pubnative.mediation.request;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public interface PubnativeNetworkRequestListener {

    /**
     * Invoked when ad request starts with valid params
     *
     * @param request Object used to make the ad request.
     */
    void onRequestStarted(PubnativeNetworkRequest request);

    /**
     * Invoked when ad request returns valid ads.
     *
     * @param request Object used to make the ad request.
     * @param ad      Loaded ad model.
     */
    void onRequestLoaded(PubnativeNetworkRequest request, PubnativeAdModel ad);

    /**
     * Invoked when ad request fails or when no ad is retrieved.
     *
     * @param request   Object used to make the ad request.
     * @param exception Exception with proper message of request failure.
     */
    void onRequestFailed(PubnativeNetworkRequest request, Exception exception);
}
