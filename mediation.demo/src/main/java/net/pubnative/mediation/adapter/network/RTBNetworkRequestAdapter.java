package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.network.PubnativeHttpRequest;

import java.util.Map;

public class RTBNetworkRequestAdapter extends PubnativeNetworkRequestAdapter
        implements PubnativeHttpRequest.Listener {

    private static final   String TAG      = RTBNetworkRequestAdapter.class.getSimpleName();
    protected static final String ENDPOINT = "endpoint";

    /**
     * Creates a new instance of PubnativeNetworkRequestAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public RTBNetworkRequestAdapter(Map data) {

        super(data);
    }

    @Override
    protected void request(Context context) {

        Log.v(TAG, "request");
        if (context == null || mData == null) {
            invokeFailed(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
        } else {
            String endpoint = (String) mData.get(ENDPOINT);
            if (endpoint == null) {
                invokeFailed(PubnativeException.ADAPTER_MISSING_DATA);
            } else {
                createRequest(context, endpoint);
            }
        }
    }

    protected void createRequest(Context context, String endpoint) {

        Log.v(TAG, "createRequest: " + endpoint);
        String json = "";
        // TODO: Use Google's openrtb repository to generate the POST JSON
        PubnativeHttpRequest request = new PubnativeHttpRequest();
        request.setPOSTString(json);
        request.start(context, endpoint, this);
    }

    @Override
    public void onPubnativeHttpRequestStart(PubnativeHttpRequest request) {

        Log.v(TAG, "onPubnativeHttpRequestStart");
    }

    @Override
    public void onPubnativeHttpRequestFinish(PubnativeHttpRequest request, String result) {
        Log.v(TAG, "onPubnativeHttpRequestStart");

        if(TextUtils.isEmpty(result)) {
            invokeFailed(PubnativeException.REQUEST_NO_FILL);
        } else {
            // TODO: Use Google's openrtb repository to generate the response JSON
            // TODO: Send the necessary data about the bid IN BACKGROUND so it doesn't affects the
            // TODO: adapter response time
            // TODO: Parse json into model
            // TODO:
            invokeLoaded(null);
        }
    }

    @Override
    public void onPubnativeHttpRequestFail(PubnativeHttpRequest request, Exception exception) {
        Log.v(TAG, "onPubnativeHttpRequestFail");
        invokeFailed(exception);
    }
}
