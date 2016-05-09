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

package net.pubnative.mediation.request;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import net.pubnative.mediation.adapter.PubnativeNetworkHub;
import net.pubnative.mediation.adapter.network.PubnativeNetworkRequestAdapter;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.Map;

public class PubnativeNetworkRequest extends PubnativeNetworkWaterfall
        implements PubnativeNetworkRequestAdapter.Listener {

    private static String TAG = PubnativeNetworkRequest.class.getSimpleName();
    protected PubnativeNetworkRequest.Listener mListener;
    protected boolean                          mIsRunning;
    protected Handler                          mHandler;
    protected PubnativeAdModel                 mAd;
    protected long                             mRequestStartTimestamp;
    //==============================================================================================
    // Listener
    //==============================================================================================

    /**
     * Interface for request callbacks that will inform about the request status
     */
    public interface Listener {

        /**
         * Invoked when ad request returns valid ads.
         *
         * @param request Object used to make the ad request.
         * @param ad      Loaded ad model.
         */
        void onPubnativeNetworkRequestLoaded(PubnativeNetworkRequest request, PubnativeAdModel ad);

        /**
         * Invoked when ad request fails or when no ad is retrieved.
         *
         * @param request   Object used to make the ad request.
         * @param exception Exception with proper message of request failure.
         */
        void onPubnativeNetworkRequestFailed(PubnativeNetworkRequest request, Exception exception);
    }
    //==============================================================================================
    // Pubic methods
    //==============================================================================================

    /**
     * Starts a new mAd request.
     *
     * @param context       valid Context object.
     * @param appToken      valid AppToken provided by Pubnative.
     * @param placementName valid placementId provided by Pubnative.
     * @param listener      valid Listener to keep track of request callbacks.
     */
    public synchronized void start(Context context, String appToken, final String placementName, PubnativeNetworkRequest.Listener listener) {

        Log.v(TAG, "start: -placement: " + placementName + " -appToken:" + appToken);
        if (listener == null) {
            Log.e(TAG, "start - Error: listener not specified, dropping the call");
        } else if (mIsRunning) {
            Log.e(TAG, "start - Error: request already running, dropping the call");
        } else {
            mIsRunning = true;
            mHandler = new Handler(Looper.getMainLooper());
            super.start(context, appToken, placementName);
        }
    }

    //==============================================================================================
    // PubnativeNetworkRequest
    //==============================================================================================
    @Override
    protected void onPacingCapActive() {

        if (mAd == null) {
            invokeFail(PubnativeException.PLACEMENT_PACING_CAP);
        } else {
            invokeLoad(mAd);
        }
    }

    @Override
    protected void onLoadFail(Exception exception) {

        invokeFail(exception);
    }

    @Override
    protected void onLoadFinish(PubnativeNetworkHub hub, PubnativeNetworkModel network, Map extras) {

        PubnativeNetworkRequestAdapter adapter = hub.getRequestAdapter();
        if (adapter == null) {
            mPlacement.trackUnreachableNetwork(0, PubnativeException.ADAPTER_TYPE_NOT_IMPLEMENTED);
            waterfall();
        } else {
            adapter.setExtras(extras);
            adapter.setListener(this);
            adapter.execute(mContext, network.timeout);
        }
    }
    //==============================================================================================
    // Callback helpers
    //==============================================================================================

    protected void invokeLoad(final PubnativeAdModel ad) {

        Log.v(TAG, "invokeLoad");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsRunning = false;
                if (mListener != null) {
                    mListener.onPubnativeNetworkRequestLoaded(PubnativeNetworkRequest.this, ad);
                }
                mListener = null;
            }
        });
    }

    protected void invokeFail(final Exception exception) {

        Log.v(TAG, "invokeFail: " + exception);
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsRunning = false;
                if (mListener != null) {
                    mListener.onPubnativeNetworkRequestFailed(PubnativeNetworkRequest.this, exception);
                }
                mListener = null;
            }
        });
    }
    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkAdapterListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onPubnativeNetworkAdapterRequestStarted(PubnativeNetworkRequestAdapter adapter) {

        Log.v(TAG, "onAdapterRequestStarted");
        mRequestStartTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onPubnativeNetworkAdapterRequestLoaded(PubnativeNetworkRequestAdapter adapter, PubnativeAdModel ad) {

        Log.v(TAG, "onAdapterRequestLoaded");
        long responseTime = System.currentTimeMillis() - mRequestStartTimestamp;
        if (ad == null) {
            mPlacement.trackAttemptedNetwork(responseTime, PubnativeException.REQUEST_NO_FILL);
            waterfall();
        } else {
            mAd = ad;
            // Track succeded network
            mPlacement.trackSuccededNetwork(responseTime);
            // Default tracking data
            mAd.setInsightModel(mPlacement.getInsightModel());
            // Finish the request
            invokeLoad(ad);
        }
    }

    @Override
    public void onPubnativeNetworkAdapterRequestFailed(PubnativeNetworkRequestAdapter adapter, Exception exception) {

        Log.e(TAG, "onAdapterRequestFailed: " + exception);
        // Waterfall to the next network
        long responseTime = System.currentTimeMillis() - mRequestStartTimestamp;
        mPlacement.trackUnreachableNetwork(responseTime, exception);
        waterfall();
    }
}
