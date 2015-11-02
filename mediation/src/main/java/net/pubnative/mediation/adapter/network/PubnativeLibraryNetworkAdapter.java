package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;

import net.pubnative.library.PubnativeContract.Request;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.request.AdRequest;
import net.pubnative.library.request.AdRequestListener;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.request.model.PubnativeAdModel;
import net.pubnative.mediation.adapter.model.PubnativeLibraryAdModel;

import java.util.ArrayList;
import java.util.Map;

public class PubnativeLibraryNetworkAdapter extends PubnativeNetworkAdapter implements AdRequestListener {

    protected final static String APP_TOKEN_KEY = "app_token";

    public PubnativeLibraryNetworkAdapter(Map data) {
        super(data);
    }

    @Override
    public void request(Context context) {
        if (context != null && data != null) {
            String appToken = (String) data.get(APP_TOKEN_KEY);
            if (!TextUtils.isEmpty(appToken)) {
                createRequest(context, appToken);
            } else {
                invokeFailed(new Exception("Invalid app_token provided."));
            }
        } else {
            invokeFailed(new Exception("No app_token provided."));
        }
    }

    protected void createRequest(Context context, String appToken) {
        AdRequest request = new AdRequest(context);
        request.setParameter(Request.APP_TOKEN, appToken);
        request.start(AdRequest.Endpoint.NATIVE, this);
    }

    @Override
    public void onAdRequestStarted(AdRequest request) {
        // Do nothing
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads) {
        if (request == null || ads == null) {
            this.invokeFailed(new Exception("Invalid request object or ads found"));
        } else {
            PubnativeAdModel wrapAd = null;
            if (ads.size() > 0) {
                wrapAd = new PubnativeLibraryAdModel(ads.get(0));
            }
            this.invokeLoaded(wrapAd);
        }
    }

    @Override
    public void onAdRequestFailed(AdRequest request, Exception ex) {
        this.invokeFailed(ex);
    }
}
