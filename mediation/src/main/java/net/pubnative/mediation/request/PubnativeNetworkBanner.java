package net.pubnative.mediation.request;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.adapter.PubnativeNetworkHub;
import net.pubnative.mediation.adapter.network.PubnativeNetworkBannerAdapter;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class PubnativeNetworkBanner extends PubnativeNetworkWaterfall implements PubnativeNetworkBannerAdapter.AdListener, PubnativeNetworkBannerAdapter.LoadListener {

    public static final String TAG = PubnativeNetworkBanner.class.getSimpleName();
    protected Listener                      mListener;
    protected Handler                       mHandler;
    protected boolean                       mIsShown;
    protected boolean                       mIsLoading;
    protected PubnativeNetworkBannerAdapter mAdapter;

    public interface Listener {

        /**
         * Called whenever the banner finished loading an ad
         * w
         *
         * @param banner banner that finished the initialize
         */
        void onPubnativeNetworkBannerLoadFinish(PubnativeNetworkBanner banner);

        /**
         * Called whenever the banner failed loading an ad
         *
         * @param banner banner that failed the initialize
         * @param exception    exception with the description of the initialize error
         */
        void onPubnativeNetworkBannerLoadFail(PubnativeNetworkBanner banner, Exception exception);

        /**
         * Called when the banner was just shown on the screen
         *
         * @param banner banner that was shown in the screen
         */
        void onPubnativeNetworkBannerShow(PubnativeNetworkBanner banner);

        /**
         * Called when the banner impression was confrimed
         *
         * @param banner banner which impression was confirmed
         */
        void onPubnativeNetworkBannerImpressionConfirmed(PubnativeNetworkBanner banner);

        /**
         * Called whenever the banner was clicked by the user
         *
         * @param banner banner that was clicked
         */
        void onPubnativeNetworkBannerClick(PubnativeNetworkBanner banner);

        /**
         * Called whenever the banner was removed from the screen
         *
         * @param banner banner that was hidden
         */
        void onPubnativeNetworkBannerHide(PubnativeNetworkBanner banner);
    }

    public synchronized void load(Context context, String appToken, String placement) {

        Log.v(TAG, "initialize");
        if (mListener == null) {
            Log.e(TAG, "initialize - Error: listener was not set, have you configured one using setListener()?");
        } else if (context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placement)) {
            invokeLoadFail(PubnativeException.BANNER_PARAMETERS_INVALID);
        } else if (mIsLoading) {
            invokeLoadFail(PubnativeException.BANNER_LOADING);
        } else if (mIsShown) {
            invokeLoadFail(PubnativeException.BANNER_SHOWN);
        } else {
            mHandler = new Handler(Looper.getMainLooper());
            initialize(context, appToken, placement);
        }

    }

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

    public synchronized boolean isReady(){

        Log.v(TAG, "isReady");
        boolean result = false;
        if (mAdapter != null) {
            result = mAdapter.isReady();
        }
        return result;

    }

    @Override
    protected void onWaterfallLoadFinish(boolean pacingActive) {

    }

    @Override
    protected void onWaterfallError(Exception exception) {

    }

    @Override
    protected void onWaterfallNextNetwork(PubnativeNetworkHub hub, PubnativeNetworkModel network, Map extras) {

    }

    protected void invokeLoadFail(final Exception exception) {

        Log.v(TAG, "invokeLoadFail", exception);
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkBannerLoadFail(PubnativeNetworkBanner.this, exception);
                }
                mListener = null;
            }
        });

    }

    @Override
    public void onAdapterShow(PubnativeNetworkBannerAdapter banner) {

    }

    @Override
    public void onAdapterImpressionConfirmed(PubnativeNetworkBannerAdapter banner) {

    }

    @Override
    public void onAdapterClick(PubnativeNetworkBannerAdapter banner) {

    }

    @Override
    public void onAdapterHide(PubnativeNetworkBannerAdapter banner) {

    }

    @Override
    public void onAdapterLoadFinish(PubnativeNetworkBannerAdapter banner) {

    }

    @Override
    public void onAdapterLoadFail(PubnativeNetworkBannerAdapter banner, Exception exception) {

    }
}
