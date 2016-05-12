package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.util.Log;

import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public abstract class PubnativeNetworkBannerAdapter extends PubnativeNetworkAdapter {

    private static final String TAG = PubnativeNetworkBannerAdapter.class.getSimpleName();

    protected LoadListener mLoadListener;
    protected AdListener mAdListener;

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkBannerAdapter(Map data) {

        super(data);
    }

    public interface LoadListener {

        /**
         * Called whenever the banner finished loading an ad
         *
         * @param banner banner that finished the initialize
         */
        void onAdapterLoadFinish(PubnativeNetworkBannerAdapter banner);

        /**
         * Called whenever the banner failed loading an ad
         *
         * @param banner banner that failed the initialize
         * @param exception    exception with the description of the initialize error
         */
        void onAdapterLoadFail(PubnativeNetworkBannerAdapter banner, Exception exception);

    }

    public interface AdListener {

        /**
         * Called when the banner was just shown on the screen
         *
         * @param banner banner that was shown in the screen
         */
        void onAdapterShow(PubnativeNetworkBannerAdapter banner);

        /**
         * Called when the banner impression was confrimed
         *
         * @param banner banner which impression was confirmed
         */
        void onAdapterImpressionConfirmed(PubnativeNetworkBannerAdapter banner);

        /**
         * Called whenever the banner was clicked by the user
         *
         * @param banner banner that was clicked
         */
        void onAdapterClick(PubnativeNetworkBannerAdapter banner);

        /**
         * Called whenever the banner was removed from the screen
         *
         * @param banner banner that was hidden
         */
        void onAdapterHide(PubnativeNetworkBannerAdapter banner);

    }

    public void setLoadListener(LoadListener loadListener) {

        Log.v(TAG, "setLoadListener");
        mLoadListener = loadListener;
    }

    public void setAdListener(AdListener adListener) {

        Log.v(TAG, "setAdListener");
        mAdListener = adListener;
    }

    @Override
    public void execute(Context context, int timeoutInMillis) {
        startTimeout(timeoutInMillis);
        load(context);
    }

    @Override
    protected void onTimeout() {
        invokeLoadFail(PubnativeException.ADAPTER_TIMEOUT);
    }

    public abstract void load(Context context);

    public abstract void show();

    public abstract void destroy();

    public abstract boolean isReady();

    protected void invokeLoadFail(Exception exception) {

        Log.v(TAG, "invokeLoadFail", exception);
        if (mLoadListener != null) {
            mLoadListener.onAdapterLoadFail(this, exception);
        }
        mLoadListener = null;
    }
}
