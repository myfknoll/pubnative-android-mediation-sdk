package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.util.Log;

import java.util.Map;

/**
 * Created by alvarlega on 11/05/16.
 */
public class PubnativeLibraryNetworkBannerAdapter extends PubnativeNetworkBannerAdapter {

    public static final String TAG = PubnativeLibraryNetworkBannerAdapter.class.getSimpleName();

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeLibraryNetworkBannerAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeNetworkInterstitialAdapter
    //==============================================================================================

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        //TODO: implement load method functional
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        //TODO: implement show method functional
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        //TODO: implement destroy method functional
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        //TODO: implement isReady method functional
        return false;
    }

    @Override
    public void hide() {

        Log.v(TAG, "hide");
        //TODO: implement isReady method functional
    }
}
