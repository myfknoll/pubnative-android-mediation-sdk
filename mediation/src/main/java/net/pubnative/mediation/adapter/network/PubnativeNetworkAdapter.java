package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Map;

public abstract class PubnativeNetworkAdapter {

    private static String TAG = PubnativeNetworkAdapter.class.getSimpleName();
    protected PubnativeNetworkAdapterRunnable mTimeoutRunnable;
    protected Map                             mData;
    protected Map<String, String>             mExtras;
    protected Handler                         mHandler;
    //==============================================================================================
    // Adapter Runnable
    //==============================================================================================

    protected class PubnativeNetworkAdapterRunnable implements Runnable {

        private final String TAG = PubnativeNetworkAdapterRunnable.class.getSimpleName();

        @Override
        public void run() {

            Log.v(TAG, "timeout");
            onTimeout();
        }
    }
    //==============================================================================================
    // PubnativeNetworkAdapter
    //==============================================================================================

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkAdapter(Map data) {

        mData = data;
    }

    /**
     * This method sets the extras for the adapter request
     *
     * @param extras valid extras Map
     */
    public void setExtras(Map<String, String> extras) {

        Log.v(TAG, "setExtras");
        mExtras = extras;
    }

    /**
     * Starts this adapter process
     *
     * @param context         valid context
     * @param timeoutInMillis timeout in milliseconds, if 0, then no timeout is set
     */
    public abstract void execute(Context context, int timeoutInMillis);

    protected abstract void onTimeout();

    //==============================================================================================
    // Timeout helpers
    //==============================================================================================
    protected void startTimeout(int timeoutInMillis) {

        Log.v(TAG, "startTimeout");
        if (timeoutInMillis > 0) {
            mTimeoutRunnable = new PubnativeNetworkAdapterRunnable();
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(mTimeoutRunnable, timeoutInMillis);
        }
    }

    protected void cancelTimeout() {

        Log.v(TAG, "cancelTimeout");
        if (mHandler != null && mTimeoutRunnable != null) {
            mHandler.removeCallbacks(mTimeoutRunnable);
            mHandler = null;
        }
    }
}
