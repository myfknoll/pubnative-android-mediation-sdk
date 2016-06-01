package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.util.Log;

import net.pubnative.mediation.adapter.model.AdMobNativeModel;

import java.util.Map;

public class AdMobNetworkRequestAdapter extends PubnativeNetworkRequestAdapter {

    private static final String TAG = AdMobNetworkRequestAdapter.class.getSimpleName();
    protected static final String   ADMOB_UNIT_ID            = "unit_id";

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public AdMobNetworkRequestAdapter(Map data) {

        super(data);
    }

    @Override
    protected void request(Context context) {
        Log.v(TAG, "request");
        invokeLoaded(new AdMobNativeModel());
    }
}
