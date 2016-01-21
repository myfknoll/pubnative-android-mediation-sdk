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

    protected PubnativeNetworkAdapterListener listener;
    protected PubnativeNetworkAdapterRunnable timeoutRunnable;
    protected Map                             data;
    protected Handler                         handler;

    protected class PubnativeNetworkAdapterRunnable implements Runnable {

        private PubnativeNetworkAdapter adapter;

        public PubnativeNetworkAdapterRunnable(PubnativeNetworkAdapter adapter) {

            this.adapter = adapter;
        }

        @Override
        public void run() {
            // Invoke failed and avoid more callbacks by setting listener to null
            this.adapter.invokeFailed(new TimeoutException("PubnativeNetworkAdapter.doRequest - adapter timeout"));
            this.adapter.listener = null;
        }
    }

    /**
     * Creates a new instance of PubnativeNetworkAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public PubnativeNetworkAdapter(Map data) {
        this.data = data;
    }

    /**
     * This method starts the adapter request setting up the configured timeout
     *
     * @param context         valid context
     * @param timeoutInMillis timeout in milliseconds. time to wait for an adapter to respond.
     * @param listener        lister to track the callbacks on adapter
     */
    public void doRequest(Context context, int timeoutInMillis, PubnativeNetworkAdapterListener listener) {

        if (listener != null) {

            this.listener = listener;

            if (context != null) {

                this.invokeStart();

                if (this.handler == null) {

                    this.handler = new Handler();
                }

                if (timeoutInMillis > 0) {

                    this.timeoutRunnable = new PubnativeNetworkAdapterRunnable(this);
                    this.handler.postDelayed(this.timeoutRunnable, timeoutInMillis);
                }

                this.request(context);

            } else {

                this.invokeFailed(new IllegalArgumentException("PubnativeNetworkAdapter.doRequest - null argument provided"));
            }

        } else {

            System.out.println("PubnativeNetworkAdapter.doRequest - context not specified, dropping the call");
        }
    }

    public abstract void request(Context context);

    // Helpers

    protected void cancelTimeout() {
        if (this.handler != null && this.timeoutRunnable != null) {
            this.handler.removeCallbacks(this.timeoutRunnable);
        }
    }

    protected void invokeStart() {
        if (this.listener != null) {
            this.listener.onAdapterRequestStarted(this);
        }
    }

    protected void invokeLoaded(PubnativeAdModel ad) {
        this.cancelTimeout();
        if (this.listener != null) {
            this.listener.onAdapterRequestLoaded(this, ad);
        }
        this.listener = null;
    }

    protected void invokeFailed(Exception exception) {
        this.cancelTimeout();
        if (this.listener != null) {
            this.listener.onAdapterRequestFailed(this, exception);
        }
        this.listener = null;
    }
}
