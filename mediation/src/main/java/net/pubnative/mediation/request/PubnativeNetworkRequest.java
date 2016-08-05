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
    protected boolean                          mIsLoading;
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
        } else if (mIsLoading) {
            Log.e(TAG, "start - Error: request already loading, dropping the call");
        } else {
            mIsLoading = true;
            mHandler = new Handler(Looper.getMainLooper());
            mListener = listener;
            initialize(context, appToken, placementName);
        }
    }

    //==============================================================================================
    // PubnativeNetworkRequest
    //==============================================================================================
    @Override
    protected void onWaterfallLoadFinish(boolean pacingActive) {

        if (pacingActive && mAd == null) {
            invokeFail(PubnativeException.PLACEMENT_PACING_CAP);
        } else if (pacingActive) {
            invokeLoad(mAd);
        } else {
            getNextNetwork();
        }
    }

    @Override
    protected void onWaterfallError(Exception exception) {

        invokeFail(exception);
    }

    @Override
    protected void onWaterfallNextNetwork(PubnativeNetworkHub hub, PubnativeNetworkModel network, Map extras, boolean isCached) {

        PubnativeNetworkRequestAdapter adapter = hub.getRequestAdapter();
        if (adapter == null) {
            mInsight.trackUnreachableNetwork(mPlacement.currentPriority(), 0, PubnativeException.ADAPTER_TYPE_NOT_IMPLEMENTED);
            getNextNetwork();
        } else {
            adapter.setCachingEnable(isCached);
            adapter.setExtras(extras);
            adapter.setListener(this);
            adapter.setTargeting(mTargeting);
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

                mIsLoading = false;
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

                mIsLoading = false;
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
            mInsight.trackAttemptedNetwork(mPlacement.currentPriority(), responseTime, PubnativeException.REQUEST_NO_FILL);
            getNextNetwork();
        } else {
            // Track succeded network
            mInsight.trackSuccededNetwork(mPlacement.currentPriority(), responseTime);
            mInsight.sendRequestInsight();
            // Default tracking data
            mAd = ad;
            mAd.setInsightModel(mInsight);
            invokeLoad(mAd);
        }
    }

    @Override
    public void onPubnativeNetworkAdapterRequestFailed(PubnativeNetworkRequestAdapter adapter, Exception exception) {

        Log.e(TAG, "onAdapterRequestFailed: " + exception);
        // Waterfall to the next network
        long responseTime = System.currentTimeMillis() - mRequestStartTimestamp;

        // Attempted when exception is not PubnativeException or ADAPTER_UNKNOWN_ERROR type;
        if(!exception.getClass().isAssignableFrom(PubnativeException.class)) {
            mInsight.trackAttemptedNetwork(mPlacement.currentPriority(), responseTime, exception);
        } else if (exception.equals(PubnativeException.ADAPTER_UNKNOWN_ERROR)) {
            mInsight.trackAttemptedNetwork(mPlacement.currentPriority(), responseTime, exception);
        } else {
            mInsight.trackUnreachableNetwork(mPlacement.currentPriority(), responseTime, exception);
        }
        getNextNetwork();
    }
}
