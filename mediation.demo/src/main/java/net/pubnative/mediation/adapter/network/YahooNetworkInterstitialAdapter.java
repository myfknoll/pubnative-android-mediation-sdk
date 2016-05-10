package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.util.Log;

import java.util.Map;

/**
 * Created by davidmartin on 10/05/16.
 */
public class YahooNetworkInterstitialAdapter extends PubnativeNetworkInterstitialAdapter {

    private static String TAG = YahooNetworkInterstitialAdapter.class.getSimpleName();

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public YahooNetworkInterstitialAdapter(Map data) {

        super(data);
    }

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        return false;
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
    }
}
