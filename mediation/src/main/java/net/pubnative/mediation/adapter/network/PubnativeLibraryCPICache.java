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
import android.util.Log;

import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;
import net.pubnative.library.request.model.api.PubnativeAPIV3DataModel;
import net.pubnative.mediation.adapter.model.PubnativeCPICacheItem;

import java.util.ArrayList;
import java.util.List;


public class PubnativeLibraryCPICache implements PubnativeRequest.Listener {

    private static final String                      TAG                         = PubnativeLibraryCPICache.class.getSimpleName();
    private static final String                      CACHE_SIZE_PARAMETER        = "5";
    private static final int                         CACHE_MIN_SIZE              = 2;
    private static final long                        CACHE_AD_VALIDITY_THRESHOLD = 1800000; // 30 Minutes
    protected static     List<PubnativeCPICacheItem> sAdQueue                    = new ArrayList<PubnativeCPICacheItem>();
    protected            String                      mAppToken                   = null;
    //==============================================================================================
    // SINGLETON
    //==============================================================================================
    private static PubnativeLibraryCPICache sInstance;

    private PubnativeLibraryCPICache() {}

    private static synchronized PubnativeLibraryCPICache getInstance() {
        if (sInstance == null) {
            sInstance = new PubnativeLibraryCPICache();
        }
        return sInstance;
    }

    //==============================================================================================
    // Public
    //==============================================================================================
    public static void init(Context context, String appToken) {
        getInstance().request(context, appToken);
    }

    public synchronized static PubnativeAdModel get(Context context) {

        PubnativeAdModel result;

        // DEQUEUE
        result = getInstance().dequeue();
        // CHECK IF WE NEED TO REQUEST MORE ADS
        if (sAdQueue.size() < CACHE_MIN_SIZE) {
            getInstance().request(context, getInstance().mAppToken);
        }

        return result;
    }



    //==============================================================================================
    // Private
    //==============================================================================================
    private void request(Context context, String appToken) {

        Log.v(TAG, "requestAd");
        if (context == null) {
            Log.w(TAG, "context is nil and required, dropping this call");
        } else if (TextUtils.isEmpty(appToken)) {
            Log.w(TAG, "appToken is nil or empty and required, dropping this call");
        } else {
            mAppToken = appToken;
            PubnativeRequest request = new PubnativeRequest();
            request.setParameter(PubnativeRequest.Parameters.APP_TOKEN, mAppToken);
            request.setParameter(PubnativeRequest.Parameters.AD_COUNT, CACHE_SIZE_PARAMETER);
            request.start(context, this);
        }
    }

    //==============================================================================================
    // QUEUE
    //==============================================================================================
    private void enqueue(List<PubnativeAdModel> ads) {

        // Refill cache with received response from server
        for (PubnativeAdModel ad : ads) {
            ad.fetch(); // Add CPI click cache
            ad.setUseClickCaching(true);
            enqueue(ad);
        }
    }

    private void enqueue(PubnativeAdModel ad) {

        // Refill cache with received response from server
        sAdQueue.add(new PubnativeCPICacheItem(ad));
    }

    private PubnativeAdModel dequeue() {

        Log.v(TAG, "dequeue");
        PubnativeAdModel result = null;

        if (sAdQueue.size() > 0) {
            PubnativeCPICacheItem item = sAdQueue.remove(0);
            long elapsedTime = System.currentTimeMillis() - item.timestamp;
            if (elapsedTime >= CACHE_AD_VALIDITY_THRESHOLD) {
                result = dequeue(); // iterate by recursion to get the first valid ad
            } else {
                result = item.ad;
            }
        }
        return result;
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PubnativeAdModel.Listener
    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {

        enqueue(ads);
    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {

        // Do nothing, we will retry next time
    }
}
