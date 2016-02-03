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

import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.request.AdRequest;
import net.pubnative.library.request.AdRequestListener;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.model.PubnativeLibraryAdModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.ArrayList;
import java.util.Map;

public class PubnativeLibraryNetworkAdapter extends PubnativeNetworkAdapter implements AdRequestListener {

    public PubnativeLibraryNetworkAdapter(Map data) {
        super(data);
    }

    @Override
    public void request(Context context, Map extras) {

        if (context != null && this.data != null) {

            createRequest(context, extras);

        } else {
            invokeFailed(new IllegalArgumentException("No context or adapter data provided."));
        }
    }

    protected void createRequest(Context context, Map<String, String> extras) {
        AdRequest request = new AdRequest(context);

        // We add all params
        for (Object key : this.data.keySet()) {
            Object value = this.data.get(key);
            request.setParameter((String)key, value.toString());
        }

        // Add extras
        if(extras != null) {

            for (String key : extras.keySet()) {

                request.setParameter(key, extras.get(key));
            }
        }

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
