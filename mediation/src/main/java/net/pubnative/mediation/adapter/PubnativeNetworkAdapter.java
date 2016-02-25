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
package net.pubnative.mediation.adapter;

import android.content.Context;
import android.os.Handler;

import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.Map;
import java.util.concurrent.TimeoutException;

public abstract class PubnativeNetworkAdapter {

    protected PubnativeNetworkAdapterListener mListener;
    protected PubnativeNetworkAdapterRunnable mTimeoutRunnable;
    protected Map                             mData;
    protected Map<String, String>             mExtras;
    protected Handler                         mHandler;

    protected class PubnativeNetworkAdapterRunnable implements Runnable {

        private PubnativeNetworkAdapter mAdapter;

        public PubnativeNetworkAdapterRunnable(PubnativeNetworkAdapter adapter) {

            mAdapter = adapter;
        }

        @Override
        public void run() {
            // Invoke failed and avoid more callbacks by setting listener to null
            mAdapter.invokeFailed(new TimeoutException("PubnativeNetworkAdapter.doRequest - adapter timeout"));
            mAdapter.mListener = null;
        }
    }

    public Map<String, String> getExtras() {

        return mExtras;
    }

    /**
     * Creates a new instance of PubnativeNetworkAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkAdapter(Map data) {

        mData = data;
    }

    /**
     * This method starts the adapter request setting up the configured timeout
     *
     * @param context         valid context
     * @param timeoutInMillis timeout in milliseconds. time to wait for an adapter to respond.
     * @param listener        lister to track the callbacks on adapter
     */
    public void doRequest(Context context, int timeoutInMillis, Map extras, PubnativeNetworkAdapterListener listener) {

        if (listener != null) {
            mListener = listener;
            if (context != null) {
                invokeStart();
                if (mHandler == null) {
                    mHandler = new Handler();
                }
                if (timeoutInMillis > 0) {
                    mTimeoutRunnable = new PubnativeNetworkAdapterRunnable(this);
                    mHandler.postDelayed(mTimeoutRunnable, timeoutInMillis);
                }
                mExtras = extras;
                request(context);
            } else {
                invokeFailed(new IllegalArgumentException("PubnativeNetworkAdapter.doRequest - null argument provided"));
            }
        } else {
            System.out.println("PubnativeNetworkAdapter.doRequest - context not specified, dropping the call");
        }
    }

    public abstract void request(Context context);
    // Helpers

    protected void cancelTimeout() {

        if (mHandler != null && mTimeoutRunnable != null) {
            mHandler.removeCallbacks(mTimeoutRunnable);
        }
    }

    protected void invokeStart() {

        if (mListener != null) {
            mListener.onAdapterRequestStarted(this);
        }
    }

    protected void invokeLoaded(PubnativeAdModel ad) {

        cancelTimeout();
        if (mListener != null) {
            mListener.onAdapterRequestLoaded(this, ad);
        }
        mListener = null;
    }

    protected void invokeFailed(Exception exception) {

        cancelTimeout();
        if (mListener != null) {
            mListener.onAdapterRequestFailed(this, exception);
        }
        mListener = null;
    }
}
