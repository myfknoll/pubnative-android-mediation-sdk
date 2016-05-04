package net.pubnative.mediation.request;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.AdvertisingIdClient;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterFactory;
import net.pubnative.mediation.adapter.PubnativeNetworkInterstitialAdapter;
import net.pubnative.mediation.config.PubnativePlacement;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;

public abstract class PubnativeNetworkInterstitial implements PubnativePlacement.Listener,
                                                              PubnativeNetworkInterstitialAdapter.LoadListener,
                                                              PubnativeNetworkInterstitialAdapter.AdListener {

    private static final String TAG = PubnativeNetworkInterstitial.class.getSimpleName();
    protected Listener                            mListener;
    protected Context                             mContext;
    protected PubnativePlacement                  mPlacement;
    protected boolean                             mIsLoading;
    protected boolean                             mIsShown;
    protected PubnativeNetworkInterstitialAdapter mAdapter;
    protected PubnativeInsightDataModel           mTrackingModel;
    protected long                                mStartTimestamp;

    /**
     * Interface for callbacks related to the interstitial view behaviour
     */
    public interface Listener {

        /**
         * Called whenever the interstitial finished loading an ad
         *
         * @param interstitial interstitial that finished the load
         */
        void onPubnativeNetworkInterstitialLoadFinish(PubnativeNetworkInterstitial interstitial);

        /**
         * Called whenever the interstitial failed loading an ad
         *
         * @param interstitial interstitial that failed the load
         * @param exception    exception with the description of the load error
         */
        void onPubnativeNetworkInterstitialLoadFail(PubnativeNetworkInterstitial interstitial, Exception exception);

        /**
         * Called when the interstitial was just shown on the screen
         *
         * @param interstitial interstitial that was shown in the screen
         */
        void onPubnativeNetworkInterstitialShow(PubnativeNetworkInterstitial interstitial);

        /**
         * Called when the interstitial impression was confrimed
         *
         * @param interstitial interstitial which impression was confirmed
         */
        void onPubnativeNetworkInterstitialImpressionConfirmed(PubnativeNetworkInterstitial interstitial);

        /**
         * Called whenever the interstitial was clicked by the user
         *
         * @param interstitial interstitial that was clicked
         */
        void onPubnativeNetworkInterstitialClick(PubnativeNetworkInterstitial interstitial);

        /**
         * Called whenever the interstitial was removed from the screen
         *
         * @param interstitial interstitial that was hidden
         */
        void onPubnativeNetworkInterstitialHide(PubnativeNetworkInterstitial interstitial);
    }
    //==============================================================================================
    // Public methods
    //==============================================================================================

    /**
     * Sets a callback listener for this interstitial object
     *
     * @param listener valid PubnativeNetworkInterstitial.Listener object
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    public void load(Context context, String appToken, String placement) {

        Log.v(TAG, "load");
        if (mListener == null) {
            Log.e(TAG, "load - Error: listener was not set, have you configured one using setListener()?");
        } else if (context == null ||
                   TextUtils.isEmpty(appToken) ||
                   TextUtils.isEmpty(placement)) {
            invokeLoadFail(PubnativeException.INTERSTITIAL_PARAMETERS_INVALID);
        } else if (mIsLoading) {
            invokeLoadFail(PubnativeException.INTERSTITIAL_LOADING);
        } else if (mIsShown) {
            invokeLoadFail(PubnativeException.INTERSTITIAL_SHOWN);
        } else {
            mContext = context;
            mPlacement = new PubnativePlacement();
            mPlacement.load(context, appToken, placement, this);
        }
    }

    public boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mAdapter != null) {
            result = mAdapter.isReady();
        }
        return result;
    }

    public void show() {

        Log.v(TAG, "show");
        if (mIsShown) {
            Log.v(TAG, "show - the ad is already shown");
        } else if (!isReady()) {
            Log.v(TAG, "show - the ad is still not loaded");
        } else {
            mAdapter.show();
        }
    }
    //==============================================================================================
    // Waterfall
    //==============================================================================================

    protected void startTracking() {

        mTrackingModel = new PubnativeInsightDataModel();
        mTrackingModel.placement_name = mPlacement.getName();
        mTrackingModel.delivery_segment_ids = mPlacement.getDeliveryRule().segment_ids;
        mTrackingModel.ad_format_code = mPlacement.getAdFormatCode();
        AdvertisingIdClient.getAdvertisingId(mContext, new AdvertisingIdClient.Listener() {

            @Override
            public void onAdvertisingIdClientFinish(AdvertisingIdClient.AdInfo adInfo) {

                if (adInfo != null && !adInfo.isLimitAdTrackingEnabled()) {
                    mTrackingModel.user_uid = adInfo.getId();
                }
                startRequest();
            }

            @Override
            public void onAdvertisingIdClientFail(Exception exception) {

                startRequest();
            }
        });
    }

    protected void startRequest() {

        if (mPlacement.isFrequencyCapActive()) {
            invokeLoadFail(PubnativeException.PLACEMENT_FREQUENCY_CAP);
        } else if (mPlacement.isPacingCapActive()) {
            if (mAdapter == null) {
                invokeLoadFail(PubnativeException.PLACEMENT_PACING_CAP);
            } else {
                invokeLoadFinish();
            }
        } else {
            waterfall();
        }
    }

    protected void waterfall() {

        Log.v(TAG, "next");
        mPlacement.next();
        PubnativeNetworkModel networkModel = mPlacement.currentNetwork();
        if (networkModel == null) {
            mPlacement.getInsightModel().sendRequestInsight();
            invokeLoadFail(PubnativeException.INTERSTITIAL_NO_FILL);
        } else {
            PubnativeNetworkInterstitialAdapter adapter = PubnativeNetworkAdapterFactory.createNetworkInterstitialAdapter(networkModel);
            if (adapter == null) {
                mPlacement.trackUnreachableNetwork(0, PubnativeException.INTERSTITIAL_ADAPTER_CREATION);
                waterfall();
            } else {
                mStartTimestamp = System.currentTimeMillis();
                // Add ML extras for adapter
                adapter.setLoadListener(this);
                adapter.execute(mContext, networkModel.timeout);
            }
        }
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoadFinish() {

        Log.v(TAG, "invokeLoadFinish");
        if (mListener != null) {
            mListener.onPubnativeNetworkInterstitialLoadFinish(this);
        }
    }

    protected void invokeLoadFail(Exception exception) {

        Log.v(TAG, "invokeLoadFail", exception);
        if (mListener != null) {
            mListener.onPubnativeNetworkInterstitialLoadFail(this, exception);
        }
    }

    protected void invokeShow() {

        Log.v(TAG, "invokeShow");
        if (mListener != null) {
            mListener.onPubnativeNetworkInterstitialShow(this);
        }
    }

    protected void invokeImpressionConfirmed() {

        Log.v(TAG, "invokeImpressionConfirmed");
        if (mListener != null) {
            mListener.onPubnativeNetworkInterstitialImpressionConfirmed(this);
        }
    }

    protected void invokeClick() {

        Log.v(TAG, "invokeClick");
        if (mListener != null) {
            mListener.onPubnativeNetworkInterstitialClick(this);
        }
    }

    protected void invokeHide() {

        Log.v(TAG, "invokeHide");
        if (mListener != null) {
            mListener.onPubnativeNetworkInterstitialHide(this);
        }
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativePlacement.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativePlacementReady(PubnativePlacement placement) {

        Log.v(TAG, "onPubnativePlacementReady");
        startTracking();
    }

    @Override
    public void onPubnativePlacementLoadFail(PubnativePlacement placement, Exception exception) {

        Log.v(TAG, "onPubnativePlacementLoadFail");
        invokeLoadFail(exception);
    }

    // PubnativeNetworkInterstitialAdapter.LoadListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterLoadFinish(PubnativeNetworkInterstitialAdapter interstitial) {

        Log.v(TAG, "onPubnativePlacementLoadFail");
        long responseTime = System.currentTimeMillis() - mStartTimestamp;
        mPlacement.trackSuccededNetwork(responseTime);
        invokeLoadFinish();
    }

    @Override
    public void onAdapterLoadFail(PubnativeNetworkInterstitialAdapter interstitial, Exception exception) {

        Log.v(TAG, "onPubnativePlacementLoadFail");
        long responseTime = System.currentTimeMillis() - mStartTimestamp;
        if (exception == PubnativeException.ADAPTER_TIMEOUT) {
            mPlacement.trackUnreachableNetwork(responseTime, exception);
        } else {
            mPlacement.trackAttemptedNetwork(responseTime, exception);
        }
        waterfall();
    }

    // PubnativeNetworkInterstitialAdapter.AdListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterShow(PubnativeNetworkInterstitialAdapter interstitial) {

        Log.v(TAG, "onPubnativePlacementLoadFail");
    }

    @Override
    public void onAdapterImpressionConfirmed(PubnativeNetworkInterstitialAdapter interstitial) {

        Log.v(TAG, "onPubnativePlacementLoadFail");
    }

    @Override
    public void onAdapterClick(PubnativeNetworkInterstitialAdapter interstitial) {

        Log.v(TAG, "onPubnativePlacementLoadFail");
    }

    @Override
    public void onAdapterHide(PubnativeNetworkInterstitialAdapter interstitial) {

        Log.v(TAG, "onPubnativePlacementLoadFail");
    }
}
