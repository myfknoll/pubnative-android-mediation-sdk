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

package net.pubnative.mediation.network;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class PubnativeHttpRequest {

    private static final String TAG = PubnativeHttpRequest.class.getSimpleName();
    //==============================================================================================
    // Properties
    //==============================================================================================
    // Request properties
    protected int      mTimeoutInMillis = 4000; // 4 seconds
    protected String   mPOSTString      = null;
    // Inner
    protected Listener mListener        = null;
    protected Handler  mHandler         = null;

    //==============================================================================================
    // Listener
    //==============================================================================================

    public interface Listener {

        /**
         * Called when the HttpRequest is about to start
         *
         * @param request request that is about to start
         */
        void onPubnativeHttpRequestStart(PubnativeHttpRequest request);

        /**
         * Called when the HttpRequest has just finished with a valid String response
         *
         * @param request request that have just finished
         * @param result  string with the given response from the server
         */
        void onPubnativeHttpRequestFinish(PubnativeHttpRequest request, String result);

        /**
         * Called when the HttpRequest fails, after this method the request will be stopped
         *
         * @param request   request that have just failed
         * @param exception exception with more info about the error
         */
        void onPubnativeHttpRequestFail(PubnativeHttpRequest request, Exception exception);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    /**
     * Sets timeout for connection and reading, if not specified default is 0 ms
     *
     * @param timeoutInMillis time in milliseconds
     */
    public void setTimeout(int timeoutInMillis) {

        Log.v(TAG, "setConnectionTimeout");
        mTimeoutInMillis = timeoutInMillis;
    }

    public void setPOSTString(String postString) {

        Log.v(TAG, "setPOSTString");
        mPOSTString = postString;
    }

    /**
     * This method will start a new request to the given URL
     *
     * @param context   valid Context object
     * @param urlString URL where the request will be done
     * @param listener  valid Listener for callbacks
     */
    public void start(Context context, final String urlString, Listener listener) {

        Log.v(TAG, "start: " + urlString);
        mListener = listener;
        mHandler = new Handler(Looper.getMainLooper());
        if (mListener == null) {
            Log.w(TAG, "Warning: null listener specified, performing request without callbacks");
        }
        if (TextUtils.isEmpty(urlString)) {
            invokeFail(new IllegalArgumentException("PubnativeHttpRequest - Error: null or empty url, dropping call"));
        } else {
            if(PubnativeDeviceUtils.isNetworkAvailable(context)) {
                invokeStart();
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        doRequest(urlString);
                    }
                }).start();
            } else {
                invokeFail(PubnativeException.REQUEST_NETWORK_NOT_FOUND);
            }
        }
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    protected void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    protected void doRequest(String urlString) {

        Log.v(TAG, "doRequest: " + urlString);
        HttpURLConnection connection = null;
        try {
            disableConnectionReuseIfNecessary();
            // 1. Create connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            // 2. Set connection properties
            connection.setDoInput(true);
            connection.setConnectTimeout(mTimeoutInMillis);
            if (TextUtils.isEmpty(mPOSTString)) {
                connection.setRequestMethod("GET");
            } else {
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                OutputStream connectionOutputStream = connection.getOutputStream();
                DataOutputStream wr = new DataOutputStream(connectionOutputStream);
                wr.writeBytes(mPOSTString);
                wr.flush();
                wr.close();
            }
            // 3. Do request
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                String resultString = stringFromInputString(inputStream);
                String response = "";
                while (inputStream.available() > 0) {
                    response += inputStream.read();
                }
                System.out.println("Anshuman " + response);
                if (resultString == null) {
                    /*String response = "";
                    while (inputStream.available() > 0) {
                        response += inputStream.read();
                    }
                    System.out.println("Anshuman " + response);*/
                    HashMap<String, String> errorData = new HashMap<String, String>();
                    errorData.put("serverResponse", response);
                    invokeFail(new PubnativeException(PubnativeException.ERROR_CODE.REQUEST_INVALID_RESPONSE, "Invalid response from server.", errorData));
                } else {
                    invokeFinish(resultString);
                }
            } else {
                HashMap<String, String> errorData = new HashMap<String, String>();
                errorData.put("statusCode", responseCode+"");
                errorData.put("errorString", stringFromInputString(connection.getErrorStream()));
                invokeFail(new PubnativeException(PubnativeException.ERROR_CODE.REQUEST_INVALID_STATUS_CODE, "Invalid status code.", errorData));
            }
        } catch (Exception exception) {
            invokeFail(exception);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    protected String stringFromInputString(InputStream inputStream) {

        Log.v(TAG, "stringFromInputString");
        String result = null;
        BufferedReader bufferReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            bufferReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "stringFromInputString - Error:" + e);
            stringBuilder = null;
        } finally {
            if (bufferReader != null) {
                try {
                    bufferReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "stringFromInputString - Error:" + e);
                }
            }
        }
        if (stringBuilder != null) {
            result = stringBuilder.toString();
        }
        return result;
    }

    //==============================================================================================
    // Listener helpers
    //==============================================================================================

    protected void invokeStart() {

        Log.v(TAG, "invokeStart");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeHttpRequestStart(PubnativeHttpRequest.this);
                }
            }
        });
    }

    protected void invokeFinish(final String result) {

        Log.v(TAG, "invokeFinish");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeHttpRequestFinish(PubnativeHttpRequest.this, result);
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

                if (mListener != null) {
                    mListener.onPubnativeHttpRequestFail(PubnativeHttpRequest.this, exception);
                }
                mListener = null;
            }
        });
    }
}