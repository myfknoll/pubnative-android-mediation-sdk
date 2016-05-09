package net.pubnative.mediation.request;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.adapter.PubnativeNetworkHub;
import net.pubnative.mediation.adapter.PubnativeNetworkHubFactory;
import net.pubnative.mediation.adapter.network.PubnativeNetworkRequestAdapter;
import net.pubnative.mediation.config.PubnativePlacement;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.request.model.PubnativeAdTargetingModel;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class PubnativeNetworkWaterfall {

    private static String TAG = PubnativeNetworkRequest.class.getSimpleName();
    protected Context                          mContext;
    protected PubnativePlacement               mPlacement;
    protected PubnativeAdTargetingModel        mTargeting;

    //==============================================================================================
    // Tracking data
    //==============================================================================================

    /**
     * Sets the targeting model for the request
     *
     * @param targeting targeting model with extended targeting config
     */
    public void setTargeting(PubnativeAdTargetingModel targeting) {

        Log.v(TAG, "setTargeting");
        mTargeting = targeting;
    }

    //==============================================================================================
    // Private methods
    //==============================================================================================
    protected synchronized void start(Context context, String appToken, String placementName) {

        if (context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placementName)) {
            onLoadFail(PubnativeException.REQUEST_PARAMETERS_INVALID);
        } else if (PubnativeDeviceUtils.isNetworkAvailable(context)) {
            mContext = context;
            mPlacement = new PubnativePlacement();
            mPlacement.setTargeting(mTargeting);
            mPlacement.load(mContext, appToken, placementName, new PubnativePlacement.Listener() {

                @Override
                public void onPubnativePlacementReady(PubnativePlacement placement) {

                    checkDeliveryCaps();
                }

                @Override
                public void onPubnativePlacementLoadFail(PubnativePlacement placement, Exception exception) {

                    onLoadFail(exception);
                }
            });
        } else {
            onLoadFail(PubnativeException.REQUEST_NO_INTERNET);
        }
    }

    protected void checkDeliveryCaps() {

        Log.v(TAG, "checkDeliveryCaps");
        if (mPlacement.isDisabled()) {
            onLoadFail(PubnativeException.PLACEMENT_DISABLED);
        } else if (mPlacement.isFrequencyCapActive()) {
            onLoadFail(PubnativeException.PLACEMENT_FREQUENCY_CAP);
        } else if (mPlacement.isPacingCapActive()) {
            onPacingCapActive();
        } else {
            waterfall();
        }
    }

    protected void waterfall() {

        Log.v(TAG, "waterfall");
        mPlacement.next();
        PubnativeNetworkModel network = mPlacement.currentNetwork();
        if (network == null) {
            mPlacement.getInsightModel().sendRequestInsight();
            onLoadFail(PubnativeException.REQUEST_NO_FILL);
        } else {
            executeAdapter(network);
        }
    }

    protected void executeAdapter(PubnativeNetworkModel network) {

        PubnativeNetworkHub hub = PubnativeNetworkHubFactory.createHub(network);
        if (hub == null) {
            mPlacement.trackUnreachableNetwork(0, PubnativeException.ADAPTER_NOT_FOUND);
            waterfall();
        } else {
            Map<String, String> extras = new HashMap<String, String>();
            extras.put(PubnativeNetworkRequestAdapter.EXTRA_REQUEST_ID, mPlacement.getTrackingUUID());
            if (mTargeting != null) {
                extras.putAll(mTargeting.toDictionary());
            }
            onLoadFinish(hub, network, extras);
        }
    }

    //==============================================================================================
    // Abstract methods
    //==============================================================================================
    protected abstract void onPacingCapActive();

    protected abstract void onLoadFail(Exception exception);

    protected abstract void onLoadFinish(PubnativeNetworkHub hub, PubnativeNetworkModel network, Map extras);
}
