package net.pubnative.mediation.adapter;

import android.util.Log;

import java.util.Map;

/**
 * Created by davidmartin on 01/05/16.
 */
public abstract class PubnativeNetworkInterstitialAdapter extends PubnativeNetworkAdapter {

    private static final String TAG = PubnativeNetworkInterstitialAdapter.class.getSimpleName();
    protected AdListener   mAdListener;
    protected LoadListener mLoadListener;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkInterstitialAdapter(Map data) {

        super(data);
    }

    /**
     * Interface for callbacks related to the interstitial view behaviour
     */
    public interface LoadListener {

        /**
         * Called whenever the interstitial finished loading an ad
         *
         * @param interstitial interstitial that finished the load
         */
        void onAdapterLoadFinish(PubnativeNetworkInterstitialAdapter interstitial);

        /**
         * Called whenever the interstitial failed loading an ad
         *
         * @param interstitial interstitial that failed the load
         * @param exception    exception with the description of the load error
         */
        void onAdapterLoadFail(PubnativeNetworkInterstitialAdapter interstitial, Exception exception);
    }

    /**
     * Interface for callbacks related to the interstitial view behaviour
     */
    public interface AdListener {

        /**
         * Called when the interstitial was just shown on the screen
         *
         * @param interstitial interstitial that was shown in the screen
         */
        void onAdapterShow(PubnativeNetworkInterstitialAdapter interstitial);

        /**
         * Called when the interstitial impression was confrimed
         *
         * @param interstitial interstitial which impression was confirmed
         */
        void onAdapterImpressionConfirmed(PubnativeNetworkInterstitialAdapter interstitial);

        /**
         * Called whenever the interstitial was clicked by the user
         *
         * @param interstitial interstitial that was clicked
         */
        void onAdapterClick(PubnativeNetworkInterstitialAdapter interstitial);

        /**
         * Called whenever the interstitial was removed from the screen
         *
         * @param interstitial interstitial that was hidden
         */
        void onAdapterHide(PubnativeNetworkInterstitialAdapter interstitial);
    }
    //==============================================================================================
    // Overridable methods
    //==============================================================================================

    public void setLoadListener(PubnativeNetworkInterstitialAdapter.LoadListener listener) {

        Log.v(TAG, "setLoadListener");
        mLoadListener = listener;
    }

    public void setAdListener(PubnativeNetworkInterstitialAdapter.AdListener listener) {

        Log.v(TAG, "setAdListener");
        mAdListener = listener;
    }
    //==============================================================================================
    // Abstract
    //==============================================================================================

    /**
     * Tells if the interstitial is ready to be shown in the screen
     *
     * @return true if it's ready, false if it's not
     */
    public abstract boolean isReady();

    /**
     * Starts showing the interstitial for the adapted network
     */
    public abstract void show();

    /**
     * Destroys the current interstitial for the adapted network
     */
    public abstract void destroy();

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoadFinish() {

        Log.v(TAG, "invokeLoadFinish");
        if (mLoadListener != null) {
            mLoadListener.onAdapterLoadFinish(this);
        }
    }

    protected void invokeLoadFail(Exception exception) {

        Log.v(TAG, "invokeLoadFail", exception);
        if (mLoadListener != null) {
            mLoadListener.onAdapterLoadFail(this, exception);
        }
    }

    protected void invokeShow() {

        Log.v(TAG, "invokeShow");
        if (mAdListener != null) {
            mAdListener.onAdapterShow(this);
        }
    }

    protected void invokeImpressionConfirmed() {

        Log.v(TAG, "invokeImpressionConfirmed");
        if (mAdListener != null) {
            mAdListener.onAdapterImpressionConfirmed(this);
        }
    }

    protected void invokeClick() {

        Log.v(TAG, "invokeClick");
        if (mAdListener != null) {
            mAdListener.onAdapterClick(this);
        }
    }

    protected void invokeHide() {

        Log.v(TAG, "invokeHide");
        if (mAdListener != null) {
            mAdListener.onAdapterHide(this);
        }
    }
}
