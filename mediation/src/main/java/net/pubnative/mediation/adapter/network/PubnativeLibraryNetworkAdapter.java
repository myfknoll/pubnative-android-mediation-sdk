// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

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
                invokeFailed(new IllegalArgumentException("Invalid app_token provided."));
            }
        } else {
            invokeFailed(new IllegalArgumentException("No context or adapter data provided."));
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
        if (request == null ) {
            this.invokeFailed(new Exception("Pubnative - PubnativeLibraryNetwork error: invalid request object on response"));
        } else {
            PubnativeAdModel wrapAd = null;
            if (ads != null && ads.size() > 0) {
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
