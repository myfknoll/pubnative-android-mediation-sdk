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
import android.util.Log;

import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.request.AdRequest;
import net.pubnative.library.request.AdRequestListener;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.model.PubnativeLibraryAdModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.ArrayList;
import java.util.Map;

public class PubnativeLibraryNetworkAdapter extends PubnativeNetworkAdapter implements AdRequestListener {

    private static String TAG = PubnativeLibraryNetworkAdapter.class.getSimpleName();

    public PubnativeLibraryNetworkAdapter(Map data) {
        super(data);
    }

    @Override
    public void request(Context context) {

        Log.v(TAG, "request(Context context)");

        if (context != null && mData != null) {
            createRequest(context);
        } else {
            invokeFailed(new IllegalArgumentException("No context or adapter data provided."));
        }
    }

    protected void createRequest(Context context) {

        Log.v(TAG, "createRequest(Context context)");

        AdRequest request = new AdRequest(context);

        // We add all params
        for (Object key : mData.keySet()) {
            Object value = mData.get(key);
            request.setParameter((String)key, value.toString());
        }

        Map<String, String> extraMap = getExtras();

        // Add extras
        if(extraMap != null) {

            for (String key : extraMap.keySet()) {

                request.setParameter(key, extraMap.get(key));
            }
        }

        request.start(AdRequest.Endpoint.NATIVE, this);
    }

    @Override
    public void onAdRequestStarted(AdRequest request) {

        Log.v(TAG, "onAdRequestStarted(AdRequest request)");
        // Do nothing
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads) {

        Log.v(TAG, "onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads)");

        if (request == null ) {
            invokeFailed(new Exception("Pubnative - PubnativeLibraryNetwork error: invalid request object on response"));
        } else {
            PubnativeAdModel wrapAd = null;
            if (ads != null && ads.size() > 0) {
                wrapAd = new PubnativeLibraryAdModel(ads.get(0));
            }
            invokeLoaded(wrapAd);
        }
    }

    @Override
    public void onAdRequestFailed(AdRequest request, Exception ex) {

        Log.v(TAG, "onAdRequestFailed(AdRequest request, Exception ex)");

        invokeFailed(ex);
    }
}
