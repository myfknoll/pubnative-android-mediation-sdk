// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
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

package net.pubnative.library.network;

import android.util.Log;

/**
 * This class is used as a data holder for the PubnativeAPIRequestManager request configuration
 */
public class PubnativeAPIRequest {

    private static String   TAG       = PubnativeAPIRequest.class.getSimpleName();
    public static  int      TIME_OUT  = 3000;
    private        String   mUrl      = null;
    private        Listener mListener = null;
    //==============================================================================================
    // LISTENER
    //==============================================================================================

    /**
     * Listener for APIRequest callbacks
     */
    public interface Listener {

        /**
         * Called whenever we get a successful response
         *
         * @param response valid String with the server response
         */
        void onPubnativeAPIRequestResponse(String response);

        /**
         * Called whenever there is an error doing the request
         *
         * @param error Exception with the error
         */
        void onPubnativeAPIRequestError(Exception error);
    }
    //==============================================================================================
    // Static
    //==============================================================================================

    /**
     * This method will configure and send a request with the passed parameters
     *
     * @param requestURL Valid URL String
     * @param listener   valid Listener where receive the request callbacks
     */
    public static void send(String requestURL, Listener listener) {

        Log.v(TAG, "send: " + requestURL);
        PubnativeAPIRequestManager.sendRequest(new PubnativeAPIRequest(requestURL, listener));
    }

    //==============================================================================================
    // Public
    //==============================================================================================
    public PubnativeAPIRequest(String url, Listener listener) {

        mUrl = url;
        mListener = listener;
    }

    /**
     * Extract the URL of the request
     *
     * @return valid URL
     */
    public String getUrl() {

        Log.v(TAG, "getUrl");
        return mUrl;
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================
    public void invokeOnResponse(String response) {

        Log.v(TAG, "invokeOnResponse");
        if (mListener != null) {
            mListener.onPubnativeAPIRequestResponse(response);
        }
    }

    public void invokeOnError(Exception error) {

        Log.v(TAG, "invokeOnError: " + error);
        if (mListener != null) {
            mListener.onPubnativeAPIRequestError(error);
        }
    }
}
