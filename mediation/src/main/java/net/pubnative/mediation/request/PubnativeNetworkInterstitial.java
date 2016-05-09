package net.pubnative.mediation.request;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.adapter.PubnativeNetworkHub;
import net.pubnative.mediation.adapter.network.PubnativeNetworkInterstitialAdapter;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class PubnativeNetworkInterstitial extends PubnativeNetworkWaterfall implements PubnativeNetworkInterstitialAdapter.LoadListener,
                                                                                PubnativeNetworkInterstitialAdapter.AdListener {

    private static final String TAG = PubnativeNetworkInterstitial.class.getSimpleName();
    protected Listener                            mListener;
    protected Handler                             mHandler;
    protected boolean                             mIsLoading;
    protected boolean                             mIsShown;
    protected PubnativeNetworkInterstitialAdapter mAdapter;
    protected long                                mStartTimestamp;

    /**
     * Interface for callbacks related to the interstitial view behaviour
     */
    public interface Listener {

        /**
         * Called whenever the interstitial finished loading an ad
         *w
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

    /**
     * Loads the interstitial ads before being shown
     */
    public synchronized void load(Context context, String appToken, String placement) {

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
            mHandler = new Handler(Looper.getMainLooper());
            super.start(context, appToken, placement);
        }
    }

    /**
     * Tells if the interstitial is ready to be shown
     */
    public synchronized boolean isReady() {

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mAdapter != null) {
            result = mAdapter.isReady();
        }
        return result;
    }

    /**
     * This method will show the interstitial if the ad is available
     */
    public synchronized void show() {

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
    // PubnativeNetworkWaterfall methods
    //==============================================================================================

    @Override
    protected void onPacingCapActive() {
        if (mAdapter == null) {
            invokeLoadFail(PubnativeException.PLACEMENT_PACING_CAP);
        } else {
            invokeLoadFinish();
        }
    }

    @Override
    protected void onLoadFail(Exception exception) {
        invokeLoadFail(exception);
    }

    @Override
    protected void onLoadFinish(PubnativeNetworkHub hub, PubnativeNetworkModel network, Map extras) {

        mAdapter = hub.getInterstitialAdapter();
        if (mAdapter == null) {
            mPlacement.trackUnreachableNetwork(0, PubnativeException.ADAPTER_TYPE_NOT_IMPLEMENTED);
            waterfall();
        } else {
            mStartTimestamp = System.currentTimeMillis();
            // Add ML extras for adapter
            mAdapter.setExtras(extras);
            mAdapter.setLoadListener(this);
            mAdapter.execute(mContext, network.timeout);
        }
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoadFinish() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                Log.v(TAG, "invokeLoadFinish");
                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialLoadFinish(PubnativeNetworkInterstitial.this);
                }
                mListener = null;
            }
        });
    }

    protected void invokeLoadFail(final Exception exception) {

        Log.v(TAG, "invokeLoadFail", exception);
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialLoadFail(PubnativeNetworkInterstitial.this, exception);
                }
                mListener = null;
            }
        });
    }

    protected void invokeShow() {

        Log.v(TAG, "invokeShow");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialShow(PubnativeNetworkInterstitial.this);
                }
            }
        });
    }

    protected void invokeImpressionConfirmed() {

        Log.v(TAG, "invokeImpressionConfirmed");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialImpressionConfirmed(PubnativeNetworkInterstitial.this);
                }
            }
        });
    }

    protected void invokeClick() {

        Log.v(TAG, "invokeClick");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialClick(PubnativeNetworkInterstitial.this);
                }
            }
        });
    }

    protected void invokeHide() {

        Log.v(TAG, "invokeHide");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkInterstitialHide(PubnativeNetworkInterstitial.this);
                }
            }
        });
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
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
