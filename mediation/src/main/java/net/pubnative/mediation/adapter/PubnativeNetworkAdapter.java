package net.pubnative.mediation.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Map;

/**
 * Created by davidmartin on 04/05/16.
 */
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
     * get extras map setted to the adapter when doing the request
     *
     * @return extras Map setted when doing the request
     */
    public Map<String, String> getExtras() {

        Log.v(TAG, "getExtras");
        return mExtras;
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
     * This method starts the adapter action
     *
     * @param context         valid context
     * @param timeoutInMillis timeout in milliseconds. time to wait for an adapter to respond.
     */
    public void execute(Context context, int timeoutInMillis) {

        Log.v(TAG, "execute");
        startTimeout(timeoutInMillis);
        start(context);
    }

    protected abstract void start(Context context);

    protected abstract void onTimeout();

    //----------------------------------------------------------------------------------------------
    // Timeout helpers
    //----------------------------------------------------------------------------------------------

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
